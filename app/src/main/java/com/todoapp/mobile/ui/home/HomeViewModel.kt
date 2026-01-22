package com.todoapp.mobile.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.move
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiEffect
import com.todoapp.mobile.ui.home.HomeContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }
    private lateinit var selectedTask: Task

    private var fetchJob: Job? = null
    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnTaskClick -> checkTask(uiAction)
            is UiAction.OnDateSelect -> changeSelectedDate(uiAction)
            is UiAction.OnTaskDateChange -> changeTaskDate(uiAction)
            is UiAction.OnTaskDescriptionChange -> changeTaskDescription(uiAction)
            is UiAction.OnTaskTimeEndChange -> changeTaskTimeEnd(uiAction)
            is UiAction.OnTaskTimeStartChange -> changeTaskTimeStart(uiAction)
            is UiAction.OnTaskTitleChange -> changeTaskTitleChange(uiAction)
            is UiAction.OnDialogDateDeselect -> deselectDate()
            is UiAction.OnShowBottomSheet -> showBottomSheet()
            is UiAction.OnDismissBottomSheet -> dismissBottomSheet()
            is UiAction.OnTaskCreate -> createTask()
            is UiAction.OnTaskLongPress -> onTaskLongPressed(uiAction)
            is UiAction.OnDeleteDialogConfirm -> onDeleteDialogConfirmed()
            is UiAction.OnDeleteDialogDismiss -> closeDialog()
            is UiAction.OnDialogDateSelect -> updateDialogDate(uiAction)
            is UiAction.OnMoveTask -> updateTaskIndices(uiAction)
        }
    }

    init {
        fetchDailyTask(uiState.value.selectedDate)
        updatePendingTaskAmount(uiState.value.selectedDate)
        updateCompletedTaskAmount(uiState.value.selectedDate)
    }

    private fun updateTaskIndices(uiAction: UiAction.OnMoveTask) {
        val list = uiState.value.tasks.toMutableList()
        list.move(uiAction.from, uiAction.to)
        _uiState.update { it.copy(tasks = list) }
    }

    private fun updatePendingTaskAmount(date: LocalDate) {
        viewModelScope.launch {
            taskRepository.observePendingTasksInAWeek(date).collect { amount ->
                _uiState.update { it.copy(pendingTaskCountThisWeek = amount) }
            }
        }
    }

    private fun updateCompletedTaskAmount(date: LocalDate) {
        viewModelScope.launch {
            taskRepository.countCompletedTasksInAWeek(date).collect { amount ->
                _uiState.update { it.copy(completedTaskCountThisWeek = amount) }
            }
        }
    }

    private fun updateDialogDate(uiAction: UiAction.OnDialogDateSelect) {
        viewModelScope.launch { _uiState.update { it.copy(dialogSelectedDate = uiAction.date) } }
    }

    private fun onDeleteDialogConfirmed() {
        deleteTask(selectedTask)
        closeDialog()
    }

    private fun onTaskLongPressed(uiAction: UiAction.OnTaskLongPress) {
        selectedTask = uiAction.task
        openDialog()
    }

    private fun closeDialog() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isDeleteDialogOpen = false)
            }
        }
    }

    private fun openDialog() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isDeleteDialogOpen = true)
            }
        }
    }

    private fun createTask() {
        viewModelScope.launch {
            taskRepository.insert(
                task = Task(
                    title = uiState.value.taskTitle,
                    description = uiState.value.taskDescription,
                    date = uiState.value.dialogSelectedDate!!,
                    timeStart = uiState.value.taskTimeStart!!,
                    timeEnd = uiState.value.taskTimeEnd!!,
                    isCompleted = false,
                )
            )
            flush()
            dismissBottomSheet()
        }
    }

    private fun fetchDailyTask(date: LocalDate) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            taskRepository.observeTasksByDate(date).collect { list ->
                _uiState.update { it.copy(tasks = list) }
            }
        }
    }

    private fun flush() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    taskTitle = "",
                    taskDescription = "",
                    taskTimeStart = null,
                    taskTimeEnd = null,
                    taskDate = LocalDate.now(),
                )
            }
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch { taskRepository.delete(task) }
    }

    private fun deselectDate() {
        _uiState.update { it.copy(dialogSelectedDate = null) }
    }

    private fun changeTaskTitleChange(uiAction: UiAction.OnTaskTitleChange) {
        _uiState.update { it.copy(taskTitle = uiAction.title) }
    }

    private fun changeTaskTimeStart(uiAction: UiAction.OnTaskTimeStartChange) {
        _uiState.update { it.copy(taskTimeStart = uiAction.time) }
    }

    private fun changeTaskTimeEnd(uiAction: UiAction.OnTaskTimeEndChange) {
        _uiState.update { it.copy(taskTimeEnd = uiAction.time) }
    }

    private fun changeTaskDescription(uiAction: UiAction.OnTaskDescriptionChange) {
        _uiState.update { it.copy(taskDescription = uiAction.description) }
    }

    private fun changeTaskDate(uiAction: UiAction.OnTaskDateChange) {
        _uiState.update { it.copy(taskDate = uiAction.date) }
    }

    private fun changeSelectedDate(uiAction: UiAction.OnDateSelect) {
        Log.d("HomeViewModel", "changeSelectedDate: ${uiAction.date}")
        _uiState.update { it.copy(selectedDate = uiAction.date) }
        fetchDailyTask(uiAction.date)
    }

    private fun checkTask(uiAction: UiAction.OnTaskClick) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTask(uiAction.task.id, isCompleted = !uiAction.task.isCompleted)
        }
    }

    private fun showBottomSheet() {
        _uiState.update { it.copy(isSheetOpen = true) }
    }

    private fun dismissBottomSheet() {
        _uiState.update { it.copy(isSheetOpen = false) }
    }
}
