package com.todoapp.mobile.ui.createnewgroup

object CreateNewGroupContract {

    data class UiState(
        val groupName: String = "",
        val groupDescription: String? = null,
        val error: String? = null,
        val isUserAuthenticated: Boolean,
    )

    sealed interface UiAction {
        data object OnBackClick : UiAction
        data class OnGroupNameChange(val groupName: String) : UiAction
        data class OnGroupDescriptionChange(val groupDescription: String) : UiAction
        data object OnCreateTap : UiAction
    }
}
