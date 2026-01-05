package com.todoapp.mobile.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

private const val DAYS_IN_WEEK = 7
private val daysRange = 0 until DAYS_IN_WEEK

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        val date = uiState.value.selectedDate
        updateWeeklyProgress(date)
        updateYearlyProgress(date)
    }

    private fun updateWeeklyProgress(date: LocalDate) {
        viewModelScope.launch {
            val pendingFLow = taskRepository.observePendingTasksInAWeek(date)
            val completedFlow = taskRepository.observeCompletedTasksInAWeek(date)

            combine(pendingFLow, completedFlow) { pendingCount, completedCount ->
                val total = pendingCount + completedCount
                if (total <= 0) {
                    0f to 0f
                } else {
                    val completedRatio = completedCount.toFloat() / total.toFloat()
                    val pendingRatio = pendingCount.toFloat() / total.toFloat()
                    completedRatio to pendingRatio
                }
            }.collect { (completedRatio, pendingRatio) ->
                _uiState.update {
                    it.copy(
                        weeklyProgress = completedRatio,
                        weeklyPendingProgress = pendingRatio
                    )
                }
            }
        }
        viewModelScope.launch {
            val weekStart = date.with(DayOfWeek.MONDAY)
            taskRepository.observeCompletedCountsByDayInAWeek(date).collect { pairs ->
                val map = pairs.toMap()
                val values = (daysRange).map { dayOffset ->
                    val d = weekStart.plusDays(dayOffset.toLong())
                    map[d] ?: 0
                }
                _uiState.update { it.copy(weeklyBarValues = values) }
            }
        }
    }

    private fun updateYearlyProgress(date: LocalDate) {
        viewModelScope.launch {
            val pendingFlow = taskRepository.observePendingTasksYearToDate(date)
            val completedFlow = taskRepository.observeCompletedTasksYearToDate(date)

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
                _uiState.update {
                    it.copy(
                        yearlyProgress = completedRatio,
                        yearlyPendingProgress = pendingRatio,
                    )
                }
            }
        }
    }
}
