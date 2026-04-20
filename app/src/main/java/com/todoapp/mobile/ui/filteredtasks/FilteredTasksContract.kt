package com.todoapp.mobile.ui.filteredtasks

import com.todoapp.mobile.domain.model.Task
import java.time.LocalDate

object FilteredTasksContract {
    enum class TaskTab { DONE, PENDING }

    enum class SortOrder { ASC, DESC }

    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val tasks: List<Task>,
            val selectedTab: TaskTab,
            val selectedWeekDate: LocalDate,
            val sortOrder: SortOrder = SortOrder.ASC,
            val pendingDeleteTask: Task? = null,
            val isDeleteDialogOpen: Boolean = false,
        ) : UiState

        data class Error(
            val message: String,
        ) : UiState
    }

    sealed interface UiAction {
        data class OnTabSelect(
            val tab: TaskTab,
        ) : UiAction

        data class OnWeekSelect(
            val date: LocalDate,
        ) : UiAction

        data class OnTaskCheck(
            val task: Task,
        ) : UiAction

        data class OnTaskClick(
            val task: Task,
        ) : UiAction

        data class OnTaskLongPress(
            val task: Task,
        ) : UiAction

        data object OnDeleteDialogConfirm : UiAction

        data object OnDeleteDialogDismiss : UiAction

        data class OnToggleTaskSecret(
            val task: Task,
        ) : UiAction

        data class OnBiometricSuccessForSecretToggle(
            val task: Task,
        ) : UiAction

        data object OnSuccessfulBiometricAuthenticationHandle : UiAction

        data object OnUndoDelete : UiAction

        data object OnBack : UiAction

        data object OnToggleSortOrder : UiAction
    }

    sealed interface UiEffect {
        data class ShowBiometricForSecretToggle(
            val task: Task,
        ) : UiEffect

        data object ShowBiometricAuthenticator : UiEffect
    }
}
