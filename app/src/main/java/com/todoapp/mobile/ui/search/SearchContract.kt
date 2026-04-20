package com.todoapp.mobile.ui.search

import com.todoapp.mobile.domain.model.Group
import com.todoapp.mobile.domain.model.GroupTask
import com.todoapp.mobile.domain.model.Task

object SearchContract {

    enum class SearchFilter { ALL, TASKS, GROUPS, GROUP_TASKS }

    sealed interface SearchResultItem {
        data class PersonalTask(val task: Task) : SearchResultItem
        data class GroupHeader(val group: Group) : SearchResultItem
        data class GroupTaskResult(val group: Group, val groupTask: GroupTask) : SearchResultItem
    }

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(
            val results: List<SearchResultItem>,
            val query: String,
            val activeFilter: SearchFilter = SearchFilter.ALL,
        ) : UiState
        data class Error(val message: String) : UiState
    }

    sealed interface UiAction {
        data class OnQueryChange(val query: String) : UiAction
        data class OnFilterChange(val filter: SearchFilter) : UiAction
        data class OnTaskClick(val task: Task) : UiAction
        data class OnTaskCheck(val task: Task) : UiAction
        data class OnGroupClick(val group: Group) : UiAction
        data class OnGroupTaskClick(val group: Group, val groupTask: GroupTask) : UiAction
        data object OnBiometricSuccess : UiAction
        data object OnBack : UiAction
    }

    sealed interface UiEffect {
        data object ShowBiometricAuthenticator : UiEffect
    }
}
