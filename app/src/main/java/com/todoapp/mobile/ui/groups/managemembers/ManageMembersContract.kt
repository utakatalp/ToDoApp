package com.todoapp.mobile.ui.groups.managemembers

object ManageMembersContract {
    data class ManageMemberUiItem(
        val userId: Long,
        val displayName: String,
        val email: String,
        val avatarUrl: String?,
        val initials: String,
        val role: String,
    )

    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val members: List<ManageMemberUiItem>,
        ) : UiState

        data class Error(
            val message: String,
        ) : UiState
    }

    sealed interface UiAction {
        data object OnAddMemberTap : UiAction

        data object OnScreenResumed : UiAction

        data class OnMemberTap(
            val userId: Long,
        ) : UiAction

        data class OnRemoveMemberTap(
            val userId: Long,
        ) : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(
            val message: String,
        ) : UiEffect
    }
}
