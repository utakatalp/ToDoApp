package com.todoapp.mobile.ui.groups.invitemember

import androidx.compose.runtime.Immutable

object InviteMemberContract {
    @Immutable
    data class UiState(
        val email: String = "",
        val emailError: String? = null,
        val isLoading: Boolean = false,
        val isSent: Boolean = false,
    )

    sealed interface UiAction {
        data class OnEmailChange(
            val email: String,
        ) : UiAction

        data object OnSendInviteTap : UiAction

        data object OnShareLinkTap : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(
            val message: String,
        ) : UiEffect
    }
}
