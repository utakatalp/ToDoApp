package com.todoapp.mobile.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.maskTitle
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.alarm.AlarmType
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.toAlarmItem
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.domain.security.SecretModeConditionFactory
import com.todoapp.mobile.domain.security.SecretModeReopenOptions
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.calendar.CalendarContract.UiAction
import com.todoapp.mobile.ui.calendar.CalendarContract.UiEffect
import com.todoapp.mobile.ui.calendar.CalendarContract.UiState
import com.todoapp.mobile.ui.home.TaskFormState
import com.todoapp.uikit.components.TaskCardItem
import com.todoapp.uikit.components.TaskDayItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel
@Inject
constructor(
    private val taskRepository: TaskRepository,
    private val secretModePreferences: SecretPreferences,
    private val alarmScheduler: AlarmScheduler,
    private val pomodoroEngine: PomodoroEngine,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Success())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<UiEffect>()
    val effect = _effect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private var pendingTaskId: Long = -1L

    init {
        syncTasksWithSelectedDate()
        syncTaskDatesForMonth()
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnDateDeselect -> deselectDate()
            is UiAction.OnDateSelect -> updateDate(uiAction)
            is UiAction.OnMonthForward ->
                updateSuccessState {
                    it.copy(
                        selectedMonth = it.selectedMonth.plusMonths(1),
                    )
                }
            is UiAction.OnMonthBack ->
                updateSuccessState {
                    it.copy(
                        selectedMonth = it.selectedMonth.minusMonths(1),
                    )
                }
            is UiAction.OnRetry -> retry()
            is UiAction.OnTaskClick -> navigateToTask(uiAction.taskId)
            is UiAction.OnShowBottomSheet -> showBottomSheet()
            is UiAction.OnDismissBottomSheet -> dismissBottomSheet()
            is UiAction.OnTaskCreate -> createTask()
            is UiAction.OnTaskTitleChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(taskTitle = uiAction.title))
                }
            is UiAction.OnDialogDateSelect ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(dialogSelectedDate = uiAction.date))
                }
            is UiAction.OnDialogDateDeselect ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(dialogSelectedDate = null))
                }
            is UiAction.OnTaskTimeStartChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(taskTimeStart = uiAction.time))
                }
            is UiAction.OnTaskTimeEndChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(taskTimeEnd = uiAction.time))
                }
            is UiAction.OnTaskDescriptionChange ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(taskDescription = uiAction.description))
                }
            is UiAction.OnToggleAdvancedSettings ->
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
                    it.copy(taskFormState = it.taskFormState.copy(isTaskSecret = uiAction.isSecret))
                }
            is UiAction.OnPomodoroTap -> navigateToPomodoro()
            is UiAction.OnSuccessfulBiometricAuthenticationHandle -> handleSuccessfulBiometricAuthentication()
        }
    }

    private fun navigateToTask(taskId: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId) ?: return@launch
            pendingTaskId = taskId

            if (!task.isSecret) {
                navigateToTaskDetail()
                return@launch
            }

            val isActive =
                secretModePreferences
                    .getCondition()
                    .isActive(System.currentTimeMillis())

            if (isActive) navigateToTaskDetail() else _effect.trySend(UiEffect.ShowBiometricAuthenticator)
        }
    }

    private fun navigateToTaskDetail() {
        viewModelScope.launch {
            _navEffect.send(NavigationEffect.Navigate(Screen.Task(pendingTaskId)))
        }
    }

    private fun handleSuccessfulBiometricAuthentication() {
        viewModelScope.launch {
            val selectedOption =
                SecretModeReopenOptions.byId(
                    secretModePreferences.getLastSelectedOptionId(),
                )
            val condition =
                SecretModeConditionFactory(
                    clock = Clock.systemDefaultZone(),
                ).create(selectedOption)
            secretModePreferences.saveCondition(condition)
            navigateToTaskDetail()
        }
    }

    private fun navigateToPomodoro() {
        if (pomodoroEngine.state.value.isRunning) {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro))
        } else {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.AddPomodoroTimer))
        }
    }

    private fun showBottomSheet() {
        updateSuccessState { it.copy(isSheetOpen = true) }
    }

    private fun dismissBottomSheet() {
        updateSuccessState { it.copy(isSheetOpen = false) }
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
        _uiState.value = UiState.Success()
        syncTasksWithSelectedDate()
    }

    private fun syncTasksWithSelectedDate() {
        viewModelScope.launch {
            _uiState
                .filterIsInstance<UiState.Success>()
                .map { it.selectedDate }
                .distinctUntilChanged()
                .collectLatest { date ->
                    observeTasks(date).collect { tasks ->
                        updateSuccessState {
                            it.copy(taskDayItems = mapTasksToTaskDayItems(tasks))
                        }
                    }
                }
        }
    }

    private fun observeTasks(date: LocalDate?): Flow<List<Task>> = when {
        date == null -> flowOf(emptyList())
        else -> taskRepository.observeTasksByDate(date)
    }

    private fun deselectDate() {
        updateSuccessState { it.copy(selectedDate = null) }
    }

    private fun updateDate(uiAction: UiAction.OnDateSelect) {
        updateSuccessState { it.copy(selectedDate = uiAction.date) }
    }

    private fun mapTasksToTaskDayItems(tasks: List<Task>): List<TaskDayItem> = tasks
        .groupBy { it.date }
        .map { (date, tasks) ->
            TaskDayItem(
                date = date,
                tasks =
                tasks.map { task ->
                    TaskCardItem(
                        taskId = task.id,
                        taskTitle = if (task.isSecret) task.title.maskTitle() else task.title,
                        taskTimeStart = task.timeStart.toString(),
                        taskTimeEnd = task.timeEnd.toString(),
                        isCompleted = task.isCompleted,
                        description = task.description,
                    )
                },
            )
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun syncTaskDatesForMonth() {
        viewModelScope.launch {
            _uiState
                .filterIsInstance<UiState.Success>()
                .map { it.selectedMonth }
                .distinctUntilChanged()
                .flatMapLatest { month ->
                    val firstDay = month.atDay(1)
                    val startDate = firstDay.minusDays((firstDay.dayOfWeek.value - 1).toLong())
                    val endDate = startDate.plusDays(34L)
                    taskRepository
                        .observeRange(startDate, endDate)
                        .map { tasks -> tasks.map { it.date }.toSet() }
                }.collect { taskDates ->
                    updateSuccessState { it.copy(taskDatesInMonth = taskDates) }
                }
        }
    }

    companion object {
        private val DEFAULT_REMINDER_MINUTES = listOf(0L, 1L, 2L, 5L, 10L)
    }
}
