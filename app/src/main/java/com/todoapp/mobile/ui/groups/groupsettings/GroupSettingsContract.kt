package com.todoapp.mobile.ui.groups.groupsettings

object GroupSettingsContract {
    data class UiState(
        val groupId: Long = 0L,
        val name: String = "",
        val description: String = "",
        val currentUserRole: String = "",
        val avatarUrl: String? = null,
        val avatarVersion: Long = 0L,
        val isSaving: Boolean = false,
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
    )

    sealed interface UiAction {
        data class OnNameChange(
            val name: String,
        ) : UiAction

        data class OnDescriptionChange(
            val description: String,
        ) : UiAction

        data object OnSaveTap : UiAction

        data object OnManageMembersTap : UiAction

        data object OnTransferOwnershipTap : UiAction

        data class OnAvatarPicked(
            val bytes: ByteArray,
            val mimeType: String,
        ) : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(
            val message: String,
        ) : UiEffect
    }
}
