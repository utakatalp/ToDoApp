package com.todoapp.mobile.ui.activity

import com.todoapp.mobile.ui.home.TaskFormState
import java.time.LocalDate
import java.time.LocalTime

object ActivityContract {
    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val selectedDate: LocalDate,
            val weeklyCompleted: Int,
            val weeklyPending: Int,
            val weeklyProgress: Float,
            val weeklyPendingProgress: Float,
            val weeklyBarValues: List<Int>,
            val weeklyPendingBarValues: List<Int>,
            val yearlyProgress: Float,
            val yearlyPendingProgress: Float,
            val yearlyCompleted: Int,
            val yearlyTotal: Int,
            val isSheetOpen: Boolean = false,
            val taskFormState: TaskFormState = TaskFormState(),
        ) : UiState

        data class Error(
            val message: String,
            val throwable: Throwable? = null,
        ) : UiState
    }

    sealed interface UiAction {
        data object OnRetry : UiAction
        data class OnWeekSelected(val date: LocalDate) : UiAction
        data object OnShowBottomSheet : UiAction
        data object OnDismissBottomSheet : UiAction
        data object OnTaskCreate : UiAction
        data class OnTaskTitleChange(val title: String) : UiAction
        data class OnDialogDateSelect(val date: LocalDate) : UiAction
        data object OnDialogDateDeselect : UiAction
        data class OnTaskTimeStartChange(val time: LocalTime) : UiAction
        data class OnTaskTimeEndChange(val time: LocalTime) : UiAction
        data class OnTaskDescriptionChange(val description: String) : UiAction
        data object OnToggleAdvancedSettings : UiAction
        data class OnTaskSecretChange(val isSecret: Boolean) : UiAction
        data object OnPomodoroTap : UiAction
        data object OnCompletedStatCardTap : UiAction
        data object OnPendingStatCardTap : UiAction
    }

    sealed interface UiEffect
}
