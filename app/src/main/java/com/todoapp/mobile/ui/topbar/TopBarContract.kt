package com.todoapp.mobile.ui.topbar

sealed interface TopBarContract {
    data class UiState(
        val isUserAuthenticated: Boolean,
        val avatarUrl: String? = null,
        val displayName: String = "",
        val avatarVersion: Long = 0L,
    )
    sealed interface UiAction {
        data object OnSettingsClick : UiAction
        data object OnProfileClick : UiAction
        data object OnNotificationClick : UiAction
        data object OnBackClick : UiAction
        data object OnAuthenticationUpdate : UiAction
        data object OnSearchClick : UiAction
        data class OnGroupSettingsClick(val groupId: Long) : UiAction
    }
}
