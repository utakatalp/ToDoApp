package com.todoapp.mobile.ui.groups

object GroupsContract {

    sealed interface UiState {
        data object Loading : UiState
        data object Empty : UiState
        data class Success(
            val groups: List<Any> = emptyList()
        ) : UiState

        data class Error(val message: String) : UiState
    }

    sealed interface UiAction {
        data object OnCreateNewGroupTap : UiAction
    }
}
