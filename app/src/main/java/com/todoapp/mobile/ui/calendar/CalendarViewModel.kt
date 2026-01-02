package com.todoapp.mobile.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.ui.calendar.CalendarContract.UiAction
import com.todoapp.mobile.ui.calendar.CalendarContract.UiState
import com.todoapp.uikit.components.TaskCardItem
import com.todoapp.uikit.components.TaskDayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        // This is intentionally not wrapped to emphasize the single source of truth.
        viewModelScope.launch {
            uiState
                .map { it.selectedFirstDate to it.selectedSecondDate }
                .distinctUntilChanged()
                .collectLatest { (startDate, endDate) ->
                    if (startDate == null || endDate == null) return@collectLatest
                    taskRepository.observeRange(
                        startDate,
                        endDate
                    ).collect { tasks ->
                        _uiState.update { it.copy(taskDayItems = mapTasksToTaskDayItems(tasks)) }
                    }
                }
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnFirstDateDeselect -> deselectStartDate()
            is UiAction.OnSecondDateDeselect -> deselectEndDate()
            is UiAction.OnFirstDateSelect -> updateStartDate(uiAction)
            is UiAction.OnSecondDateSelect -> updateEndDate(uiAction)
        }
    }

    private fun deselectEndDate() {
        _uiState.update { it.copy(selectedSecondDate = null) }
    }

    private fun deselectStartDate() {
        _uiState.update { it.copy(selectedFirstDate = null) }
    }

    private fun updateEndDate(uiAction: UiAction.OnSecondDateSelect) {
        _uiState.update { it.copy(selectedSecondDate = uiAction.date) }
    }

    private fun updateStartDate(uiAction: UiAction.OnFirstDateSelect) {
        if (uiState.value.selectedSecondDate != null && uiAction.date > uiState.value.selectedSecondDate) {
            _uiState.update {
                it.copy(
                    selectedFirstDate = uiState.value.selectedSecondDate,
                    selectedSecondDate = uiAction.date
                )
            }
        } else {
            _uiState.update { it.copy(selectedFirstDate = uiAction.date) }
        }
    }

    private fun mapTasksToTaskDayItems(tasks: List<Task>): List<TaskDayItem> {
        return tasks.groupBy { it.date }.map { (date, tasks) ->
            TaskDayItem(
                date = date,
                tasks = tasks.map { task ->
                    TaskCardItem(
                        task.title,
                        task.timeStart.toString(),
                        task.timeEnd.toString()
                    )
                }
            )
        }
    }
}
