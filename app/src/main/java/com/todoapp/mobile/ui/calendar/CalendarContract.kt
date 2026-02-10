package com.todoapp.mobile.ui.calendar

import com.todoapp.uikit.components.TaskDayItem
import java.time.LocalDate

object CalendarContract {
    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val selectedFirstDate: LocalDate? = null,
            val selectedSecondDate: LocalDate? = null,
            val taskDayItems: List<TaskDayItem> = emptyList(),
        ) : UiState

        data class Error(
            val message: String,
            val throwable: Throwable? = null,
        ) : UiState
    }

    sealed interface UiAction {
        data class OnFirstDateSelect(val date: LocalDate) : UiAction
        data class OnSecondDateSelect(val date: LocalDate) : UiAction
        data object OnFirstDateDeselect : UiAction
        data object OnSecondDateDeselect : UiAction
        data object OnRetry : UiAction
    }

    sealed interface UiEffect
}
