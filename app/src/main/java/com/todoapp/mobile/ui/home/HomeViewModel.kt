package com.todoapp.mobile.ui.home

import androidx.lifecycle.ViewModel
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiEffect
import com.todoapp.mobile.ui.home.HomeContract.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnTaskClick -> {
                checkTask(uiAction)
            }
            is UiAction.OnDateSelect -> {
                changeSelectedDate(uiAction)
            }
            is UiAction.OnTaskDateChange -> changeTaskDate(uiAction)
            is UiAction.OnTaskDescriptionChange -> changeTaskDescription(uiAction)
            is UiAction.OnTaskTimeEndChange -> changeTaskTimeEnd(uiAction)
            is UiAction.OnTaskTimeStartChange -> changeTaskTimeStart(uiAction)
            is UiAction.OnTaskTitleChange -> changeTaskTitleChange(uiAction)
            is UiAction.OnDateDeselect -> deselectDate()
            is UiAction.OnShowBottomSheet -> showBottomSheet()
            is UiAction.OnDismissBottomSheet -> dismissBottomSheet()
        }
    }

    private fun deselectDate() {
        _uiState.update { it.copy(selectedDate = null) }
    }

    private fun changeTaskTitleChange(uiAction: UiAction.OnTaskTitleChange) {
        _uiState.update {
            it.copy(
                taskTitle = uiAction.title,
            )
        }
    }

    private fun changeTaskTimeStart(uiAction: UiAction.OnTaskTimeStartChange) {
        _uiState.update {
            it.copy(
                taskTimeStart = uiAction.time,
            )
        }
    }

    private fun changeTaskTimeEnd(uiAction: UiAction.OnTaskTimeEndChange) {
        _uiState.update { it.copy(taskTimeEnd = uiAction.time) }
    }

    private fun changeTaskDescription(uiAction: UiAction.OnTaskDescriptionChange) {
        _uiState.update {
            it.copy(
                taskDescription = uiAction.description,
            )
        }
    }

    private fun changeTaskDate(uiAction: UiAction.OnTaskDateChange) {
        _uiState.update {
            it.copy(
                taskDate = uiAction.date,
            )
        }
    }

    private fun changeSelectedDate(uiAction: UiAction.OnDateSelect) {
        _uiState.update {
            it.copy(
                selectedDate = uiAction.date,
            )
        }
    }

    private fun checkTask(uiAction: UiAction.OnTaskClick) {
        _uiState.update { state ->
            state.copy(
                tasks =
                    state.tasks.map { task ->
                        if (task.taskId == uiAction.task.taskId) {
                            task.copy(isDone = !task.isDone)
                        } else {
                            task.copy()
                        }
                    },
            )
        }
    }

    private fun showBottomSheet() {
        _uiState.update { it.copy(isSheetOpen = true) }
    }

    private fun dismissBottomSheet() {
        _uiState.update { it.copy(isSheetOpen = false) }
    }
}
