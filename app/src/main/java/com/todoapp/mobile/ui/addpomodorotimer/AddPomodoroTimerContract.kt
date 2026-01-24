package com.todoapp.mobile.ui.addpomodorotimer

object AddPomodoroTimerContract {

    data class UiState(
        val sessionCount: Float = 8f,
        val focusTime: Float = 25f,
        val shortBreak: Float = 5f,
        val longBreak: Float = 20f,
        val sectionCount: Float = 4f,
    )

    sealed interface UiAction {
        data class onSessionCountChange(val value: Float) : UiAction
        data class onFocusTimeChange(val value: Float) : UiAction
        data class onShortBreakChange(val value: Float) : UiAction
        data class onLongBreakChange(val value: Float) : UiAction
        data class onSectionCountChange(val value: Float) : UiAction
        data object OnCancelTap : UiAction
        data object OnStartTap : UiAction
    }

    sealed interface UiEffect
}
