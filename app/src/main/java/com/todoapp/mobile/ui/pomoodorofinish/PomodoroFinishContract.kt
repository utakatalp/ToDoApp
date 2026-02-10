package com.todoapp.mobile.ui.pomoodorofinish

object PomodoroFinishContract {

    sealed interface UiAction {
        data object OnRestartTap : UiAction
        data object OnEditSettingsTap : UiAction
        data object OnDismiss : UiAction
    }
}
