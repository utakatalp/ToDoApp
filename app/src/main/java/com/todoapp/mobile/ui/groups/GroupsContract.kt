package com.todoapp.mobile.ui.groups

object GroupsContract {

    data class GroupUiItem(
        val id: Long,
        val name: String,
        val role: String,
        val description: String,
        val memberCount: Int,
        val pendingTaskCount: Int,
        val createdAt: String,
    )

    sealed interface UiState {
        data object Loading : UiState
        data object Empty : UiState
        data class Success(
            val groups: List<GroupUiItem>,
        ) : UiState

        data class Error(val message: String) : UiState
    }

    sealed interface UiAction {
        data object OnCreateNewGroupTap : UiAction
        data class OnGroupTap(val id: Long) : UiAction
        data class OnMoveGroup(val from: Int, val to: Int) : UiAction
        data class OnDeleteGroupTap(val id: Long) : UiAction
    }
}
