package com.todoapp.mobile.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.alarm.AlarmType
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.toAlarmItem
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.activity.ActivityContract.UiAction
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
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
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityViewModel
@Inject
constructor(
    private val taskRepository: TaskRepository,
    private val alarmScheduler: AlarmScheduler,
    private val pomodoroEngine: PomodoroEngine,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private val selectedDateFlow = MutableStateFlow(LocalDate.now())

    init {
        viewModelScope.launch {
            selectedDateFlow
                .flatMapLatest { date ->
                    val today = LocalDate.now()
                    combine(
                        combine(
                            taskRepository.observePendingTasksInAWeek(date),
                            taskRepository.countCompletedTasksInAWeek(date),
                        ) { pending, completed -> pending to completed },
                        taskRepository.observeCompletedCountsByDayInAWeek(date),
                        taskRepository.observePendingCountsByDayInAWeek(date),
                        taskRepository.observePendingTasksYearToDate(today),
                        taskRepository.countCompletedTasksYearToDate(today),
                    ) { (weeklyPending, weeklyCompleted), weeklyBarValues, weeklyPendingBarValues, yearlyPending, yearlyCompleted ->
                        val (weeklyProgress, weeklyPendingProcess) =
                            calculateProgress(
                                weeklyCompleted,
                                weeklyPending,
                            )
                        val (yearlyProgress, yearlyPendingProcess) =
                            calculateProgress(
                                yearlyCompleted,
                                yearlyPending,
                            )
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
                            isSheetOpen = if (current is UiState.Success) current.isSheetOpen else false,
                            taskFormState = if (current is UiState.Success) current.taskFormState else TaskFormState(),
                        )
                    }
                }.catch { e -> _uiState.value = UiState.Error(e.message ?: "Unknown Error", e) }
                .collect { _uiState.value = it }
        }
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
                showTransientError { s, v -> s.copy(taskFormState = s.taskFormState.copy(isTitleError = v)) }
                false
            }
            form.dialogSelectedDate == null -> {
                showTransientError { s, v -> s.copy(taskFormState = s.taskFormState.copy(isDateError = v)) }
                false
            }
            form.taskTimeStart == null || form.taskTimeEnd == null -> {
                showTransientError { s, v -> s.copy(taskFormState = s.taskFormState.copy(isTimeError = v)) }
                false
            }
            form.taskTimeStart.isAfter(form.taskTimeEnd) -> {
                showTransientError { s, v -> s.copy(taskFormState = s.taskFormState.copy(isTimeError = v)) }
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
        durationMs: Long = 2000L,
        setFlag: (UiState.Success, Boolean) -> UiState.Success,
    ) {
        viewModelScope.launch {
            updateSuccessState { setFlag(it, true) }
            delay(durationMs)
            updateSuccessState { setFlag(it, false) }
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
    }
}
