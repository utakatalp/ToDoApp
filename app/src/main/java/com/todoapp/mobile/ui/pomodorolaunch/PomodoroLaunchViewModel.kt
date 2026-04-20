package com.todoapp.mobile.ui.pomodorolaunch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.repository.PomodoroRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.pomodorolaunch.PomodoroLaunchContract.UiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroLaunchViewModel @Inject constructor(
    private val pomodoroRepository: PomodoroRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroLaunchContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    init {
        loadSettings()
    }

    fun onAction(action: UiAction) {
        when (action) {
            UiAction.OnStartTap -> _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro))
            UiAction.OnSettingsTap -> _navEffect.trySend(NavigationEffect.Navigate(Screen.AddPomodoroTimer))
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val pomodoro = pomodoroRepository.getSavedPomodoroSettings() ?: return@launch
            _uiState.update {
                it.copy(
                    sessionCount = pomodoro.sessionCount,
                    focusTime = pomodoro.focusTime,
                    shortBreak = pomodoro.shortBreak,
                    longBreak = pomodoro.longBreak,
                    isLoading = false,
                )
            }
        }
    }
}
