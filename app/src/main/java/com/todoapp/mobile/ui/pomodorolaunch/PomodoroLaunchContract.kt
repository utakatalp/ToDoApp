package com.todoapp.mobile.ui.pomodorolaunch

object PomodoroLaunchContract {
    data class UiState(
        val sessionCount: Int = 0,
        val focusTime: Int = 0,
        val shortBreak: Int = 0,
        val longBreak: Int = 0,
        val isLoading: Boolean = true,
    )

    sealed interface UiAction {
        data object OnStartTap : UiAction

        data object OnSettingsTap : UiAction
    }
}
