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

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnFocusTimeChange -> _uiState.update { it.copy(focusTime = uiAction.value) }
            is UiAction.OnLongBreakChange -> _uiState.update { it.copy(longBreak = uiAction.value) }
            is UiAction.OnSectionCountChange -> _uiState.update { it.copy(sectionCount = uiAction.value) }
            is UiAction.OnSessionCountChange -> _uiState.update { it.copy(sessionCount = uiAction.value) }
            is UiAction.OnShortBreakChange -> _uiState.update { it.copy(shortBreak = uiAction.value) }
            is UiAction.OnCancelTap -> _navEffect.trySend(NavigationEffect.Back)
            is UiAction.OnStartTap -> updateSavedPomodoroSettings()
        }
    }

    init {
        viewModelScope.launch {
            val pomodoro = pomodoroRepository.getSavedPomodoroSettings()
            if (pomodoro == null) {
                insertPomodoroSettings()
                return@launch
            }
            loadPomodoroSettings(pomodoro)
            pomodoroId = pomodoro.id
        }
    }

    private fun updateSavedPomodoroSettings() {
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

    private suspend fun insertPomodoroSettings() {
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

    private fun loadPomodoroSettings(pomodoro: Pomodoro) {
        _uiState.update {
            it.copy(
                sessionCount = pomodoro.sessionCount.toFloat(),
                focusTime = pomodoro.focusTime.toFloat(),
                shortBreak = pomodoro.shortBreak.toFloat(),
                longBreak = pomodoro.longBreak.toFloat(),
                sectionCount = pomodoro.sectionCount.toFloat()
            )
        }
    }
}
