package com.todoapp.mobile.ui.groupdetails

import com.todoapp.mobile.domain.model.Task

object GroupDetailsContract {
    sealed interface UiState {
        data class Success(
            val memberCount: Int,
            val pendingTaskCount: Int,
            val completedTaskCount: Int,
            val groupTasks: List<Task.Group> = emptyList(),
            val checked: Boolean = false,
        ) : UiState

        object Loading : UiState
        data class Error(val message: String, val throwable: Throwable) : UiState
        data class Empty(val groupId: Long) : UiState
    }

    sealed interface UiAction {
        data object OnAllTap : UiAction
        data object OnAssignedToMeTap : UiAction
        data class OnTaskCheckboxTap(val task: Task.Group) : UiAction
        data class OnTaskCardTap(val task: Task.Group) : UiAction
        data class OnTaskLongPress(val task: Task.Group) : UiAction
        data class OnAddTaskTap(val groupId: Long) : UiAction
    }
}
