package com.todoapp.mobile.ui.search

import com.todoapp.mobile.domain.model.Group
import com.todoapp.mobile.domain.model.GroupTask
import com.todoapp.mobile.domain.model.Recurrence
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.TaskCategory

object SearchContract {
    enum class SearchFilter { ALL, TASKS, GROUPS, GROUP_TASKS }

    enum class StatusFilter { ALL, PENDING, COMPLETED }

    enum class DateRangeFilter { ALL_TIME, TODAY, THIS_WEEK, THIS_MONTH }

    data class SearchFilters(
        val resultType: SearchFilter = SearchFilter.ALL,
        val categories: Set<TaskCategory> = emptySet(),
        val recurrences: Set<Recurrence> = emptySet(),
        val status: StatusFilter = StatusFilter.ALL,
        val dateRange: DateRangeFilter = DateRangeFilter.ALL_TIME,
    ) {
        val activeCount: Int = listOf(
            resultType != SearchFilter.ALL,
            categories.isNotEmpty(),
            recurrences.isNotEmpty(),
            status != StatusFilter.ALL,
            dateRange != DateRangeFilter.ALL_TIME,
        ).count { it }
    }

    sealed interface SearchResultItem {
        data class PersonalTask(
            val task: Task,
        ) : SearchResultItem

        data class GroupHeader(
            val group: Group,
        ) : SearchResultItem

        data class GroupTaskResult(
            val group: Group,
            val groupTask: GroupTask,
        ) : SearchResultItem
    }

    sealed interface UiState {
        data object Idle : UiState

        data object Loading : UiState

        data class Success(
            val results: List<SearchResultItem>,
            val query: String,
            val filters: SearchFilters = SearchFilters(),
            val isFilterDialogOpen: Boolean = false,
        ) : UiState

        data class Error(
            val message: String,
        ) : UiState
    }

    sealed interface UiAction {
        data class OnQueryChange(
            val query: String,
        ) : UiAction

        data object OnOpenFilterDialog : UiAction

        data object OnDismissFilterDialog : UiAction

        data class OnApplyFilters(
            val filters: SearchFilters,
        ) : UiAction

        data object OnClearFilters : UiAction

        data class OnTaskClick(
            val task: Task,
        ) : UiAction

        data class OnTaskCheck(
            val task: Task,
        ) : UiAction

        data class OnGroupClick(
            val group: Group,
        ) : UiAction

        data class OnGroupTaskClick(
            val group: Group,
            val groupTask: GroupTask,
        ) : UiAction

        data object OnBiometricSuccess : UiAction

        data object OnBack : UiAction
    }

    sealed interface UiEffect {
        data object ShowBiometricAuthenticator : UiEffect
    }
}
