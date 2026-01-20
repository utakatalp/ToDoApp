package com.todoapp.mobile.ui.settings

object SettingsContract {
    data class UiState(
        val secretMode: Boolean = false,
        val selectedSecretMode: ReopenSecretMode = ReopenSecretMode.IMMEDIATE,
    )

    sealed interface UiAction {
        data class onSelectedSecretModeChange(val label: ReopenSecretMode) : UiAction
    }

    sealed interface UiEffect
}
