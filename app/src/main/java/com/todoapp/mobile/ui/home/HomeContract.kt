package com.todoapp.mobile.ui.home

import com.todoapp.mobile.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime

object HomeContract {
    data class UiState(
        val selectedDate: LocalDate? = LocalDate.now(),
        val tasks: List<Task> = emptyList(),
        val completedTaskCountThisWeek: Int = 0,
        val pendingTaskCountThisWeek: Int = 0,
        val taskTitle: String = "",
        val taskTimeStart: LocalTime? = null,
        val taskTimeEnd: LocalTime? = null,
        val taskDate: LocalDate = LocalDate.now(),
        val taskDescription: String = "",
        val isSheetOpen: Boolean = false,
    )

    sealed interface UiAction {
        data class OnDateSelect(
            val date: LocalDate,
        ) : UiAction

        data object OnDateDeselect : UiAction

        data class OnTaskClick(
            val task: Task,
        ) : UiAction

        data class OnTaskTitleChange(
            val title: String,
        ) : UiAction

        data class OnTaskTimeStartChange(
            val time: LocalTime,
        ) : UiAction

        data class OnTaskTimeEndChange(
            val time: LocalTime,
        ) : UiAction

        data class OnTaskDateChange(
            val date: LocalDate,
        ) : UiAction

        data class OnTaskDescriptionChange(
            val description: String,
        ) : UiAction

        data object OnShowBottomSheet : UiAction

        data object OnDismissBottomSheet : UiAction
    }

    sealed interface UiEffect
}
