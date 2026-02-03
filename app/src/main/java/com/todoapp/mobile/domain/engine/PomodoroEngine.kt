package com.todoapp.mobile.domain.engine

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface PomodoroEngine {

    val state: StateFlow<PomodoroEngineState>
    val events: SharedFlow<PomodoroEvent>

    fun setSessionQueue(queue: ArrayDeque<Session>)

    fun prepare()

    fun start()

    fun pause()

    fun skip(autoStart: Boolean)

    fun finish()

    fun updateBannerVisibility(isVisible: Boolean)
}

data class PomodoroEngineState(
    val remainingSeconds: Long = DEFAULT_REMAINING_SECONDS,
    val mode: PomodoroMode = PomodoroMode.Focus,
    val isRunning: Boolean = false,
    val isOvertime: Boolean = false,
    val isVisible: Boolean = true,
)

sealed interface PomodoroEvent {
    data object SessionFinished : PomodoroEvent

    data object PomodoroFinished : PomodoroEvent
}

enum class PomodoroMode {
    Focus,
    ShortBreak,
    LongBreak,
    OverTime,
}

data class Session(
    val durationSeconds: Long,
    val mode: PomodoroMode,
)

private const val DEFAULT_FOCUS_MINUTES: Long = 25L
private const val SECONDS_PER_MINUTE: Long = 60L
private const val DEFAULT_REMAINING_SECONDS: Long = DEFAULT_FOCUS_MINUTES * SECONDS_PER_MINUTE
