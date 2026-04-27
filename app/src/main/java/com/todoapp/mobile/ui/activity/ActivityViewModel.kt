package com.todoapp.mobile.ui.activity

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.alarm.AlarmType
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.model.Recurrence
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.TaskCategory
import com.todoapp.mobile.domain.model.toAlarmItem
import com.todoapp.mobile.domain.repository.ActivityPreferences
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.activity.ActivityContract.BestDay
import com.todoapp.mobile.ui.activity.ActivityContract.CategoryStat
import com.todoapp.mobile.ui.activity.ActivityContract.TrendDirection
import com.todoapp.mobile.ui.activity.ActivityContract.UiAction
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import com.todoapp.mobile.ui.activity.ActivityContract.WeekTrend
import com.todoapp.mobile.ui.home.TaskFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityViewModel
@Inject
constructor(
    private val taskRepository: TaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val pomodoroEngine: PomodoroEngine,
    private val activityPreferences: ActivityPreferences,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private val selectedDateFlow = MutableStateFlow(LocalDate.now())

    init {
        viewModelScope.launch {
            combine(
                selectedDateFlow,
                activityPreferences.observeIncludeRecurring(),
            ) { date, include -> date to include }
                .flatMapLatest { (date, include) -> buildSuccessState(date, include) }
                .catch { e -> _uiState.value = UiState.Error(e.message ?: "Unknown Error", e) }
                .collect { _uiState.value = it }
        }
    }

    private fun buildSuccessState(
        date: LocalDate,
        includeRecurring: Boolean,
    ): kotlinx.coroutines.flow.Flow<UiState.Success> {
        val today = LocalDate.now()
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(6)
        val priorWeekDate = date.minusWeeks(1)

        val weekCountsFlow = combine(
            taskRepository.observePendingTasksInAWeek(date, includeRecurring),
            taskRepository.countCompletedTasksInAWeek(date, includeRecurring),
            taskRepository.countCompletedTasksInAWeek(priorWeekDate, includeRecurring),
        ) { pending, completed, priorCompleted ->
            Triple(pending, completed, priorCompleted)
        }

        val weekBarsFlow = combine(
            taskRepository.observeCompletedCountsByDayInAWeek(date, includeRecurring),
            taskRepository.observePendingCountsByDayInAWeek(date, includeRecurring),
        ) { completedBars, pendingBars -> completedBars to pendingBars }

        val yearFlow = combine(
            taskRepository.observePendingTasksYearToDate(today),
            taskRepository.countCompletedTasksYearToDate(today),
        ) { pending, completed -> pending to completed }

        // Range stream feeds streak (today-back), category breakdown (this week's completions),
        // best day (already derivable from completedBars but reused for category ordering).
        val rangeForWeekFlow = taskRepository.observeRange(weekStart, weekEnd)
        val rangeForStreakFlow = taskRepository.observeRange(today.minusDays(STREAK_LOOKBACK_DAYS), today)

        return combine(
            weekCountsFlow,
            weekBarsFlow,
            yearFlow,
            rangeForWeekFlow,
            rangeForStreakFlow,
        ) { weekCounts, weekBars, year, rangeWeek, rangeStreak ->
            val (weeklyPending, weeklyCompleted, priorWeekCompleted) = weekCounts
            val (weeklyBarValues, weeklyPendingBarValues) = weekBars
            val (yearlyPending, yearlyCompleted) = year

            val (weeklyProgress, weeklyPendingProcess) = calculateProgress(weeklyCompleted, weeklyPending)
            val (yearlyProgress, yearlyPendingProcess) = calculateProgress(yearlyCompleted, yearlyPending)

            val trend = computeTrend(current = weeklyCompleted, prior = priorWeekCompleted)
            val streak = computeStreakDays(today, rangeStreak)
            val bestDay = computeBestDay(weekStart, weeklyBarValues)
            val categories = computeCategoryBreakdown(rangeWeek, includeRecurring)

            val current = _uiState.value
            UiState.Success(
                selectedDate = date,
                weeklyCompleted = weeklyCompleted,
                weeklyPending = weeklyPending,
                weeklyProgress = weeklyProgress,
                weeklyPendingProgress = weeklyPendingProcess,
                weeklyBarValues = weeklyBarValues,
                weeklyPendingBarValues = weeklyPendingBarValues,
                yearlyProgress = yearlyProgress,
                yearlyPendingProgress = yearlyPendingProcess,
                yearlyCompleted = yearlyCompleted,
                yearlyTotal = yearlyCompleted + yearlyPending,
                includeRecurring = includeRecurring,
                weekTrend = trend,
                streakDays = streak,
                bestDay = bestDay,
                categoryBreakdown = categories,
                isSheetOpen = if (current is UiState.Success) current.isSheetOpen else false,
                taskFormState = if (current is UiState.Success) current.taskFormState else TaskFormState(),
            )
        }
    }

    private fun computeTrend(current: Int, prior: Int): WeekTrend? {
        if (current == 0 && prior == 0) return null
        if (prior == 0) return WeekTrend(TrendDirection.UP, percentDelta = 100)
        val delta = ((current - prior).toFloat() / prior * 100f).roundToInt()
        val direction = when {
            delta > 0 -> TrendDirection.UP
            delta < 0 -> TrendDirection.DOWN
            else -> TrendDirection.FLAT
        }
        return WeekTrend(direction, percentDelta = abs(delta))
    }

    // Walks back day-by-day from `today` looking at task completions. The streak ends at the first
    // day with zero completed tasks. We cap the lookback at STREAK_LOOKBACK_DAYS to bound work.
    private fun computeStreakDays(today: LocalDate, rangeTasks: List<Task>): Int {
        val completedByDay = rangeTasks
            .filter { it.isCompleted }
            .groupingBy { it.date }
            .eachCount()
        var streak = 0
        var cursor = today
        while (streak < STREAK_LOOKBACK_DAYS.toInt()) {
            val count = completedByDay[cursor] ?: 0
            if (count <= 0) break
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private fun computeBestDay(weekStart: LocalDate, weeklyBarValues: List<Int>): BestDay? {
        if (weeklyBarValues.isEmpty()) return null
        val maxIndex = weeklyBarValues.withIndex().maxByOrNull { it.value } ?: return null
        if (maxIndex.value <= 0) return null
        val day = weekStart.plusDays(maxIndex.index.toLong()).dayOfWeek
        return BestDay(day = day, count = maxIndex.value)
    }

    private fun computeCategoryBreakdown(
        weekTasks: List<Task>,
        includeRecurring: Boolean,
    ): List<CategoryStat> {
        val pool = if (includeRecurring) weekTasks else weekTasks.filter { it.recurrence == Recurrence.NONE }
        val completed = pool.filter { it.isCompleted }
        if (completed.isEmpty()) return emptyList()
        return completed
            .groupBy { it.category to (if (it.category == TaskCategory.OTHER) it.customCategoryName else null) }
            .map { (key, tasks) -> CategoryStat(key.first, key.second?.takeIf { it.isNotBlank() }, tasks.size) }
            .sortedByDescending { it.count }
            .take(MAX_CATEGORY_ROWS)
    }

    fun onAction(action: UiAction) {
        when (action) {
            UiAction.OnRetry -> retry()
            is UiAction.OnWeekSelected -> selectedDateFlow.value = action.date
            UiAction.OnShowBottomSheet -> updateSuccessState { it.copy(isSheetOpen = true) }
            UiAction.OnDismissBottomSheet -> updateSuccessState { it.copy(isSheetOpen = false) }
            UiAction.OnTaskCreate -> createTask()
            is UiAction.OnTaskTitleChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(taskTitle = action.title))
                }
            is UiAction.OnDialogDateSelect ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(dialogSelectedDate = action.date))
                }
            UiAction.OnDialogDateDeselect ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(dialogSelectedDate = null))
                }
            is UiAction.OnTaskTimeStartChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(taskTimeStart = action.time))
                }
            is UiAction.OnTaskTimeEndChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(taskTimeEnd = action.time))
                }
            is UiAction.OnTaskDescriptionChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(taskDescription = action.description))
                }
            UiAction.OnToggleAdvancedSettings ->
                updateSuccessState {
                    it.copy(
                        taskFormState =
                        it.taskFormState.copy(
                            isAdvancedSettingsExpanded = !it.taskFormState.isAdvancedSettingsExpanded,
                        ),
                    )
                }
            is UiAction.OnTaskSecretChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(isTaskSecret = action.isSecret))
                }
            UiAction.OnPomodoroTap -> navigateToPomodoro()
            UiAction.OnCompletedStatCardTap -> navigateToFilteredTasks(isCompleted = true)
            UiAction.OnPendingStatCardTap -> navigateToFilteredTasks(isCompleted = false)
            is UiAction.OnToggleIncludeRecurring ->
                viewModelScope.launch { activityPreferences.setIncludeRecurring(action.include) }
        }
    }

    private fun navigateToFilteredTasks(isCompleted: Boolean) {
        val date = selectedDateFlow.value
        _navEffect.trySend(NavigationEffect.Navigate(Screen.FilteredTasks(isCompleted, date.toEpochDay())))
    }

    private fun navigateToPomodoro() {
        if (pomodoroEngine.state.value.isRunning) {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro))
        } else {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.AddPomodoroTimer))
        }
    }

    private fun createTask() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return
        if (!validateTaskForm(currentState)) return

        viewModelScope.launch {
            val form = currentState.taskFormState
            val task =
                Task(
                    title = form.taskTitle,
                    description = form.taskDescription,
                    date = form.dialogSelectedDate!!,
                    timeStart = form.taskTimeStart!!,
                    timeEnd = form.taskTimeEnd!!,
                    isCompleted = false,
                    isSecret = form.isTaskSecret,
                )
            taskRepository.insert(task)
            scheduleTaskReminders(task)
            updateSuccessState { it.copy(taskFormState = TaskFormState(), isSheetOpen = false) }
        }
    }

    private fun validateTaskForm(state: UiState.Success): Boolean {
        val form = state.taskFormState
        return when {
            form.taskTitle.isBlank() -> {
                showTransientError(R.string.error_task_title_required) { s, v ->
                    s.copy(taskFormState = s.taskFormState.copy(titleErrorRes = v))
                }
                false
            }
            form.dialogSelectedDate == null -> {
                showTransientError(R.string.error_task_date_required) { s, v ->
                    s.copy(taskFormState = s.taskFormState.copy(dateErrorRes = v))
                }
                false
            }
            form.taskTimeStart == null || form.taskTimeEnd == null -> {
                showTransientError(R.string.error_task_time_required) { s, v ->
                    s.copy(taskFormState = s.taskFormState.copy(timeErrorRes = v))
                }
                false
            }
            form.taskTimeStart.isAfter(form.taskTimeEnd) -> {
                showTransientError(R.string.error_task_end_before_start) { s, v ->
                    s.copy(taskFormState = s.taskFormState.copy(timeErrorRes = v))
                }
                false
            }
            else -> true
        }
    }

    private fun scheduleTaskReminders(
        task: Task,
        remindBeforeMinutes: List<Long> = DEFAULT_REMINDER_MINUTES,
    ) {
        remindBeforeMinutes.forEach { minutes ->
            alarmScheduler.schedule(
                task.toAlarmItem(remindBeforeMinutes = minutes),
                type = AlarmType.TASK,
            )
        }
    }

    private fun showTransientError(
        @StringRes errorRes: Int,
        durationMs: Long = 2000L,
        setErrorRes: (UiState.Success, Int?) -> UiState.Success,
    ) {
        viewModelScope.launch {
            updateSuccessState { setErrorRes(it, errorRes) }
            delay(durationMs)
            updateSuccessState { setErrorRes(it, null) }
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
        _uiState.value = UiState.Loading
        selectedDateFlow.value = selectedDateFlow.value
    }

    private fun calculateProgress(
        completed: Int,
        pending: Int,
    ): Pair<Float, Float> {
        val total = completed + pending
        return if (total > 0) {
            completed.toFloat() / total to pending.toFloat() / total
        } else {
            0f to 0f
        }
    }

    companion object {
        private val DEFAULT_REMINDER_MINUTES = listOf(0L, 1L, 2L, 5L, 10L)
        private const val STREAK_LOOKBACK_DAYS = 60L
        private const val MAX_CATEGORY_ROWS = 4
    }
}
