package com.todoapp.mobile.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.ui.activity.ActivityContract.UiAction
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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

        viewModelScope.launch {
            delay(LOADING_DELAY)
            combine(
                taskRepository.observePendingTasksInAWeek(date),
                taskRepository.countCompletedTasksInAWeek(date),
                taskRepository.observeCompletedCountsByDayInAWeek(date),
                taskRepository.observePendingTasksYearToDate(date),
                taskRepository.countCompletedTasksYearToDate(date)
            ) { weeklyPending, weeklyCompleted, weeklyBarValues, yearlyPending, yearlyCompleted ->

                val (weeklyProgress, weeklyPendingProcess) = calculateProgress(weeklyCompleted, weeklyPending)
                val (yearlyProgress, yearlyPendingProcess) = calculateProgress(yearlyCompleted, yearlyPending)

                UiState.Success(
                    selectedDate = date,
                    weeklyProgress = weeklyProgress,
                    weeklyPendingProgress = weeklyPendingProcess,
                    weeklyBarValues = weeklyBarValues,
                    yearlyProgress = yearlyProgress,
                    yearlyPendingProgress = yearlyPendingProcess
                )
            }
                .catch { e -> _uiState.value = UiState.Error(e.message ?: "Unknown Error", e) }
                .collect { _uiState.value = it }
        }
    }

    private fun calculateProgress(completed: Int, pending: Int): Pair<Float, Float> {
        val total = completed + pending
        return if (total > 0) {
            completed.toFloat() / total to pending.toFloat() / total
        } else {
            0f to 0f
        }
    }

    companion object {
        private const val LOADING_DELAY = 200L
    }
}
