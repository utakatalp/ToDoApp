package com.todoapp.mobile.ui.pomodorosummary

object PomodoroSummaryContract {

    data class UiState(
        val focusSessions: Int = 0,
        val totalFocusMinutes: Int = 0,
        val totalBreakMinutes: Int = 0,
        val completedAt: String = "",
    )

    sealed interface UiAction {
        data object OnStartAgainTap : UiAction
        data object OnEditSettingsTap : UiAction
        data object OnCloseTap : UiAction
    }
}
