package com.todoapp.mobile.ui.addpomodorotimer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.model.Pomodoro
import com.todoapp.mobile.domain.repository.PomodoroRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerContract.UiAction
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerContract.UiEffect
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPomodoroTimerViewModel @Inject constructor(
    private val pomodoroRepository: PomodoroRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }
    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }
    private var pomodoroId: Long = 0

    init {
        viewModelScope.launch {

            val pomodoro = pomodoroRepository.getPomodoro()
            if (pomodoro != null) {
                _uiState.update {
                    it.copy(
                        sessionCount = pomodoro.sessionCount.toFloat(),
                        focusTime = pomodoro.focusTime.toFloat(),
                        shortBreak = pomodoro.shortBreak.toFloat(),
                        longBreak = pomodoro.longBreak.toFloat(),
                        sectionCount = pomodoro.sectionCount.toFloat()
                    )
                }
                pomodoroId = pomodoro.id
            } else {
                pomodoroRepository.insertPomodoro(
                    Pomodoro(
                        id = 0,
                        sessionCount = uiState.value.sessionCount.toInt(),
                        focusTime = uiState.value.focusTime.toInt(),
                        shortBreak = uiState.value.shortBreak.toInt(),
                        longBreak = uiState.value.longBreak.toInt(),
                        sectionCount = uiState.value.sectionCount.toInt()
                    )
                )
            }
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.onFocusTimeChange -> _uiState.update { it.copy(focusTime = uiAction.value) }
            is UiAction.onLongBreakChange -> _uiState.update { it.copy(longBreak = uiAction.value) }
            is UiAction.onSectionCountChange -> _uiState.update { it.copy(sectionCount = uiAction.value) }
            is UiAction.onSessionCountChange -> _uiState.update { it.copy(sessionCount = uiAction.value) }
            is UiAction.onShortBreakChange -> _uiState.update { it.copy(shortBreak = uiAction.value) }
            is UiAction.OnCancelTap -> _navEffect.trySend(NavigationEffect.Back)
            is UiAction.OnStartTap -> {
                viewModelScope.launch {
                    pomodoroRepository.updatePomodoro(
                        Pomodoro(
                            id = pomodoroId,
                            sessionCount = uiState.value.sessionCount.toInt(),
                            focusTime = uiState.value.focusTime.toInt(),
                            shortBreak = uiState.value.shortBreak.toInt(),
                            longBreak = uiState.value.longBreak.toInt(),
                            sectionCount = uiState.value.sectionCount.toInt()
                        )
                    )
                    _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro))
                }
            }
        }
    }
}
