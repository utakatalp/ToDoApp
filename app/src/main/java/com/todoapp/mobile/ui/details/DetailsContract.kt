package com.todoapp.mobile.ui.details

import androidx.annotation.StringRes
import java.time.LocalDate
import java.time.LocalTime

object DetailsContract {
    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val isDirty: Boolean,
            val isSaving: Boolean,
            val taskTitle: String,
            val taskTimeStart: LocalTime?,
            val taskTimeEnd: LocalTime?,
            val taskDate: LocalDate,
            val taskDescription: String,
            val dialogSelectedDate: LocalDate?,
            @StringRes val titleError: Int?,
        ) : UiState

        data class Error(
            val message: String,
            val throwable: Throwable? = null,
        ) : UiState
    }

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
        data object OnRetry : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(@StringRes val message: Int) : UiEffect
    }
}
