package com.todoapp.mobile.ui.calendar

import com.todoapp.uikit.components.TaskDayItem
import java.time.LocalDate

object CalendarContract {
    data class UiState(
        val selectedFirstDate: LocalDate? = null,
        val selectedSecondDate: LocalDate? = null,
        val taskDayItems: List<TaskDayItem> = emptyList(),
    )

    sealed interface UiAction {
        data class OnFirstDateSelect(
            val date: LocalDate,
        ) : UiAction

        data class OnSecondDateSelect(
            val date: LocalDate,
        ) : UiAction

        data object OnFirstDateDeselect : UiAction

        data object OnSecondDateDeselect : UiAction
    }

    sealed interface UiEffect
}
