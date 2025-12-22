package com.todoapp.mobile.ui.home

import androidx.lifecycle.ViewModel
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnTaskClick -> {
                checkTask(uiAction)
            }

            is UiAction.OnDateSelect -> {
                changeSelectedDate(uiAction)
            }
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
}
