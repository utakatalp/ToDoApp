package com.todoapp.mobile.data.engine

import com.todoapp.mobile.common.pollFirst
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.engine.PomodoroEngineState
import com.todoapp.mobile.domain.engine.PomodoroEvent
import com.todoapp.mobile.domain.engine.PomodoroMode
import com.todoapp.mobile.domain.engine.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroEngineImpl @Inject constructor() : PomodoroEngine {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(PomodoroEngineState())
    override val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<PomodoroEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val events = _events.asSharedFlow()

    private val sessionQueue: ArrayDeque<Session> = ArrayDeque()

    private var timerJob: Job? = null
    private var overtimeJob: Job? = null

    private var remainingMillis: Long = 0L
    private var overtimeMillis: Long = 0L

    // ---------------- QUEUE ----------------

    override fun setSessionQueue(queue: ArrayDeque<Session>) {
        sessionQueue.clear()
        queue.forEach { sessionQueue.addLast(it) }
    }

    override fun prepare() {
        startNextSession(autoStart = false)
    }

    // ---------------- CONTROLS ----------------

    override fun start() {
        if (_state.value.isOvertime) return
        startCountdown()
        _state.update { it.copy(isRunning = true) }
    }

    override fun pause() {
        cancelRunningJobs()
        _state.update { it.copy(isRunning = false) }
    }

    override fun skip(autoStart: Boolean) {
        startNextSession(autoStart)
    }

    override fun finish() {
        cancelRunningJobs()
        _state.update { it.copy(isRunning = false) }
        _events.tryEmit(PomodoroEvent.PomodoroFinished)
    }

    override fun updateBannerVisibility(isVisible: Boolean) {
        _state.update { it.copy(isVisible = isVisible) }
    }

    // ---------------- CORE LOGIC ----------------

    private fun startCountdown() {
        if (timerJob?.isActive == true) return
        if (remainingMillis <= ZERO_MILLIS) return

        timerJob = scope.launch {
            runCountdown()
        }
    }

    private fun startOvertime() {
        overtimeJob?.cancel()
        overtimeMillis = ZERO_MILLIS

        _state.update {
            it.copy(
                isOvertime = true,
                isRunning = true,
                mode = PomodoroMode.OverTime,
            )
        }

        _events.tryEmit(PomodoroEvent.SessionFinished)

        overtimeJob = scope.launch {
            runOvertime()
        }
    }

    private fun startNextSession(autoStart: Boolean) {
        pause()
        overtimeMillis = ZERO_MILLIS

        _state.update { it.copy(isOvertime = false) }

        val next = sessionQueue.pollFirst()
        if (next == null) {
            _events.tryEmit(PomodoroEvent.PomodoroFinished)
            updateBannerVisibility(false)
            return
        }

        remainingMillis = next.durationSeconds * MILLIS_PER_SECOND
        _state.update { it.copy(mode = next.mode) }
        publishRemaining(remainingMillis)

        if (autoStart) start()
    }

    private fun onSessionFinished() {
        startOvertime()
    }

    private fun cancelRunningJobs() {
        timerJob?.cancel()
        overtimeJob?.cancel()
    }

    private suspend fun runCountdown() {
        publishRemaining(remainingMillis)

        while (currentCoroutineContext().isActive && remainingMillis > ZERO_MILLIS) {
            delay(TICK_MILLIS)
            remainingMillis = (remainingMillis - TICK_MILLIS).coerceAtLeast(ZERO_MILLIS)
            publishRemaining(remainingMillis)
        }

        if (currentCoroutineContext().isActive && remainingMillis == ZERO_MILLIS) {
            onSessionFinished()
        }
    }

    private suspend fun runOvertime() {
        while (currentCoroutineContext().isActive) {
            delay(TICK_MILLIS)
            overtimeMillis += TICK_MILLIS
            publishRemaining(overtimeMillis)
        }
    }

    // ---------------- TIME ----------------

    private fun publishRemaining(millis: Long) {
        val totalSeconds = (millis / MILLIS_PER_SECOND).coerceAtLeast(ZERO_SECONDS)
        _state.update { it.copy(remainingSeconds = totalSeconds) }
    }

    private companion object {
        const val TICK_MILLIS: Long = 1_000L
        const val MILLIS_PER_SECOND: Long = 1_000L
        const val ZERO_MILLIS: Long = 0L
        const val ZERO_SECONDS: Long = 0L
    }
}
