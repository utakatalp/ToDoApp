package com.todoapp.mobile.ui.home

import com.todoapp.mobile.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

object HomeContract {

    data class GroupSelectionItem(val groupId: Long, val name: String)

    sealed interface UiState {
        data object Loading : UiState
        data class Success(
            val selectedDate: LocalDate,
            val displayedMonth: YearMonth = YearMonth.now(),
            val tasks: List<Task>,
            val completedTaskCountThisWeek: Int,
            val pendingTaskCountThisWeek: Int,
            val isSheetOpen: Boolean,
            val isDeleteDialogOpen: Boolean,
            val isSecretModeEnabled: Boolean,
            val taskFormState: TaskFormState = TaskFormState(),
            val pendingDeleteTask: Task? = null,
            val availableGroups: List<GroupSelectionItem> = emptyList(),
        ) : UiState

        data class Error(
            val message: String,
            val throwable: Throwable? = null,
        ) : UiState
    }

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
        data object OnRetry : UiAction
        data class OnDialogDateSelect(val date: LocalDate) : UiAction
        data class OnMoveTask(
            val from: Int,
            val to: Int,
        ) : UiAction

        data object OnPomodoroTap : UiAction
        data object OnToggleAdvancedSettings : UiAction
        data class OnTaskSecretChange(val isSecret: Boolean) : UiAction
        data class OnTaskClick(val task: Task) : UiAction
        data object OnSuccessfulBiometricAuthenticationHandle : UiAction
        data class OnToggleTaskSecret(val task: Task) : UiAction
        data class OnBiometricSuccessForSecretToggle(val task: Task) : UiAction
        data object OnUndoDelete : UiAction
        data object OnCompletedStatCardTap : UiAction
        data object OnPendingStatCardTap : UiAction
        data class OnGroupSelectionChanged(val groupId: Long?) : UiAction
        data object OnPreviousMonth : UiAction
        data object OnNextMonth : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(val message: String) : UiEffect
        data object ShowBiometricAuthenticator : UiEffect
        data class ShowBiometricForSecretToggle(val task: Task) : UiEffect
        data class ShowError(val message: String) : UiEffect
    }
}
