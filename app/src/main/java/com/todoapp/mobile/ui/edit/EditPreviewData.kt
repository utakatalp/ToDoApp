@file:Suppress("MagicNumber", "LongParameterList")

package com.todoapp.mobile.ui.edit

import androidx.annotation.StringRes
import com.todoapp.mobile.ui.edit.EditContract.UiState
import java.time.LocalDate
import java.time.LocalTime

object EditPreviewData {
    fun successState(
        isDirty: Boolean = false,
        isSaving: Boolean = false,
        taskTitle: String = "Sample Task",
        taskTimeStart: LocalTime? = LocalTime.of(9, 0),
        taskTimeEnd: LocalTime? = LocalTime.of(10, 0),
        taskDate: LocalDate = LocalDate.now(),
        taskDescription: String = "Sample description",
        dialogSelectedDate: LocalDate? = LocalDate.now(),
        @StringRes titleError: Int? = null,
    ) = UiState.Success(
        isDirty = isDirty,
        isSaving = isSaving,
        taskTitle = taskTitle,
        taskTimeStart = taskTimeStart,
        taskTimeEnd = taskTimeEnd,
        taskDate = taskDate,
        taskDescription = taskDescription,
        dialogSelectedDate = dialogSelectedDate,
        titleError = titleError,
    )
}
