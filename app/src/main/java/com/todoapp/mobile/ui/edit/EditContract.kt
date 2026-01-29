package com.todoapp.mobile.ui.edit

import androidx.annotation.StringRes
import java.time.LocalDate
import java.time.LocalTime

object EditContract {
    data class UiState(
        val isDirty: Boolean = false,
        val taskTitle: String = "",
        val taskTimeStart: LocalTime? = null,
        val taskTimeEnd: LocalTime? = null,
        val taskDate: LocalDate = LocalDate.now(),
        val taskDescription: String = "",
        val dialogSelectedDate: LocalDate? = null,
        val titleError: Int? = null,
    )

    sealed interface UiAction {
        data object OnBackClick : UiAction
        data object OnCancelClick : UiAction
        data class OnTaskTitleEdit(val title: String) : UiAction
        data class OnTaskTimeStartEdit(val time: LocalTime) : UiAction
        data class OnTaskTimeEndEdit(val time: LocalTime) : UiAction
        data class OnTaskDateEdit(val date: LocalDate) : UiAction
        data class OnTaskDescriptionEdit(val description: String) : UiAction
        data class OnDialogDateSelect(val date: LocalDate) : UiAction
        data object OnDialogDateDeselect : UiAction
        data object OnSaveChanges : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(@StringRes val message: Int) : UiEffect
    }
}
