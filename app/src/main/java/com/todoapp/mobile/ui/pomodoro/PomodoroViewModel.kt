package com.todoapp.mobile.ui.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.model.Pomodoro
import com.todoapp.mobile.domain.repository.PomodoroRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.pomodoro.PomodoroContract.UiAction
import com.todoapp.mobile.ui.pomodoro.PomodoroContract.UiEffect
import com.todoapp.mobile.ui.pomodoro.PomodoroContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val pomodoroRepository: PomodoroRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(min = INITIAL_MINUTES, second = INITIAL_SECONDS)
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    private var timerJob: Job? = null
    private var overtimeJob: Job? = null

    private var overtimeMillis: Long = 0L

    private lateinit var pomodoro: Pomodoro

    private var remainingMillis: Long = 0L

    private val sessionQueue: ArrayDeque<Session> = ArrayDeque()

    init {
        viewModelScope.launch {
            pomodoro = pomodoroRepository.getSavedPomodoroSettings()!! // null olma ihtimali yok ama sor
            buildSessionQueue(pomodoro)
            startNextSession(autoStart = false)
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.SkipSession -> {
                startNextSession(autoStart = uiState.value.isStopWatchRunning)
            }

            UiAction.StartCountDown -> {
                if (uiState.value.isOvertime) {
                    startNextSession(autoStart = true)
                    _uiState.update { it.copy(isStopWatchRunning = true, isOvertime = false, infoMessage = null) }
                } else {
                    startCountdown()
                    _uiState.update { it.copy(isStopWatchRunning = true) }
                }
            }

            UiAction.StopCountDown -> {
                stopCountdown()
                _uiState.update { it.copy(isStopWatchRunning = false) }
            }
        }
    }

    private fun startCountdown() {
        if (timerJob?.isActive == true) return

        if (uiState.value.isOvertime) return

        if (remainingMillis <= 0L) {
            setTime(min = 0, sec = 0)
            return
        }

        timerJob = viewModelScope.launch {
            publishRemaining(remainingMillis)

            while (isActive && remainingMillis > 0L) {
                delay(MILLIS_PER_SECOND)
                remainingMillis -= MILLIS_PER_SECOND
                if (remainingMillis < 0L) remainingMillis = 0L
                publishRemaining(remainingMillis)
            }

            if (isActive && remainingMillis == 0L) {
                onSessionFinished()
            }
        }
    }

    private fun stopCountdown() {
        timerJob?.cancel()
        overtimeJob?.cancel()
    }

    private fun startOvertime() {
        overtimeJob?.cancel()
        overtimeMillis = 0L

        publishRemaining(0L)
        _uiState.update {
            it.copy(
                isOvertime = true,
                isStopWatchRunning = true,
                infoMessage = NEXT_STEP_MESSAGE
            )
        }

        val finishedMode = uiState.value.mode

        _uiEffect.trySend(UiEffect.SessionFinished(finishedMode))

        _uiState.update { it.copy(mode = PomodoroMode.OverTime) }

        overtimeJob = viewModelScope.launch {
            while (isActive) {
                delay(MILLIS_PER_SECOND)
                overtimeMillis += MILLIS_PER_SECOND
                publishRemaining(overtimeMillis)
            }
        }
    }

    private fun publishRemaining(remainingMillis: Long) {
        val totalSeconds = (remainingMillis / MILLIS_PER_SECOND).toInt()
        val min = totalSeconds / SECONDS_PER_MINUTE_INT
        val sec = totalSeconds % SECONDS_PER_MINUTE_INT
        setTime(min = min, sec = sec)
    }

    private fun setTime(min: Int, sec: Int) {
        _uiState.update { it.copy(min = min, second = sec) }
    }

    private fun buildSessionQueue(pomodoro: Pomodoro) {
        sessionQueue.clear()

        val focusMillis = pomodoro.focusTime * MILLIS_PER_MINUTE
        val shortMillis = pomodoro.shortBreak * MILLIS_PER_MINUTE
        val longMillis = pomodoro.longBreak * MILLIS_PER_MINUTE

        for (i in 1..pomodoro.sessionCount) {
            sessionQueue.addLast(
                Session(
                    duration = focusMillis,
                    mode = PomodoroMode.Focus
                )
            )

            val isLastFocus = i == pomodoro.sessionCount
            if (!isLastFocus) {
                val breakMode = if (i % pomodoro.sectionCount == 0) {
                    PomodoroMode.LongBreak
                } else {
                    PomodoroMode.ShortBreak
                }

                val breakDuration = if (breakMode is PomodoroMode.LongBreak) {
                    longMillis
                } else {
                    shortMillis
                }

                sessionQueue.addLast(
                    Session(
                        duration = breakDuration,
                        mode = breakMode
                    )
                )
            }
        }
    }

    private fun startNextSession(autoStart: Boolean) {
        stopCountdown()
        overtimeMillis = 0L
        _uiState.update { it.copy(isOvertime = false, infoMessage = null) }

        val next: Session? = sessionQueue.pollFirst()
        if (next == null) {
            remainingMillis = 0L
            _uiState.update {
                it.copy(
                    mode = PomodoroMode.Focus,
                    isStopWatchRunning = false
                )
            }
            publishRemaining(remainingMillis)
            return
        }

        remainingMillis = next.duration
        _uiState.update { it.copy(mode = next.mode) }
        publishRemaining(remainingMillis)

        if (autoStart) {
            startCountdown()
        }
    }

    private fun onSessionFinished() {
        startOvertime()
    }

    private companion object {
        // Time constants
        const val MILLIS_PER_SECOND: Long = 1_000L
        const val SECONDS_PER_MINUTE_INT: Int = 60
        const val MILLIS_PER_MINUTE: Long = 60_000L

        // UI
        const val INITIAL_MINUTES: Int = 25
        const val INITIAL_SECONDS: Int = 0

        // Messages
        const val NEXT_STEP_MESSAGE: String = "Session has done, start the next one."
    }
}
