package com.todoapp.mobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.move
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
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiEffect
import com.todoapp.mobile.ui.home.HomeContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val secretModePreferences: SecretPreferences,
    private val alarmScheduler: AlarmScheduler,
    private val pomodoroEngine: PomodoroEngine
) : ViewModel() {

    private data class DailyData(
        val tasks: List<Task>,
        val pendingTaskCountThisWeek: Int,
        val completedTaskCountThisWeek: Int,
    )

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    private lateinit var selectedTask: Task
    private var fetchJob: Job? = null

    init {
        loadInitialData()
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnRetry -> retry()
            is UiAction.OnDateSelect -> changeSelectedDate(uiAction)
            is UiAction.OnTaskCreate -> createTask()
            is UiAction.OnTaskCheck -> checkTask(uiAction)
            is UiAction.OnTaskLongPress -> onTaskLongPressed(uiAction)
            is UiAction.OnDeleteDialogConfirm -> onDeleteDialogConfirmed()
            is UiAction.OnDeleteDialogDismiss -> closeDeleteDialog()
            is UiAction.OnMoveTask -> updateTaskIndices(uiAction)
            is UiAction.OnTaskTitleChange -> changeTaskTitle(uiAction)
            is UiAction.OnTaskDescriptionChange -> changeTaskDescription(uiAction)
            is UiAction.OnTaskDateChange -> changeTaskDate(uiAction)
            is UiAction.OnTaskTimeStartChange -> changeTaskTimeStart(uiAction)
            is UiAction.OnTaskTimeEndChange -> changeTaskTimeEnd(uiAction)
            is UiAction.OnTaskSecretChange -> toggleTaskSecret(uiAction)
            is UiAction.OnDialogDateSelect -> updateDialogDate(uiAction)
            is UiAction.OnDialogDateDeselect -> deselectDialogDate()
            is UiAction.OnShowBottomSheet -> showBottomSheet()
            is UiAction.OnDismissBottomSheet -> dismissBottomSheet()
            is UiAction.OnToggleAdvancedSettings -> toggleAdvancedSettings()
            is UiAction.OnTaskClick -> openTaskDetail(uiAction.task)
            is UiAction.OnPomodoroTap -> navigateToPomodoro()
            is UiAction.OnSuccessfulBiometricAuthenticationHandle -> handleSuccessfulBiometricAuthentication()
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

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                fetchDailyTask(today)
            } catch (e: IOException) {
                _uiState.value = UiState.Error(
                    message = e.message ?: "Unknown error",
                    throwable = e,
                )
            }
        }
    }

    private fun retry() {
        _uiState.value = UiState.Loading
        loadInitialData()
    }

    private fun navigateToPomodoro() {
        if (pomodoroEngine.state.value.isRunning) {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro))
        } else {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.AddPomodoroTimer))
        }
    }

    private fun fetchDailyTask(date: LocalDate) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            delay(LOADING_DELAY)
            combine(
                taskRepository.observeTasksByDate(date),
                taskRepository.observePendingTasksInAWeek(date),
                taskRepository.countCompletedTasksInAWeek(date)
            ) { tasks, pendingTaskCount, completedTaskCount ->
                DailyData(tasks, pendingTaskCount, completedTaskCount)
            }.collect { data ->
                _uiState.update { current ->
                    when (current) {
                        is UiState.Success -> current.copy(
                            tasks = data.tasks,
                            pendingTaskCountThisWeek = data.pendingTaskCountThisWeek,
                            completedTaskCountThisWeek = data.completedTaskCountThisWeek
                        )

                        else -> createInitialState(date, data)
                    }
                }
            }
        }
    }

    private fun createInitialState(date: LocalDate, data: DailyData) = UiState.Success(
        selectedDate = date,
        tasks = data.tasks,
        completedTaskCountThisWeek = data.completedTaskCountThisWeek,
        pendingTaskCountThisWeek = data.pendingTaskCountThisWeek,
        dialogSelectedDate = null,
        taskTitle = "",
        taskTimeStart = null,
        taskTimeEnd = null,
        taskDate = date,
        taskDescription = "",
        isSheetOpen = false,
        isDeleteDialogOpen = false,
        isAdvancedSettingsExpanded = false,
        isTaskSecret = false,
        isSecretModeEnabled = false,
        isTitleError = false,
        isTimeError = false,
        isDateError = false,
    )

    private fun changeSelectedDate(uiAction: UiAction.OnDateSelect) {
        updateSuccessState { it.copy(selectedDate = uiAction.date) }
        fetchDailyTask(uiAction.date)
    }

    private fun createTask() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return
        if (!validateTaskForm(currentState)) return

        viewModelScope.launch {
            val task = Task(
                title = currentState.taskTitle,
                description = currentState.taskDescription,
                date = currentState.dialogSelectedDate!!,
                timeStart = currentState.taskTimeStart!!,
                timeEnd = currentState.taskTimeEnd!!,
                isCompleted = false,
                isSecret = currentState.isTaskSecret
            )
            taskRepository.insert(task)
            scheduleTaskReminders(task)
            clearTaskForm()
            dismissBottomSheet()
        }
    }

    private fun checkTask(uiAction: UiAction.OnTaskCheck) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTaskCompletion(
                uiAction.task.id,
                isCompleted = !uiAction.task.isCompleted
            )
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch { taskRepository.delete(task) }
    }

    private fun updateTaskIndices(uiAction: UiAction.OnMoveTask) {
        updateSuccessState { state ->
            val list = state.tasks.toMutableList()
            list.move(uiAction.from, uiAction.to)
            state.copy(tasks = list)
        }
    }

    private fun onTaskLongPressed(uiAction: UiAction.OnTaskLongPress) {
        selectedTask = uiAction.task
        openDeleteDialog()
    }

    private fun changeTaskTitle(uiAction: UiAction.OnTaskTitleChange) {
        updateSuccessState { it.copy(taskTitle = uiAction.title) }
    }

    private fun changeTaskDescription(uiAction: UiAction.OnTaskDescriptionChange) {
        updateSuccessState { it.copy(taskDescription = uiAction.description) }
    }

    private fun changeTaskDate(uiAction: UiAction.OnTaskDateChange) {
        updateSuccessState { it.copy(taskDate = uiAction.date) }
    }

    private fun changeTaskTimeStart(uiAction: UiAction.OnTaskTimeStartChange) {
        updateSuccessState { it.copy(taskTimeStart = uiAction.time) }
    }

    private fun onDeleteDialogConfirmed() {
        deleteTask(selectedTask)
        closeDeleteDialog()
    }

    private fun changeTaskTimeEnd(uiAction: UiAction.OnTaskTimeEndChange) {
        updateSuccessState { it.copy(taskTimeEnd = uiAction.time) }
    }

    private fun toggleTaskSecret(uiAction: UiAction.OnTaskSecretChange) {
        updateSuccessState { it.copy(isTaskSecret = uiAction.isSecret) }
    }

    private fun updateDialogDate(uiAction: UiAction.OnDialogDateSelect) {
        updateSuccessState { it.copy(dialogSelectedDate = uiAction.date) }
    }

    private fun deselectDialogDate() {
        updateSuccessState { it.copy(dialogSelectedDate = null) }
    }

    private fun clearTaskForm() {
        updateSuccessState {
            it.copy(
                taskTitle = "",
                taskDescription = "",
                taskTimeStart = null,
                taskTimeEnd = null,
                taskDate = it.selectedDate,
            )
        }
    }

    private fun validateTaskForm(state: UiState.Success): Boolean {
        return when {
            state.taskTitle.isBlank() -> {
                showTransientError { s, v -> s.copy(isTitleError = v) }
                false
            }

            state.dialogSelectedDate == null -> {
                showTransientError { s, v -> s.copy(isDateError = v) }
                false
            }

            state.taskTimeStart == null || state.taskTimeEnd == null -> {
                showTransientError { s, v -> s.copy(isTimeError = v) }
                false
            }

            state.taskTimeStart.isAfter(state.taskTimeEnd) -> {
                showTransientError { s, v -> s.copy(isTimeError = v) }
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
                type = AlarmType.TASK
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

    private fun showBottomSheet() {
        updateSuccessState { it.copy(isSheetOpen = true) }
    }

    private fun dismissBottomSheet() {
        updateSuccessState { it.copy(isSheetOpen = false) }
    }

    private fun toggleAdvancedSettings() {
        updateSuccessState { it.copy(isAdvancedSettingsExpanded = !it.isAdvancedSettingsExpanded) }
    }

    private fun openDeleteDialog() {
        updateSuccessState { it.copy(isDeleteDialogOpen = true) }
    }

    private fun closeDeleteDialog() {
        updateSuccessState { it.copy(isDeleteDialogOpen = false) }
    }

    private fun openTaskDetail(task: Task) {
        selectedTask = task

        if (!task.isSecret) {
            navigateToTaskDetail()
            return
        }

        viewModelScope.launch {
            val isActive = secretModePreferences
                .getCondition()
                .isActive(System.currentTimeMillis())

            if (isActive) navigateToTaskDetail() else authenticate()
        }
    }

    private fun navigateToTaskDetail() {
        viewModelScope.launch {
            _navEffect.send(NavigationEffect.Navigate(Screen.Task(selectedTask.id)))
        }
    }

    private fun authenticate() {
        _uiEffect.trySend(UiEffect.ShowBiometricAuthenticator)
    }

    private fun handleSuccessfulBiometricAuthentication() {
        viewModelScope.launch {
            val selectedOption = SecretModeReopenOptions.byId(
                secretModePreferences.getLastSelectedOptionId()
            )
            val condition = SecretModeConditionFactory(
                clock = Clock.systemDefaultZone()
            ).create(selectedOption)
            secretModePreferences.saveCondition(condition)
            navigateToTaskDetail()
        }
    }

    companion object {
        private val DEFAULT_REMINDER_MINUTES = listOf(0L, 1L, 2L, 5L, 10L)
        private const val LOADING_DELAY = 200L
    }
}
