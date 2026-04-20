package com.todoapp.mobile.ui.groups.transferownership

object TransferOwnershipContract {
    data class TransferMemberUiItem(
        val userId: Long,
        val displayName: String,
        val subtitle: String,
        val avatarUrl: String?,
        val initials: String,
    )

    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val members: List<TransferMemberUiItem>,
            val filteredMembers: List<TransferMemberUiItem>,
            val searchQuery: String,
            val selectedUserId: Long?,
        ) : UiState

        data class Error(
            val message: String,
        ) : UiState
    }

    sealed interface UiAction {
        data class OnSearchChange(
            val query: String,
        ) : UiAction

        data class OnMemberSelected(
            val userId: Long,
        ) : UiAction

        data object OnTransferConfirm : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(
            val message: String,
        ) : UiEffect
    }
}
