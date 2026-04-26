package com.todoapp.mobile.ui.invitations

import com.todoapp.mobile.domain.model.Invitation

object InvitationsContract {
    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val items: List<Invitation>,
            val isRefreshing: Boolean = false,
            val processingIds: Set<Long> = emptySet(),
            val pendingAction: PendingAction? = null,
        ) : UiState

        data class Error(val message: String) : UiState
    }

    sealed interface PendingAction {
        val invitation: Invitation
        data class Accept(override val invitation: Invitation) : PendingAction
        data class Decline(override val invitation: Invitation) : PendingAction
    }

    sealed interface UiAction {
        data object OnRetry : UiAction
        data object OnPullToRefresh : UiAction
        data class OnAccept(val id: Long) : UiAction
        data class OnDecline(val id: Long) : UiAction
        data object OnConfirmPending : UiAction
        data object OnDismissPending : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(val resId: Int) : UiEffect
    }
}
