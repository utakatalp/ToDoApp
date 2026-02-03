package com.todoapp.mobile.ui.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.toUiMode
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.engine.PomodoroEvent
import com.todoapp.mobile.domain.engine.PomodoroMode
import com.todoapp.mobile.domain.engine.Session
import com.todoapp.mobile.domain.model.Pomodoro
import com.todoapp.mobile.domain.repository.PomodoroRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.pomodoro.PomodoroContract.UiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val pomodoroRepository: PomodoroRepository,
    private val engine: PomodoroEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<PomodoroContract.UiEffect>() }
    val uiEffect by lazy { _uiEffect.receiveAsFlow() }

    val engineState = engine.state
    private val engineEvents = engine.events

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    fun onAction(action: UiAction) {
        when (action) {
            UiAction.SkipSession -> onSkipSession()
            UiAction.StartCountDown -> onStartCountdown()
            UiAction.StopCountDown -> onStopCountdown()
            is UiAction.ToggleBannerVisibility -> updateBannerVisibility(action.isVisible)
            UiAction.Back -> onBack()
        }
    }

    init {
        initializeEngineIfNeeded()
        observeEngineEvents()
        observeEngineState()
    }

    private fun onSkipSession() {
        engine.skip(autoStart = uiState.value.isRunning)
    }

    private fun onStartCountdown() {
        if (uiState.value.isOvertime) {
            engine.skip(autoStart = true)
        } else {
            engine.start()
        }
    }

    private fun onStopCountdown() {
        engine.pause()
    }

    private fun updateBannerVisibility(isVisible: Boolean) {
        engine.updateBannerVisibility(isVisible)
    }

    private fun onBack() {
        _navEffect.trySend(NavigationEffect.Back)
    }

    private fun initializeEngineIfNeeded() {
        viewModelScope.launch {
            if (!engine.state.value.isRunning) {
                val pomodoro = pomodoroRepository.getSavedPomodoroSettings()!!
                val queue = buildSessionQueue(pomodoro)
                engine.setSessionQueue(queue)
                engine.prepare()
            }
        }
    }

    private fun navigateToFinishScreen() {
        _navEffect.trySend(NavigationEffect.Navigate(Screen.PomodoroFinish, Screen.Home))
    }

    private fun observeEngineEvents() {
        viewModelScope.launch {
            engineEvents.collect { event ->
                when (event) {
                    PomodoroEvent.SessionFinished -> onSessionFinished()
                    PomodoroEvent.PomodoroFinished -> navigateToFinishScreen()
                }
            }
        }
    }

    private suspend fun onSessionFinished() {
        _uiEffect.send(PomodoroContract.UiEffect.SessionFinished)
    }

    private fun observeEngineState() {
        viewModelScope.launch {
            engineState.collect { engineSnapshot ->
                mapEngineStateToUiState(engineSnapshot)
            }
        }
    }

    private fun mapEngineStateToUiState(engineStateSnapshot: com.todoapp.mobile.domain.engine.PomodoroEngineState) {
        val totalSeconds = engineStateSnapshot.remainingSeconds
        val min = (totalSeconds / SECONDS_PER_MINUTE).toInt()
        val sec = (totalSeconds % SECONDS_PER_MINUTE).toInt()

        _uiState.update {
            it.copy(
                min = min,
                second = sec,
                mode = engineStateSnapshot.mode.toUiMode(),
                isRunning = engineStateSnapshot.isRunning,
                isOvertime = engineStateSnapshot.isOvertime,
            )
        }
    }

    private fun buildSessionQueue(pomodoro: Pomodoro): ArrayDeque<Session> {
        val queue: ArrayDeque<Session> = ArrayDeque()

        val focusSeconds = pomodoro.focusTime * SECONDS_PER_MINUTE
        val shortSeconds = pomodoro.shortBreak * SECONDS_PER_MINUTE
        val longSeconds = pomodoro.longBreak * SECONDS_PER_MINUTE

        for (i in FIRST_SESSION_INDEX..pomodoro.sessionCount) {
            queue.addLast(
                Session(
                    durationSeconds = focusSeconds,
                    mode = PomodoroMode.Focus,
                )
            )

            val isLastFocus = i == pomodoro.sessionCount
            if (!isLastFocus) {
                val breakMode =
                    if (i % pomodoro.sectionCount == 0) {
                        PomodoroMode.LongBreak
                    } else {
                        PomodoroMode.ShortBreak
                    }

                val breakDurationSeconds =
                    if (breakMode == PomodoroMode.LongBreak) {
                        longSeconds
                    } else {
                        shortSeconds
                    }

                queue.addLast(
                    Session(
                        durationSeconds = breakDurationSeconds,
                        mode = breakMode,
                    )
                )
            }
        }

        return queue
    }

    private companion object {
        private const val SECONDS_PER_MINUTE: Long = 60L
        private const val FIRST_SESSION_INDEX: Int = 1
    }
}
