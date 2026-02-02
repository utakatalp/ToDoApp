package com.todoapp.mobile.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.move
import com.todoapp.mobile.domain.alarm.AlarmScheduler
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
) : ViewModel() {
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

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnTaskCheck -> checkTask(uiAction)
            is UiAction.OnDateSelect -> changeSelectedDate(uiAction)
            is UiAction.OnTaskDateChange -> changeTaskDate(uiAction)
            is UiAction.OnTaskDescriptionChange -> changeTaskDescription(uiAction)
            is UiAction.OnTaskTimeEndChange -> changeTaskTimeEnd(uiAction)
            is UiAction.OnTaskTimeStartChange -> changeTaskTimeStart(uiAction)
            is UiAction.OnTaskTitleChange -> changeTaskTitleChange(uiAction)
            is UiAction.OnDialogDateDeselect -> deselectDate()
            is UiAction.OnShowBottomSheet -> showBottomSheet()
            is UiAction.OnDismissBottomSheet -> dismissBottomSheet()
            is UiAction.OnTaskCreate -> createTask()
            is UiAction.OnTaskLongPress -> onTaskLongPressed(uiAction)
            is UiAction.OnDeleteDialogConfirm -> onDeleteDialogConfirmed()
            is UiAction.OnDeleteDialogDismiss -> closeDialog()
            is UiAction.OnDialogDateSelect -> updateDialogDate(uiAction)
            is UiAction.OnMoveTask -> updateTaskIndices(uiAction)
            is UiAction.OnPomodoroTap -> navigateToPomodoro()
            is UiAction.OnTaskSecretChange -> toggleTaskSecret(uiAction)
            is UiAction.OnToggleAdvancedSettings -> toggleAdvancedSettings()
            is UiAction.OnTaskClick -> openTaskDetail(uiAction.task)
            is UiAction.OnSuccessfulBiometricAuthenticationHandle -> handleSuccessfulBiometricAuthentication()
            is UiAction.OnEditClick -> navigateToEdit(uiAction.task)
            is UiAction.OnRetry -> retry()
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
        loadInitialData()
    }

    private fun fetchDailyTask(date: LocalDate) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            taskRepository.observeTasksByDate(date).collect { list ->
                val currentState = uiState.value
                _uiState.value = when (currentState) {
                    is UiState.Loading -> UiState.Success(
                        selectedDate = date,
                        tasks = list,
                    )

                    is UiState.Success -> currentState.copy(tasks = list)
                    is UiState.Error -> UiState.Success(
                        selectedDate = date,
                        tasks = list,
                    )
                }
            }
        }

        updatePendingTaskAmount(date)
        updateCompletedTaskAmount(date)
    }

    private fun updatePendingTaskAmount(date: LocalDate) {
        viewModelScope.launch {
            taskRepository.observePendingTasksInAWeek(date).collect { amount ->
                updateSuccessState { it.copy(pendingTaskCountThisWeek = amount) }
            }
        }
    }

    private fun updateCompletedTaskAmount(date: LocalDate) {
        viewModelScope.launch {
            taskRepository.countCompletedTasksInAWeek(date).collect { amount ->
                updateSuccessState { it.copy(completedTaskCountThisWeek = amount) }
            }
        }
    }

    private fun changeSelectedDate(uiAction: UiAction.OnDateSelect) {
        updateSuccessState { it.copy(selectedDate = uiAction.date) }
        fetchDailyTask(uiAction.date)
    }

    private fun changeTaskTitleChange(uiAction: UiAction.OnTaskTitleChange) {
        updateSuccessState { it.copy(taskTitle = uiAction.title) }
    }

    private fun changeTaskTimeStart(uiAction: UiAction.OnTaskTimeStartChange) {
        updateSuccessState { it.copy(taskTimeStart = uiAction.time) }
    }

    private fun changeTaskTimeEnd(uiAction: UiAction.OnTaskTimeEndChange) {
        updateSuccessState { it.copy(taskTimeEnd = uiAction.time) }
    }

    private fun changeTaskDescription(uiAction: UiAction.OnTaskDescriptionChange) {
        updateSuccessState { it.copy(taskDescription = uiAction.description) }
    }

    private fun changeTaskDate(uiAction: UiAction.OnTaskDateChange) {
        updateSuccessState { it.copy(taskDate = uiAction.date) }
    }

    private fun deselectDate() {
        updateSuccessState { it.copy(dialogSelectedDate = null) }
    }

    private fun showBottomSheet() {
        updateSuccessState { it.copy(isSheetOpen = true) }
    }

    private fun dismissBottomSheet() {
        updateSuccessState { it.copy(isSheetOpen = false) }
    }

    private fun toggleAdvancedSettings() {
        updateSuccessState { state ->
            state.copy(isAdvancedSettingsExpanded = !state.isAdvancedSettingsExpanded)
        }
    }

    private fun toggleTaskSecret(uiAction: UiAction.OnTaskSecretChange) {
        updateSuccessState {
            it.copy(isTaskSecret = uiAction.isSecret)
        }
    }

    private fun updateTaskIndices(uiAction: UiAction.OnMoveTask) {
        updateSuccessState { state ->
            val list = state.tasks.toMutableList()
            list.move(uiAction.from, uiAction.to)
            state.copy(tasks = list)
        }
    }

    private fun updateDialogDate(uiAction: UiAction.OnDialogDateSelect) {
        viewModelScope.launch { updateSuccessState { it.copy(dialogSelectedDate = uiAction.date) } }
    }

    private fun openDialog() {
        viewModelScope.launch {
            updateSuccessState {
                it.copy(isDeleteDialogOpen = true)
            }
        }
    }

    private fun closeDialog() {
        viewModelScope.launch {
            updateSuccessState {
                it.copy(isDeleteDialogOpen = false)
            }
        }
    }

    private fun onTaskLongPressed(uiAction: UiAction.OnTaskLongPress) {
        selectedTask = uiAction.task
        openDialog()
    }

    private fun onDeleteDialogConfirmed() {
        deleteTask(selectedTask)
        closeDialog()
    }

    private fun createTask() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return

        when {
            currentState.taskTitle.isBlank() -> {
                showTitleError()
                return
            }

            currentState.dialogSelectedDate == null -> {
                showDateError()
                return
            }

            currentState.taskTimeStart == null || currentState.taskTimeEnd == null -> {
                showTimeError()
                return
            }

            currentState.taskTimeStart.isAfter(currentState.taskTimeEnd) -> {
                showTimeError()
                return
            }
        }

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
            taskRepository.insert(task = task)
            scheduleTaskReminders(task)
            flush()
            dismissBottomSheet()
        }
    }

    private fun showTitleError(durationMs: Long = 2000L) {
        showTransientError(durationMs) { state, value -> state.copy(isTitleError = value) }
    }

    private fun showDateError(durationMs: Long = 2000L) {
        showTransientError(
            durationMs = durationMs,
            setFlag = { state, value -> state.copy(isDateError = value) },
        )
    }

    private fun showTimeError(durationMs: Long = 2000L) {
        showTransientError(
            durationMs = durationMs,
            setFlag = { state, value -> state.copy(isTimeError = value) },
        )
    }

    private fun showTransientError(
        durationMs: Long,
        setFlag: (UiState.Success, Boolean) -> UiState.Success,
    ) {
        viewModelScope.launch {
            updateSuccessState { setFlag(it, true) }
            delay(durationMs)
            updateSuccessState { setFlag(it, false) }
        }
    }

    private fun flush() {
        viewModelScope.launch {
            updateSuccessState {
                it.copy(
                    taskTitle = "",
                    taskDescription = "",
                    taskTimeStart = null,
                    taskTimeEnd = null,
                    taskDate = LocalDate.now(),
                )
            }
        }
    }

    private fun checkTask(uiAction: UiAction.OnTaskCheck) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTaskCompletion(uiAction.task.id, isCompleted = !uiAction.task.isCompleted)
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch { taskRepository.delete(task) }
    }

    private fun scheduleTaskReminders(
        task: Task,
        remindBeforeMinutes: List<Long> = DEFAULT_REMINDER_MINUTES,
    ) {
        remindBeforeMinutes.forEach { minutes ->
            alarmScheduler.schedule(task.toAlarmItem(remindBeforeMinutes = minutes))
        }
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
        // taskId argümanı geçilecek Task Detail ekranı bitince (selectedTask.id)
        viewModelScope.launch {
            _navEffect.send(NavigationEffect.Navigate(Screen.Settings))
            // TODO() Task Detail ekranı henüz yapılmadı, oraya navigate edecek.
            //  geçici olarak Settings'e navigate ediyor
        }
    }

    private fun authenticate() {
        _uiEffect.trySend(UiEffect.ShowBiometricAuthenticator)
    }

    private fun handleSuccessfulBiometricAuthentication() {
        viewModelScope.launch {
            val selectedOption = SecretModeReopenOptions.byId(secretModePreferences.getLastSelectedOptionId())
            val condition = SecretModeConditionFactory(clock = Clock.systemDefaultZone()).create(selectedOption)
            secretModePreferences.saveCondition(condition)
            navigateToTaskDetail()
        }
    }

    private fun navigateToEdit(task: Task) {
        _navEffect.trySend(NavigationEffect.NavigateToEdit(task.id))
    }

    private fun navigateToPomodoro() {
        _navEffect.trySend(NavigationEffect.Navigate(Screen.AddPomodoroTimer))
    }

    companion object {
        private val DEFAULT_REMINDER_MINUTES = listOf(
            0L, // at time
            1L, // 1 min before
            2L, // 2 min before
            5L, // 5 min before
            10L, // 10 min before
        )
    }
}
