package com.todoapp.mobile.ui.calendar

import com.todoapp.mobile.ui.home.TaskFormState
import com.todoapp.uikit.components.TaskDayItem
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

object CalendarContract {
    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val selectedDate: LocalDate? = null,
            val selectedMonth: YearMonth = YearMonth.now(),
            val taskDatesInMonth: Set<LocalDate> = emptySet(),
            val taskDayItems: List<TaskDayItem> = emptyList(),
            val isSheetOpen: Boolean = false,
            val taskFormState: TaskFormState = TaskFormState(),
        ) : UiState

        data class Error(
            val message: String,
            val throwable: Throwable? = null,
        ) : UiState
    }

    sealed interface UiAction {
        data class OnDateSelect(
            val date: LocalDate,
        ) : UiAction

        data object OnDateDeselect : UiAction

        data object OnMonthForward : UiAction

        data object OnMonthBack : UiAction

        data object OnRetry : UiAction

        data class OnTaskClick(
            val taskId: Long,
        ) : UiAction

        data object OnShowBottomSheet : UiAction

        data object OnDismissBottomSheet : UiAction

        data object OnTaskCreate : UiAction

        data class OnTaskTitleChange(
            val title: String,
        ) : UiAction

        data class OnDialogDateSelect(
            val date: LocalDate,
        ) : UiAction

        data object OnDialogDateDeselect : UiAction

        data class OnTaskTimeStartChange(
            val time: LocalTime,
        ) : UiAction

        data class OnTaskTimeEndChange(
            val time: LocalTime,
        ) : UiAction

        data class OnTaskDescriptionChange(
            val description: String,
        ) : UiAction

        data object OnToggleAdvancedSettings : UiAction

        data class OnTaskSecretChange(
            val isSecret: Boolean,
        ) : UiAction

        data object OnPomodoroTap : UiAction

        data object OnSuccessfulBiometricAuthenticationHandle : UiAction
    }

    sealed interface UiEffect {
        data object ShowBiometricAuthenticator : UiEffect
    }
}
