package com.todoapp.mobile.ui.addpomodorotimer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.model.Pomodoro
import com.todoapp.mobile.domain.repository.PomodoroRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerContract.UiAction
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    private var pomodoroId: Long = 0

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnFocusTimeChange -> updateUiState { copy(focusTime = action.value) }
            is UiAction.OnLongBreakChange -> updateUiState { copy(longBreak = action.value) }
            is UiAction.OnSectionCountChange -> updateUiState { copy(sectionCount = action.value) }
            is UiAction.OnSessionCountChange -> updateUiState { copy(sessionCount = action.value) }
            is UiAction.OnShortBreakChange -> updateUiState { copy(shortBreak = action.value) }
            UiAction.OnCancelTap -> onCancelTap()
            UiAction.OnStartTap -> onStartTap()
        }
    }

    private inline fun updateUiState(
        crossinline reducer: UiState.() -> UiState,
    ) {
        _uiState.update { current -> current.reducer() }
    }

    private fun onCancelTap() {
        _navEffect.trySend(NavigationEffect.Back)
    }

    private fun onStartTap() {
        updateSavedPomodoroSettings()
    }

    init {
        loadSavedPomodoroSettings()
    }

    private fun loadSavedPomodoroSettings() {
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
            _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro, Screen.Home))
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
    updateUiState {
        copy(
            sessionCount = pomodoro.sessionCount.toFloat(),
            focusTime = pomodoro.focusTime.toFloat(),
            shortBreak = pomodoro.shortBreak.toFloat(),
            longBreak = pomodoro.longBreak.toFloat(),
            sectionCount = pomodoro.sectionCount.toFloat(),
        )
    }
}
}
