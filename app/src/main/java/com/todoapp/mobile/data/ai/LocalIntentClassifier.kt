package com.todoapp.mobile.data.ai

import android.content.Context
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.repository.TaskRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalIntentClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskRepository: TaskRepository,
    private val clock: Clock,
) {
    enum class Intent { TODAY_TASKS, OVERDUE_TASKS, WEEKLY_PROGRESS, GREETING }

    data class Match(val intent: Intent, val response: String)

    suspend fun tryAnswer(prompt: String): Match? {
        val normalized = prompt.trim().lowercase()
        if (normalized.isEmpty()) return null
        if (normalized.length > MAX_INTENT_LENGTH) return null
        if (containsMutationVerb(normalized)) return null

        return when {
            GREETING.matches(normalized) -> Match(
                Intent.GREETING,
                context.getString(R.string.chat_local_greeting),
            )
            matchesToday(normalized) -> Match(
                Intent.TODAY_TASKS,
                buildTodayResponse(),
            )
            matchesOverdue(normalized) -> Match(
                Intent.OVERDUE_TASKS,
                buildOverdueResponse(),
            )
            matchesWeekly(normalized) -> Match(
                Intent.WEEKLY_PROGRESS,
                buildWeeklyResponse(),
            )
            else -> null
        }
    }

    private fun matchesToday(text: String): Boolean =
        TODAY_TR_ANCHOR.containsMatchIn(text) || TODAY_EN_ANCHOR.containsMatchIn(text)

    private fun matchesOverdue(text: String): Boolean =
        OVERDUE_KEYWORDS.containsMatchIn(text)

    private fun matchesWeekly(text: String): Boolean =
        WEEKLY_TR_ANCHOR.containsMatchIn(text) || WEEKLY_EN_ANCHOR.containsMatchIn(text)

    private suspend fun buildTodayResponse(): String {
        val today = LocalDate.now(clock)
        val count = taskRepository.observeTasksByDate(today, includeRecurringInstances = true).first().size
        return if (count == 0) {
            context.getString(R.string.chat_local_today_empty)
        } else {
            context.getString(R.string.chat_local_today_count_format, count)
        }
    }

    private suspend fun buildOverdueResponse(): String {
        val today = LocalDate.now(clock)
        val count = taskRepository.observeOverdueTasks(today).first().size
        return if (count == 0) {
            context.getString(R.string.chat_local_overdue_empty)
        } else {
            context.getString(R.string.chat_local_overdue_count_format, count)
        }
    }

    private suspend fun buildWeeklyResponse(): String {
        val today = LocalDate.now(clock)
        val count = taskRepository.countCompletedTasksInAWeek(today, includeRecurring = true).first()
        return context.getString(R.string.chat_local_week_count_format, count)
    }

    private fun containsMutationVerb(text: String): Boolean = MUTATION_VERBS.containsMatchIn(text)

    companion object {
        private const val MAX_INTENT_LENGTH = 60
        private val GREETING = Regex(
            "^(merhaba|selam|s\\.?a\\.?|naber|iyi\\s+(günler|sabahlar|akşamlar)|" +
                "hi|hello|hey|good\\s+(morning|afternoon|evening))[!?.\\s]*\$",
        )
        private val TODAY_TR_ANCHOR = Regex(
            "\\bbugün(kü)?\\b.*\\b(ne|neler|görev(ler|im|in)?|iş(ler|im|in)?|var)\\b",
        )
        private val TODAY_EN_ANCHOR = Regex(
            "(what'?s\\s+(due|on)\\s+today|today'?s?\\s+tasks?|(any|my)\\s+tasks?\\s+today)",
        )
        private val OVERDUE_KEYWORDS = Regex(
            "\\b(gecikmiş|geciken|overdue|past\\s+due)\\b",
        )
        private val WEEKLY_TR_ANCHOR = Regex(
            "\\bbu\\s+hafta\\b.*\\b(nasıl(ım|sın|sınız)?|gidiyor(um|sun)?|ne\\s+kadar|kaç|ilerleme)\\b|" +
                "\\bhafta(lık)?\\s+ilerleme",
        )
        private val WEEKLY_EN_ANCHOR = Regex(
            "(how\\s+am\\s+i\\s+doing\\s+this\\s+week|this\\s+week'?s?\\s+progress|weekly\\s+progress)",
        )
        private val MUTATION_VERBS = Regex(
            "\\b(ekle|sil|güncelle|oluştur|tamamla|değiştir|kaldır|" +
                "add|delete|create|update|remove|complete|mark|set)\\b",
        )
    }
}
