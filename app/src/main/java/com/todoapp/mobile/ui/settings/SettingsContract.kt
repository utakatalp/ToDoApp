package com.todoapp.mobile.ui.settings

import com.todoapp.mobile.domain.model.ThemePreference

object SettingsContract {

    data class UiState(
        val currentTheme: ThemePreference = ThemePreference.SYSTEM_DEFAULT,
    )

    sealed interface UiAction {
        data class OnThemeChange(val theme: ThemePreference) : UiAction
        data object OnBackClick : UiAction
    }

    sealed interface UiEffect {
        data object NavigateToBack : UiEffect
    }
}
