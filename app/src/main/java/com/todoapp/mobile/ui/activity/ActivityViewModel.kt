package com.todoapp.mobile.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.ui.activity.ActivityContract.UiAction
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onAction(action: UiAction) {
        when (action) {
            UiAction.OnRetry -> retry()
        }
    }

    private fun retry() {
        _uiState.value = UiState.Loading
        loadData()
    }

    private fun loadData() {
        val date = LocalDate.now()
        updateWeeklyProgress(date)
        updateYearlyProgress(date)
    }

    private inline fun updateSuccessState(crossinline transform: (UiState.Success) -> UiState.Success) {
        _uiState.update { currentState ->
            when (currentState) {
                is UiState.Success -> transform(currentState)
                is UiState.Loading -> transform(UiState.Success(selectedDate = LocalDate.now()))
                else -> currentState
            }
        }
    }

    private fun updateWeeklyProgress(date: LocalDate) {
        viewModelScope.launch {
            combine(
                taskRepository.observePendingTasksInAWeek(date),
                taskRepository.countCompletedTasksInAWeek(date)
            ) { pendingCount, completedCount ->
                val total = pendingCount + completedCount
                if (total <= 0) {
                    0f to 0f
                } else {
                    val completedRatio = completedCount.toFloat() / total.toFloat()
                    val pendingRatio = pendingCount.toFloat() / total.toFloat()
                    completedRatio to pendingRatio
                }
            }.collect { (completedRatio, pendingRatio) ->
                updateSuccessState {
                    it.copy(
                        weeklyProgress = completedRatio,
                        weeklyPendingProgress = pendingRatio
                    )
                }
            }
        }

        viewModelScope.launch {
            taskRepository.observeCompletedCountsByDayInAWeek(date).collect { values ->
                updateSuccessState { it.copy(weeklyBarValues = values) }
            }
        }
    }

    private fun updateYearlyProgress(date: LocalDate) {
        viewModelScope.launch {
            val pendingFlow = taskRepository.observePendingTasksYearToDate(date)
            val completedFlow = taskRepository.countCompletedTasksYearToDate(date)

            combine(pendingFlow, completedFlow) { pendingCount, completedCount ->
                val total = pendingCount + completedCount
                if (total <= 0) {
                    0f to 0f
                } else {
                    val completedRatio = completedCount.toFloat() / total.toFloat()
                    val pendingRatio = pendingCount.toFloat() / total.toFloat()
                    completedRatio to pendingRatio
                }
            }.collect { (completedRatio, pendingRatio) ->
                updateSuccessState {
                    it.copy(
                        yearlyProgress = completedRatio,
                        yearlyPendingProgress = pendingRatio,
                    )
                }
            }
        }
    }
}
