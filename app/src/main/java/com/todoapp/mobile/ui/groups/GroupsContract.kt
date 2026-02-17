package com.todoapp.mobile.ui.groups

import com.todoapp.mobile.data.model.network.data.FamilyGroupSummaryData

object GroupsContract {

    sealed interface UiState {
        data object Loading : UiState
        data object Empty : UiState
        data class Success(
            val groups: List<FamilyGroupSummaryData> = emptyList()
        ) : UiState

        data class Error(val message: String) : UiState
    }

    sealed interface UiAction {
        data object OnCreateNewGroupTap : UiAction
        data class OnDeleteGroupTap(val id: Long) : UiAction
    }
}
