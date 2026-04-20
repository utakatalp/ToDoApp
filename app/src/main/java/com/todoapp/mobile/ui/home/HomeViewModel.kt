package com.todoapp.mobile.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.move
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.alarm.AlarmType
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.toAlarmItem
import com.todoapp.mobile.domain.repository.GroupRepository
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.domain.repository.TaskSyncRepository
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
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskSyncRepository: TaskSyncRepository,
    private val secretModePreferences: SecretPreferences,
    private val alarmScheduler: AlarmScheduler,
    private val pomodoroEngine: PomodoroEngine,
    private val groupRepository: GroupRepository,
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
    private var pendingDeleteJob: Job? = null

    init {
        taskSyncRepository.fetchTasks()
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
            is UiAction.OnCompletedStatCardTap -> navigateToFilteredTasks(isCompleted = true)
            is UiAction.OnPendingStatCardTap -> navigateToFilteredTasks(isCompleted = false)
            is UiAction.OnSuccessfulBiometricAuthenticationHandle -> handleSuccessfulBiometricAuthentication()
            is UiAction.OnToggleTaskSecret -> toggleExistingTaskSecret(uiAction)
            is UiAction.OnBiometricSuccessForSecretToggle -> performSecretToggle(uiAction)
            is UiAction.OnUndoDelete -> undoDelete()
            is UiAction.OnPreviousMonth -> navigateToPreviousMonth()
            is UiAction.OnNextMonth -> navigateToNextMonth()
            is UiAction.OnGroupSelectionChanged -> updateSuccessState {
                it.copy(taskFormState = it.taskFormState.copy(selectedGroupId = uiAction.groupId))
            }
            is UiAction.OnPendingPhotoAdd -> updateSuccessState { s ->
                s.copy(
                    taskFormState = s.taskFormState.copy(
                        pendingPhotos = s.taskFormState.pendingPhotos + PendingPhoto(uiAction.bytes, uiAction.mimeType),
                    ),
                )
            }
            is UiAction.OnPendingPhotoRemove -> updateSuccessState { s ->
                s.copy(
                    taskFormState = s.taskFormState.copy(
                        pendingPhotos = s.taskFormState.pendingPhotos.filterIndexed { i, _ -> i != uiAction.index },
                    ),
                )
            }
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

    private fun navigateToFilteredTasks(isCompleted: Boolean) {
        val date = (uiState.value as? UiState.Success)?.selectedDate ?: LocalDate.now()
        _navEffect.trySend(NavigationEffect.Navigate(Screen.FilteredTasks(isCompleted, date.toEpochDay())))
    }

    private fun navigateToPomodoro() {
        if (pomodoroEngine.state.value.isRunning) {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro))
        } else {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.PomodoroLaunch))
        }
    }

    private fun fetchDailyTask(date: LocalDate) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            delay(LOADING_DELAY)
            combine(
                taskRepository.observeTasksByDate(date),
                taskRepository.observePendingTasksInAWeek(date),
                taskRepository.countCompletedTasksInAWeek(date),
                taskRepository.observeTaskPhotoUrls(),
            ) { tasks, pendingTaskCount, completedTaskCount, photoUrls ->
                val withPhotos = tasks.map { t ->
                    val urls = t.remoteId?.let { photoUrls[it] } ?: emptyList()
                    if (urls.isNotEmpty()) t.copy(photoUrls = urls) else t
                }
                DailyData(withPhotos, pendingTaskCount, completedTaskCount)
            }.collect { data ->
                _uiState.update { current ->
                    when (current) {
                        is UiState.Success -> current.copy(
                            tasks = data.tasks,
                            pendingTaskCountThisWeek = data.pendingTaskCountThisWeek,
                            completedTaskCountThisWeek = data.completedTaskCountThisWeek,
                        )

                        else -> createInitialState(date, data)
                    }
                }
            }
        }
    }

    private fun createInitialState(date: LocalDate, data: DailyData) = UiState.Success(
        selectedDate = date,
        displayedMonth = YearMonth.from(date),
        tasks = data.tasks,
        completedTaskCountThisWeek = data.completedTaskCountThisWeek,
        pendingTaskCountThisWeek = data.pendingTaskCountThisWeek,
        isSheetOpen = false,
        isDeleteDialogOpen = false,
        isSecretModeEnabled = false,
        taskFormState = TaskFormState(),
    )

    private fun changeSelectedDate(uiAction: UiAction.OnDateSelect) {
        updateSuccessState {
            it.copy(
                selectedDate = uiAction.date,
                displayedMonth = YearMonth.from(uiAction.date),
            )
        }
        fetchDailyTask(uiAction.date)
    }

    private fun navigateToPreviousMonth() {
        val current = _uiState.value as? UiState.Success ?: return
        val newMonth = current.displayedMonth.minusMonths(1)
        val newDate = newMonth.atDay(1)
        updateSuccessState { it.copy(displayedMonth = newMonth, selectedDate = newDate) }
        fetchDailyTask(newDate)
    }

    private fun navigateToNextMonth() {
        val current = _uiState.value as? UiState.Success ?: return
        val thisMonth = YearMonth.now()
        if (current.displayedMonth >= thisMonth) return
        val newMonth = current.displayedMonth.plusMonths(1)
        val newDate = if (newMonth == thisMonth) LocalDate.now() else newMonth.atDay(1)
        updateSuccessState { it.copy(displayedMonth = newMonth, selectedDate = newDate) }
        fetchDailyTask(newDate)
    }

    private fun createTask() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return
        if (!validateTaskForm(currentState)) return

        viewModelScope.launch {
            val form = currentState.taskFormState
            val task = Task(
                title = form.taskTitle,
                description = form.taskDescription.ifBlank { null },
                date = form.dialogSelectedDate!!,
                timeStart = form.taskTimeStart!!,
                timeEnd = form.taskTimeEnd!!,
                isCompleted = false,
                isSecret = form.isTaskSecret
            )
            if (form.selectedGroupId != null) {
                groupRepository.createGroupTask(form.selectedGroupId, task)
            } else if (form.pendingPhotos.isNotEmpty()) {
                taskRepository.insertWithPhotos(
                    task,
                    form.pendingPhotos.map { it.bytes to it.mimeType },
                )
                scheduleTaskReminders(task)
            } else {
                taskRepository.insert(task)
                scheduleTaskReminders(task)
            }
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
        updateSuccessState { it.copy(pendingDeleteTask = task) }
        pendingDeleteJob?.cancel()
        pendingDeleteJob = viewModelScope.launch {
            delay(UNDO_DELAY_MS)
            taskRepository.delete(task)
            updateSuccessState { it.copy(pendingDeleteTask = null) }
        }
    }

    private fun undoDelete() {
        pendingDeleteJob?.cancel()
        pendingDeleteJob = null
        updateSuccessState { it.copy(pendingDeleteTask = null) }
    }

    private fun updateTaskIndices(uiAction: UiAction.OnMoveTask) {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return

        updateSuccessState { state ->
            val list = state.tasks.toMutableList()
            list.move(uiAction.from, uiAction.to)
            state.copy(tasks = list)
        }

        viewModelScope.launch(Dispatchers.IO) {
            taskRepository
                .reorderTasksForDate(
                    date = currentState.selectedDate,
                    fromIndex = uiAction.from,
                    toIndex = uiAction.to,
                )
                .onFailure { t ->
                    Log.e("HomeViewModel", "Failed to persist task reorder", t)
                }
        }
    }

    private fun onTaskLongPressed(uiAction: UiAction.OnTaskLongPress) {
        selectedTask = uiAction.task
        openDeleteDialog()
    }

    private fun changeTaskTitle(uiAction: UiAction.OnTaskTitleChange) {
        updateSuccessState { it.copy(taskFormState = it.taskFormState.copy(taskTitle = uiAction.title)) }
    }

    private fun changeTaskDescription(uiAction: UiAction.OnTaskDescriptionChange) {
        updateSuccessState { it.copy(taskFormState = it.taskFormState.copy(taskDescription = uiAction.description)) }
    }

    private fun changeTaskDate(uiAction: UiAction.OnTaskDateChange) {
        updateSuccessState { it.copy(taskFormState = it.taskFormState.copy(dialogSelectedDate = uiAction.date)) }
    }

    private fun changeTaskTimeStart(uiAction: UiAction.OnTaskTimeStartChange) {
        updateSuccessState { it.copy(taskFormState = it.taskFormState.copy(taskTimeStart = uiAction.time)) }
    }

    private fun onDeleteDialogConfirmed() {
        deleteTask(selectedTask)
        closeDeleteDialog()
    }

    private fun changeTaskTimeEnd(uiAction: UiAction.OnTaskTimeEndChange) {
        updateSuccessState { it.copy(taskFormState = it.taskFormState.copy(taskTimeEnd = uiAction.time)) }
    }

    private fun toggleTaskSecret(uiAction: UiAction.OnTaskSecretChange) {
        updateSuccessState { it.copy(taskFormState = it.taskFormState.copy(isTaskSecret = uiAction.isSecret)) }
    }

    private fun toggleExistingTaskSecret(action: UiAction.OnToggleTaskSecret) {
        _uiEffect.trySend(UiEffect.ShowBiometricForSecretToggle(action.task))
    }

    private fun performSecretToggle(action: UiAction.OnBiometricSuccessForSecretToggle) {
        viewModelScope.launch {
            taskRepository.update(action.task.copy(isSecret = !action.task.isSecret))
        }
    }

    private fun updateDialogDate(uiAction: UiAction.OnDialogDateSelect) {
        updateSuccessState { it.copy(taskFormState = it.taskFormState.copy(dialogSelectedDate = uiAction.date)) }
    }

    private fun deselectDialogDate() {
        updateSuccessState { it.copy(taskFormState = it.taskFormState.copy(dialogSelectedDate = null)) }
    }

    private fun clearTaskForm() {
        updateSuccessState { it.copy(taskFormState = TaskFormState()) }
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
        viewModelScope.launch(Dispatchers.IO) {
            remindBeforeMinutes.forEach { minutes ->
                alarmScheduler.schedule(
                    task.toAlarmItem(remindBeforeMinutes = minutes),
                    type = AlarmType.TASK
                )
            }
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
        viewModelScope.launch {
            groupRepository.observeAllGroups()
                .collect { groups ->
                    val items = groups.mapNotNull { group ->
                        group.remoteId?.let { remoteId -> HomeContract.GroupSelectionItem(remoteId, group.name) }
                    }
                    updateSuccessState { it.copy(availableGroups = items) }
                }
        }
    }

    private fun dismissBottomSheet() {
        updateSuccessState { it.copy(isSheetOpen = false) }
    }

    private fun toggleAdvancedSettings() {
        updateSuccessState {
            it.copy(
                taskFormState = it.taskFormState.copy(
                    isAdvancedSettingsExpanded = !it.taskFormState.isAdvancedSettingsExpanded
                )
            )
        }
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
        private const val UNDO_DELAY_MS = 5000L
    }
}
