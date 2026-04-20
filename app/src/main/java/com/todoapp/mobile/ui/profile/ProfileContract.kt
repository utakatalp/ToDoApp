package com.todoapp.mobile.ui.profile

object ProfileContract {

    data class UiState(
        val isLoading: Boolean = true,
        val userId: Long = 0L,
        val email: String = "",
        val displayName: String = "",
        val editedDisplayName: String = "",
        val avatarUrl: String? = null,
        val avatarVersion: Long = 0L,
        val isSaving: Boolean = false,
        val isUploading: Boolean = false,
        val errorMessage: String? = null,
    )

    sealed interface UiAction {
        data class OnDisplayNameChange(val value: String) : UiAction
        data object OnSaveName : UiAction
        data class OnAvatarPicked(val bytes: ByteArray, val mimeType: String) : UiAction
        data object OnBack : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(val message: String) : UiEffect
    }
}
