package com.todoapp.mobile.ui.home

import com.todoapp.mobile.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime

object HomeContract {
    data class UiState(
        val selectedDate: LocalDate = LocalDate.now(),
        val tasks: List<Task> = emptyList(),
        val completedTaskCountThisWeek: Int = 0,
        val pendingTaskCountThisWeek: Int = 0,
        val dialogSelectedDate: LocalDate? = null,
        val taskTitle: String = "",
        val taskTimeStart: LocalTime? = null,
        val taskTimeEnd: LocalTime? = null,
        val taskDate: LocalDate = LocalDate.now(),
        val taskDescription: String = "",
        val isSheetOpen: Boolean = false,
        val isDeleteDialogOpen: Boolean = false,
        val isAdvancedSettingsExpanded: Boolean = false,
        val isTaskSecret: Boolean = false,
        val isSecretModeEnabled: Boolean = true,
        val isTitleError: Boolean = false,
        val isTimeError: Boolean = false,
        val isDateError: Boolean = false,
    )

    sealed interface UiAction {
        data class OnDateSelect(val date: LocalDate) : UiAction
        data object OnDialogDateDeselect : UiAction
        data class OnTaskCheck(val task: Task) : UiAction
        data class OnTaskTitleChange(val title: String) : UiAction
        data class OnTaskTimeStartChange(val time: LocalTime) : UiAction
        data class OnTaskTimeEndChange(val time: LocalTime) : UiAction
        data class OnTaskDateChange(val date: LocalDate) : UiAction
        data class OnTaskDescriptionChange(val description: String) : UiAction
        data object OnShowBottomSheet : UiAction
        data object OnDismissBottomSheet : UiAction
        data object OnTaskCreate : UiAction
        data class OnTaskLongPress(val task: Task) : UiAction
        data object OnDeleteDialogDismiss : UiAction
        data object OnDeleteDialogConfirm : UiAction
        data class OnDialogDateSelect(val date: LocalDate) : UiAction
        data class OnMoveTask(
            val from: Int,
            val to: Int
        ) : UiAction
        data object OnPomodoroTap : UiAction
        object OnToggleAdvancedSettings : UiAction
        data class OnTaskSecretChange(val isSecret: Boolean) : UiAction
        data class OnTaskClick(val task: Task) : UiAction
        data object OnSuccessfulBiometricAuthenticationHandle : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(val message: String) : UiEffect
        data object ShowBiometricAuthenticator : UiEffect
        data class ShowError(val message: String) : UiEffect
    }
}
