package com.todoapp.mobile.ui.topbar

sealed interface TopBarContract {
    data class UiState(
        val isUserAuthenticated: Boolean
    )
    sealed interface UiAction {
        data object OnSettingsClick : UiAction
        data object OnProfileClick : UiAction
        data object OnNotificationClick : UiAction
        data object OnBackClick : UiAction
        data object OnLogoutClick : UiAction
        data object OnAuthenticationUpdate : UiAction
    }
}
