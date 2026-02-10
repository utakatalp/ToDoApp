package com.todoapp.mobile.ui.settings

import com.todoapp.mobile.domain.constants.DailyPlanDefaults.DEFAULT_PLAN_TIME
import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.mobile.domain.security.SecretModeReopenOption
import com.todoapp.mobile.domain.security.SecretModeReopenOptions
import java.time.LocalTime

object SettingsContract {
    data class UiState(
        val currentTheme: ThemePreference = ThemePreference.SYSTEM_DEFAULT,
        val selectedSecretMode: SecretModeReopenOption = SecretModeReopenOptions.Immediate,
        val remainedSecretModeTime: String = "",
        val isSecretModeActive: Boolean = false,
        val dailyPlanTime: LocalTime = DEFAULT_PLAN_TIME,
    )

    sealed interface UiAction {
        data class OnSelectedSecretModeChange(val label: SecretModeReopenOption) : UiAction
        data class OnThemeChange(val theme: ThemePreference) : UiAction
        data object OnSettingsSave : UiAction
        data object OnDisableSecretModeTap : UiAction
        data class OnDailyPlanTimeChange(val time: LocalTime) : UiAction
        data object OnNavigateToSecretModeSettings : UiAction
    }

    sealed interface UiEffect
}
