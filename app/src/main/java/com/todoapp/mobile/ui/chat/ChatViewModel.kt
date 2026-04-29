package com.todoapp.mobile.ui.chat

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.InvalidStateException
import com.google.firebase.ai.type.PromptBlockedException
import com.google.firebase.ai.type.QuotaExceededException
import com.google.firebase.ai.type.ResponseStoppedException
import com.google.firebase.ai.type.ServerException
import com.google.firebase.ai.type.content
import com.todoapp.mobile.data.ai.ChatToolRegistry
import com.todoapp.mobile.data.ai.LocalIntentClassifier
import com.todoapp.mobile.data.network.NetworkMonitor
import com.todoapp.mobile.data.repository.DataStoreHelper
import com.todoapp.mobile.domain.model.ChatMessage
import com.todoapp.mobile.domain.repository.ChatRepository
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.navigation.NavigationEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val toolRegistry: ChatToolRegistry,
    private val chatRepository: ChatRepository,
    private val taskRepository: TaskRepository,
    private val networkMonitor: NetworkMonitor,
    private val dataStoreHelper: DataStoreHelper,
    private val intentClassifier: LocalIntentClassifier,
    private val clock: Clock,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatContract.UiState>(ChatContract.UiState.Loading)
    val uiState: StateFlow<ChatContract.UiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<ChatContract.UiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    private var chat: Chat = generativeModel.startChat()
    private var cooldownJob: Job? = null
    private var cooldownEndElapsedMs: Long = 0L
    private var draftSaveJob: Job? = null
    private var quotaHitCount: Int = 0
    private var refusalCount: Int = 0
    private var lastSendElapsedMs: Long = 0L

    init {
        viewModelScope.launch {
            val initial = chatRepository.getMessages()
            val savedDraft = dataStoreHelper.observeChatDraft().first()
            chat = generativeModel.startChat(history = initial.takeLast(MAX_HISTORY_TURNS).toFirebaseHistory())
            _uiState.value = ChatContract.UiState.Ready(messages = initial, draft = savedDraft)
            chatRepository.observeMessages().collect { messages ->
                _uiState.update { current ->
                    when (current) {
                        is ChatContract.UiState.Ready -> current.copy(messages = messages)
                        else -> current
                    }
                }
                _uiEffect.trySend(ChatContract.UiEffect.ScrollToBottom)
            }
        }
    }

    fun onAction(action: ChatContract.UiAction) {
        when (action) {
            is ChatContract.UiAction.OnDraftChanged -> updateDraft(action.text)
            ChatContract.UiAction.OnSendClicked -> sendCurrentDraft()
            ChatContract.UiAction.OnClearHistory -> clearHistory()
            ChatContract.UiAction.OnRetry -> retry()
            ChatContract.UiAction.OnDismissError -> dismissError()
        }
    }

    private fun updateDraft(text: String) {
        val capped = if (text.length > MAX_DRAFT_LENGTH) text.take(MAX_DRAFT_LENGTH) else text
        _uiState.update { current ->
            when (current) {
                is ChatContract.UiState.Ready -> current.copy(draft = capped)
                else -> current
            }
        }
        scheduleDraftSave(capped)
    }

    private fun scheduleDraftSave(value: String) {
        draftSaveJob?.cancel()
        draftSaveJob = viewModelScope.launch {
            delay(DRAFT_SAVE_DEBOUNCE_MS)
            dataStoreHelper.setChatDraft(value)
        }
    }

    private fun dismissError() {
        cooldownJob?.cancel()
        _uiState.update { current ->
            when (current) {
                is ChatContract.UiState.Ready -> current.copy(
                    error = null,
                    rateLimitCooldownSecondsRemaining = 0,
                    lastFailedPrompt = null,
                )
                else -> current
            }
        }
    }

    private fun sendCurrentDraft() {
        val current = _uiState.value as? ChatContract.UiState.Ready ?: return
        val prompt = current.draft.trim()
        if (prompt.isEmpty() || current.isThinking) return
        val now = SystemClock.elapsedRealtime()
        if (now - lastSendElapsedMs < SEND_COOLDOWN_MS) {
            Timber.tag(LOG_TAG).d("send cooldown active, ignoring tap")
            return
        }
        lastSendElapsedMs = now
        if (!networkMonitor.isOnline.value) {
            _uiState.value = current.copy(draft = "")
            setError(ChatContract.ChatError.OFFLINE, lastFailedPrompt = prompt)
            return
        }
        _uiState.value = current.copy(
            draft = "",
            isThinking = true,
            error = null,
            lastFailedPrompt = null,
            rateLimitCooldownSecondsRemaining = 0,
        )
        cooldownJob?.cancel()
        draftSaveJob?.cancel()
        viewModelScope.launch {
            dataStoreHelper.setChatDraft("")
            chatRepository.appendUserMessage(prompt)
            val localMatch = intentClassifier.tryAnswer(prompt)
            if (localMatch != null) {
                Timber.tag(METRICS_TAG).i("local_intent_hit:%s", localMatch.intent.name)
                chatRepository.appendAssistantMessage(localMatch.response)
                _uiState.update { latest ->
                    when (latest) {
                        is ChatContract.UiState.Ready -> latest.copy(
                            isThinking = false,
                            toolInFlight = null,
                        )
                        else -> latest
                    }
                }
                return@launch
            }
            executeSendInternal(prompt)
        }
    }

    private fun retry() {
        val current = _uiState.value as? ChatContract.UiState.Ready ?: return
        val prompt = current.lastFailedPrompt ?: return
        if (current.isThinking) return
        if (current.error == ChatContract.ChatError.RATE_LIMITED &&
            current.rateLimitCooldownSecondsRemaining > 0
        ) {
            return
        }
        if (!networkMonitor.isOnline.value) {
            setError(ChatContract.ChatError.OFFLINE, lastFailedPrompt = prompt)
            return
        }
        cooldownJob?.cancel()
        _uiState.value = current.copy(
            isThinking = true,
            error = null,
            rateLimitCooldownSecondsRemaining = 0,
            lastFailedPrompt = null,
        )
        viewModelScope.launch {
            executeSendInternal(prompt)
        }
    }

    private suspend fun executeSendInternal(prompt: String) {
        val enriched = buildContextPreamble() + "\n\n" + prompt
        runFunctionCallingLoop(enriched, originalPrompt = prompt)
        _uiState.update { latest ->
            when (latest) {
                is ChatContract.UiState.Ready -> latest.copy(isThinking = false, toolInFlight = null)
                else -> latest
            }
        }
    }

    private suspend fun buildContextPreamble(): String {
        val today = LocalDate.now(clock)
        val tomorrow = today.plusDays(1)
        val tasksToday = taskRepository.observeTasksByDate(today, includeRecurringInstances = true).first()
        val tasksTomorrow = taskRepository.observeTasksByDate(tomorrow, includeRecurringInstances = true).first()
        val overdue = taskRepository.observeOverdueTasks(today).first()
        val completedThisWeek = taskRepository.countCompletedTasksInAWeek(today, includeRecurring = true).first()
        return buildString {
            append("[Context: Today is ").append(today).append(" (").append(today.dayOfWeek).append(").\n")
            if (tasksToday.isEmpty()) {
                append("Today: no tasks.\n")
            } else {
                append("Today: ").append(tasksToday.size).append(" tasks:\n")
                tasksToday.take(MAX_PREAMBLE_TASKS).forEach { task ->
                    append("  #").append(task.id)
                    append(" \"").append(task.title).append("\" ")
                    append(task.timeStart).append("-").append(task.timeEnd)
                    append(" [").append(if (task.isCompleted) "completed" else "pending").append("]\n")
                }
                if (tasksToday.size > MAX_PREAMBLE_TASKS) {
                    append("  (and ").append(tasksToday.size - MAX_PREAMBLE_TASKS)
                    append(" more — call getTodaysTasks for full list)\n")
                }
            }
            append("Tomorrow: ").append(tasksTomorrow.size).append(" tasks scheduled.\n")
            append("Overdue: ").append(overdue.size).append(" tasks past due.\n")
            append("Completed this week: ").append(completedThisWeek).append(" tasks.\n")
            append("]")
        }
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod")
    private suspend fun runFunctionCallingLoop(initialUserText: String, originalPrompt: String) {
        val memo = mutableMapOf<String, FunctionResponsePart>()
        var emptyTextRetried = false
        var nextContent: Any = initialUserText
        var iteration = 0
        val turnStartNs = System.nanoTime()
        try {
            while (iteration < MAX_TOOL_ITERATIONS) {
                val response = sendMessageWithRetry(nextContent)
                val calls = response.functionCalls
                if (calls.isEmpty()) {
                    val text = response.text?.trim().orEmpty()
                    if (text.isNotBlank()) {
                        chatRepository.appendAssistantMessage(text)
                        logTurnSummary(iteration, turnStartNs, text)
                        return
                    }
                    if (!emptyTextRetried) {
                        emptyTextRetried = true
                        Timber.tag(LOG_TAG).w("Empty response, retrying with nudge once")
                        nextContent = nudgeForLanguageOf(originalPrompt)
                        iteration++
                        continue
                    }
                    setError(ChatContract.ChatError.GENERIC, lastFailedPrompt = originalPrompt)
                    return
                }
                Timber.tag(LOG_TAG).d("Iteration %d → %d tool call(s)", iteration, calls.size)
                _uiState.update { current ->
                    when (current) {
                        is ChatContract.UiState.Ready -> current.copy(toolInFlight = calls.first().name)
                        else -> current
                    }
                }
                val responseParts = calls.map { call ->
                    val key = "${call.name}|${call.args.toSortedMap()}"
                    memo.getOrPut(key) {
                        val toolStartNs = System.nanoTime()
                        val result = toolRegistry.execute(call)
                        val toolMs = (System.nanoTime() - toolStartNs) / NS_PER_MS
                        Timber.tag(METRICS_TAG).i("tool=%s ms=%d", call.name, toolMs)
                        result
                    }
                }
                _uiState.update { current ->
                    when (current) {
                        is ChatContract.UiState.Ready -> current.copy(toolInFlight = null)
                        else -> current
                    }
                }
                nextContent = content("function") {
                    responseParts.forEach { part(it) }
                }
                iteration++
            }
            setError(ChatContract.ChatError.LOOP_OVERFLOW, lastFailedPrompt = originalPrompt)
        } catch (e: QuotaExceededException) {
            quotaHitCount++
            Timber.tag(METRICS_TAG).w("quotaHit count=%d", quotaHitCount)
            Timber.tag(LOG_TAG).w(e, "Quota exceeded (free tier limit hit)")
            setError(
                ChatContract.ChatError.RATE_LIMITED,
                lastFailedPrompt = originalPrompt,
                retryAfterSeconds = parseRetryAfterSeconds(e),
            )
        } catch (e: PromptBlockedException) {
            Timber.tag(LOG_TAG).w(e, "Prompt blocked by safety filter")
            setError(ChatContract.ChatError.BLOCKED, lastFailedPrompt = null)
        } catch (e: ResponseStoppedException) {
            Timber.tag(LOG_TAG).w(e, "Response stopped by safety filter")
            setError(ChatContract.ChatError.BLOCKED, lastFailedPrompt = null)
        } catch (e: InvalidStateException) {
            Timber.tag(LOG_TAG).w(e, "Invalid chat state")
            setError(ChatContract.ChatError.GENERIC, lastFailedPrompt = originalPrompt)
        } catch (e: ServerException) {
            if (e.isRateLimited()) {
                Timber.tag(LOG_TAG).w(e, "Rate limited (free tier quota hit)")
                setError(
                    ChatContract.ChatError.RATE_LIMITED,
                    lastFailedPrompt = originalPrompt,
                    retryAfterSeconds = parseRetryAfterSeconds(e),
                )
            } else {
                Timber.tag(LOG_TAG).w(e, "Server error")
                setError(ChatContract.ChatError.GENERIC, lastFailedPrompt = originalPrompt)
            }
        } catch (e: IOException) {
            Timber.tag(LOG_TAG).w(e, "Network error")
            setError(ChatContract.ChatError.OFFLINE, lastFailedPrompt = originalPrompt)
        } catch (e: Exception) {
            Timber.tag(LOG_TAG).w(e, "Unexpected chat error")
            setError(ChatContract.ChatError.GENERIC, lastFailedPrompt = originalPrompt)
        }
    }

    private fun nudgeForLanguageOf(prompt: String): String = if (TR_CHAR_REGEX.containsMatchIn(prompt)) EMPTY_RESPONSE_NUDGE_TR else EMPTY_RESPONSE_NUDGE_EN

    private fun logTurnSummary(roundTripCount: Int, turnStartNs: Long, replyText: String) {
        val totalMs = (System.nanoTime() - turnStartNs) / NS_PER_MS
        val refused = REFUSAL_PREFIXES.any { replyText.startsWith(it, ignoreCase = true) }
        if (refused) refusalCount++
        Timber.tag(METRICS_TAG).i(
            "turn rt=%d ms=%d refused=%s refusalTotal=%d quotaTotal=%d",
            roundTripCount + 1,
            totalMs,
            refused,
            refusalCount,
            quotaHitCount,
        )
    }

    private fun setError(
        error: ChatContract.ChatError,
        lastFailedPrompt: String?,
        retryAfterSeconds: Int? = null,
    ) {
        cooldownJob?.cancel()
        val cooldownSec = if (error == ChatContract.ChatError.RATE_LIMITED) {
            (retryAfterSeconds ?: RATE_LIMIT_COOLDOWN_SECONDS)
                .coerceIn(MIN_DYNAMIC_COOLDOWN_SECONDS, MAX_DYNAMIC_COOLDOWN_SECONDS)
        } else {
            0
        }
        if (error == ChatContract.ChatError.RATE_LIMITED) {
            cooldownEndElapsedMs = SystemClock.elapsedRealtime() + cooldownSec * MS_PER_SEC
            Timber.tag(METRICS_TAG).i("cooldown set sec=%d (server=%s)", cooldownSec, retryAfterSeconds)
        }
        _uiState.update { current ->
            when (current) {
                is ChatContract.UiState.Ready -> current.copy(
                    error = error,
                    lastFailedPrompt = lastFailedPrompt,
                    rateLimitCooldownSecondsRemaining = cooldownSec,
                    toolInFlight = null,
                )
                else -> current
            }
        }
        if (error == ChatContract.ChatError.RATE_LIMITED) {
            startCooldownTicker()
        }
    }

    private fun parseRetryAfterSeconds(throwable: Throwable): Int? {
        val haystack = "${throwable.message.orEmpty()} ${throwable.cause?.message.orEmpty()}"
        val match = RETRY_AFTER_REGEX.find(haystack) ?: return null
        val seconds = match.groupValues[1].toDoubleOrNull() ?: return null
        if (seconds <= 0) return null
        return seconds.toInt() + RETRY_AFTER_PADDING_SECONDS
    }

    private fun startCooldownTicker() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            while (true) {
                val remainingMs = cooldownEndElapsedMs - SystemClock.elapsedRealtime()
                val remainingSec = ((remainingMs + MS_PER_SEC - 1) / MS_PER_SEC).toInt().coerceAtLeast(0)
                _uiState.update { current ->
                    when (current) {
                        is ChatContract.UiState.Ready -> current.copy(rateLimitCooldownSecondsRemaining = remainingSec)
                        else -> current
                    }
                }
                if (remainingSec <= 0) break
                delay(COOLDOWN_TICK_MS)
            }
        }
    }

    private fun clearHistory() {
        cooldownJob?.cancel()
        viewModelScope.launch {
            chatRepository.clear()
            chat = generativeModel.startChat()
            _uiState.update { current ->
                when (current) {
                    is ChatContract.UiState.Ready -> current.copy(
                        error = null,
                        isThinking = false,
                        lastFailedPrompt = null,
                        rateLimitCooldownSecondsRemaining = 0,
                        toolInFlight = null,
                    )
                    else -> current
                }
            }
        }
    }

    private fun List<ChatMessage>.toFirebaseHistory(): List<Content> = map { message ->
        val role = when (message.role) {
            ChatMessage.Role.USER -> ROLE_USER
            ChatMessage.Role.ASSISTANT -> ROLE_MODEL
        }
        content(role) { text(message.content) }
    }

    private inline fun MutableStateFlow<ChatContract.UiState>.update(
        transform: (ChatContract.UiState) -> ChatContract.UiState,
    ) {
        value = transform(value)
    }

    private fun ServerException.isRateLimited(): Boolean {
        val haystack = "${message.orEmpty()} ${cause?.message.orEmpty()}"
        return RATE_LIMIT_MARKERS.any { haystack.contains(it, ignoreCase = true) }
    }

    @Suppress("ThrowsCount")
    private suspend fun sendMessageWithRetry(payload: Any): GenerateContentResponse {
        var attempt = 0
        while (true) {
            try {
                return when (payload) {
                    is String -> chat.sendMessage(payload)
                    is Content -> chat.sendMessage(payload)
                    else -> error("Unexpected payload type: ${payload::class}")
                }
            } catch (e: QuotaExceededException) {
                throw e
            } catch (e: ServerException) {
                if (e.isRateLimited() || attempt >= MAX_RETRIES) throw e
                Timber.tag(LOG_TAG).w(e, "Server error on attempt %d, retrying", attempt + 1)
                delay(BACKOFF_DELAYS_MS[attempt])
                attempt++
            } catch (e: IOException) {
                if (attempt >= MAX_RETRIES) throw e
                Timber.tag(LOG_TAG).w(e, "Network error on attempt %d, retrying", attempt + 1)
                delay(BACKOFF_DELAYS_MS[attempt])
                attempt++
            }
        }
    }

    companion object {
        private const val MAX_TOOL_ITERATIONS = 5
        private const val MAX_RETRIES = 2
        private const val MAX_HISTORY_TURNS = 10
        private const val MAX_PREAMBLE_TASKS = 20
        const val MAX_DRAFT_LENGTH = 100
        private const val SEND_COOLDOWN_MS = 3_000L
        private const val RATE_LIMIT_COOLDOWN_SECONDS = 30
        private const val MIN_DYNAMIC_COOLDOWN_SECONDS = 5
        private const val MAX_DYNAMIC_COOLDOWN_SECONDS = 300
        private const val RETRY_AFTER_PADDING_SECONDS = 1
        private const val COOLDOWN_TICK_MS = 1_000L
        private const val MS_PER_SEC = 1_000L
        private val RETRY_AFTER_REGEX = Regex("""[Rr]etry in (\d+(?:\.\d+)?)s""")
        private const val DRAFT_SAVE_DEBOUNCE_MS = 500L
        private const val ROLE_USER = "user"
        private const val ROLE_MODEL = "model"
        private const val LOG_TAG = "ChatViewModel"
        private const val METRICS_TAG = "DoneBotMetrics"
        private const val NS_PER_MS = 1_000_000L
        private const val EMPTY_RESPONSE_NUDGE_EN = "Please answer the previous question briefly."
        private const val EMPTY_RESPONSE_NUDGE_TR = "Lütfen önceki soruyu kısaca yanıtla."
        private val TR_CHAR_REGEX = Regex("[ıİşŞğĞüÜöÖçÇ]")
        private val REFUSAL_PREFIXES = listOf(
            "Sorry, I can only help",
            "Üzgünüm, sadece bu uygulamadaki",
        )
        private val RATE_LIMIT_MARKERS = listOf(
            "429",
            "RESOURCE_EXHAUSTED",
            "quota",
            "rate limit",
        )
        private val BACKOFF_DELAYS_MS = longArrayOf(1_000L, 3_000L)
    }
}
