package com.todoapp.mobile.ui.settings

import com.todoapp.mobile.domain.security.SecretModeReopenOption

object SettingsContract {
    data class UiState(
        val secretMode: Boolean = false,
        val selectedSecretMode: SecretModeReopenOption = SecretModeReopenOption.IMMEDIATE,
        val remainedSecretModeTime: String = "",
    )

    sealed interface UiAction {
        data class OnSelectedSecretModeChange(val label: SecretModeReopenOption) : UiAction
        data object OnSettingsSave : UiAction
    }

    sealed interface UiEffect
}
