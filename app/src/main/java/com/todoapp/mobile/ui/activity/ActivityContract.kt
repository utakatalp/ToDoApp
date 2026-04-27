package com.todoapp.mobile.ui.activity

import com.todoapp.mobile.domain.model.TaskCategory
import com.todoapp.mobile.ui.home.TaskFormState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

object ActivityContract {
    enum class TrendDirection { UP, DOWN, FLAT }

    data class WeekTrend(
        val direction: TrendDirection,
        val percentDelta: Int,
    )

    data class BestDay(
        val day: DayOfWeek,
        val count: Int,
    )

    data class CategoryStat(
        val category: TaskCategory,
        val customLabel: String?,
        val count: Int,
    )

    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val selectedDate: LocalDate,
            val weeklyCompleted: Int,
            val weeklyPending: Int,
            val weeklyProgress: Float,
            val weeklyPendingProgress: Float,
            val weeklyBarValues: List<Int>,
            val weeklyPendingBarValues: List<Int>,
            val yearlyProgress: Float,
            val yearlyPendingProgress: Float,
            val yearlyCompleted: Int,
            val yearlyTotal: Int,
            val includeRecurring: Boolean = false,
            val weekTrend: WeekTrend? = null,
            val streakDays: Int = 0,
            val bestDay: BestDay? = null,
            val categoryBreakdown: List<CategoryStat> = emptyList(),
            val isSheetOpen: Boolean = false,
            val taskFormState: TaskFormState = TaskFormState(),
        ) : UiState

        data class Error(
            val message: String,
            val throwable: Throwable? = null,
        ) : UiState
    }

    sealed interface UiAction {
        data object OnRetry : UiAction

        data class OnWeekSelected(
            val date: LocalDate,
        ) : UiAction

        data object OnShowBottomSheet : UiAction

        data object OnDismissBottomSheet : UiAction

        data object OnTaskCreate : UiAction

        data class OnTaskTitleChange(
            val title: String,
        ) : UiAction

        data class OnDialogDateSelect(
            val date: LocalDate,
        ) : UiAction

        data object OnDialogDateDeselect : UiAction

        data class OnTaskTimeStartChange(
            val time: LocalTime,
        ) : UiAction

        data class OnTaskTimeEndChange(
            val time: LocalTime,
        ) : UiAction

        data class OnTaskDescriptionChange(
            val description: String,
        ) : UiAction

        data object OnToggleAdvancedSettings : UiAction

        data class OnTaskSecretChange(
            val isSecret: Boolean,
        ) : UiAction

        data object OnPomodoroTap : UiAction

        data object OnCompletedStatCardTap : UiAction

        data object OnPendingStatCardTap : UiAction

        data class OnToggleIncludeRecurring(
            val include: Boolean,
        ) : UiAction
    }

    sealed interface UiEffect
}
