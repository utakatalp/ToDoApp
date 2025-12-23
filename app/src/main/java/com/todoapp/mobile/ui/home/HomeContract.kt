package com.todoapp.mobile.ui.home

import com.todoapp.mobile.domain.model.Task
import java.time.LocalDate

object HomeContract {
    data class UiState(
        val selectedDate: LocalDate = LocalDate.now(),
        val tasks: List<Task> = emptyList(),
        val completedTaskCountThisWeek: Int = 0,
        val pendingTaskCountThisWeek: Int = 0,
    )

    sealed interface UiAction {
        // data object OnLoginClick : UiAction
        data class OnDateSelect(
            val date: LocalDate,
        ) : UiAction

        data class OnTaskClick(
            val task: Task,
        ) : UiAction
    }

    sealed interface UiEffect {
        // data object NavigateToLogin : UiEffect
    }
}
