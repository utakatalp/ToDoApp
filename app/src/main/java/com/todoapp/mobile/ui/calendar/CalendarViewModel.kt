package com.todoapp.mobile.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.maskTitle
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.ui.calendar.CalendarContract.UiAction
import com.todoapp.mobile.ui.calendar.CalendarContract.UiState
import com.todoapp.uikit.components.TaskCardItem
import com.todoapp.uikit.components.TaskDayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Success())
    val uiState = _uiState.asStateFlow()

    init {
        syncTasksWithSelectedDates()
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnFirstDateDeselect -> deselectStartDate()
            is UiAction.OnSecondDateDeselect -> deselectEndDate()
            is UiAction.OnFirstDateSelect -> updateStartDate(uiAction)
            is UiAction.OnSecondDateSelect -> updateEndDate(uiAction)
            is UiAction.OnRetry -> retry()
        }
    }

    private inline fun updateSuccessState(crossinline transform: (UiState.Success) -> UiState.Success) {
        _uiState.update { currentState ->
            when (currentState) {
                is UiState.Success -> transform(currentState)
                else -> currentState
            }
        }
    }

    private fun retry() {
        _uiState.value = UiState.Success()
        syncTasksWithSelectedDates()
    }

    private fun syncTasksWithSelectedDates() {
        viewModelScope.launch {
            _uiState
                .filterIsInstance<UiState.Success>()
                .map { it.selectedFirstDate to it.selectedSecondDate }
                .distinctUntilChanged()
                .collectLatest { (startDate, endDate) ->
                    observeTasks(startDate, endDate).collect { tasks ->
                        updateSuccessState {
                            it.copy(taskDayItems = mapTasksToTaskDayItems(tasks))
                        }
                    }
                }
        }
    }

    private fun observeTasks(
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Flow<List<Task>> = when {
        startDate == null && endDate == null -> flowOf(emptyList())
        startDate != null && endDate == null -> taskRepository.observeTasksByDate(startDate)
        startDate == null && endDate != null -> taskRepository.observeTasksByDate(endDate)
        else -> taskRepository.observeRange(startDate!!, endDate!!)
    }

    private fun deselectEndDate() {
        updateSuccessState { it.copy(selectedSecondDate = null) }
    }

    private fun deselectStartDate() {
        updateSuccessState { it.copy(selectedFirstDate = null) }
    }

    private fun updateEndDate(uiAction: UiAction.OnSecondDateSelect) {
        updateSuccessState { it.copy(selectedSecondDate = uiAction.date) }
    }

    private fun updateStartDate(uiAction: UiAction.OnFirstDateSelect) {
        updateSuccessState { state ->
            if (state.selectedSecondDate != null && uiAction.date > state.selectedSecondDate) {
                state.copy(
                    selectedFirstDate = state.selectedSecondDate,
                    selectedSecondDate = uiAction.date
                )
            } else {
                state.copy(selectedFirstDate = uiAction.date)
            }
        }
    }

    private fun mapTasksToTaskDayItems(tasks: List<Task>): List<TaskDayItem> {
        return tasks
            .groupBy { it.date }
            .map { (date, tasks) ->
                TaskDayItem(
                    date = date,
                    tasks = tasks.map { task ->
                        TaskCardItem(
                            if (task.isSecret) task.title.maskTitle() else task.title,
                            task.timeStart.toString(),
                            task.timeEnd.toString(),
                        )
                    }
                )
            }
    }
}
