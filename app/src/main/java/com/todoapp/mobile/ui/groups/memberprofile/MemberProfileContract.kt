package com.todoapp.mobile.ui.groups.memberprofile

object MemberProfileContract {
    data class MemberUiItem(
        val userId: Long,
        val displayName: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val avatarUrl: String?,
        val initials: String,
        val role: String,
        val joinedAt: String,
    )

    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val member: MemberUiItem,
            val pendingRemoval: Boolean = false,
            val showConfirmDialog: Boolean = false,
        ) : UiState

        data class Error(
            val message: String,
        ) : UiState
    }

    sealed interface UiAction {
        data object OnRemoveTap : UiAction

        data object OnConfirmRemove : UiAction

        data object OnDismissDialog : UiAction

        data object OnUndoRemove : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(
            val message: String,
        ) : UiEffect
    }
}
