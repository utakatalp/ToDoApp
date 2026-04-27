package com.todoapp.mobile.ui.home

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.common.move
import com.todoapp.mobile.common.needsOverlayPermission
import com.todoapp.mobile.common.needsPostNotificationsPermission
import com.todoapp.mobile.data.repository.DataStoreHelper
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
import com.todoapp.mobile.ui.settings.PermissionType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val taskRepository: TaskRepository,
    private val taskSyncRepository: TaskSyncRepository,
    private val secretModePreferences: SecretPreferences,
    private val alarmScheduler: AlarmScheduler,
    private val pomodoroEngine: PomodoroEngine,
    private val groupRepository: GroupRepository,
    private val dataStoreHelper: DataStoreHelper,
    @ApplicationContext private val appContext: Context,
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

    override fun onCleared() {
        super.onCleared()
        val previous = (_uiState.value as? UiState.Success)?.pendingDeleteTask ?: return
        if (pendingDeleteJob?.isActive != true) return
        pendingDeleteJob?.cancel()
        CoroutineScope(SupervisorJob()).launch { taskRepository.delete(previous) }
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
            is UiAction.OnReminderOffsetChange -> changeReminderOffset(uiAction.minutes)
            is UiAction.OnCategoryChange -> changeCategory(uiAction.category)
            is UiAction.OnCustomCategoryNameChange -> changeCustomCategoryName(uiAction.name)
            is UiAction.OnRecurrenceChange -> changeRecurrence(uiAction.recurrence)
            is UiAction.OnFilterChange -> changeFilter(uiAction.filter)
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
            is UiAction.OnGroupSelectionChanged ->
                updateSuccessState {
                    it.copy(taskFormState = it.taskFormState.copy(selectedGroupId = uiAction.groupId))
                }
            is UiAction.OnPendingPhotoAdd ->
                updateSuccessState { s ->
                    s.copy(
                        taskFormState =
                        s.taskFormState.copy(
                            pendingPhotos = s.taskFormState.pendingPhotos + PendingPhoto(uiAction.bytes, uiAction.mimeType),
                        ),
                    )
                }
            is UiAction.OnPendingPhotoRemove ->
                updateSuccessState { s ->
                    s.copy(
                        taskFormState =
                        s.taskFormState.copy(
                            pendingPhotos = s.taskFormState.pendingPhotos.filterIndexed { i, _ -> i != uiAction.index },
                        ),
                    )
                }
            is UiAction.DismissPermission -> dismissPermission(uiAction.type)
            is UiAction.PermissionGranted -> dismissPermission(uiAction.type)
            is UiAction.RefreshPermissions -> refreshPermissions()
        }
    }

    private fun refreshPermissions() {
        viewModelScope.launch {
            val pending = dataStoreHelper.observeFirstLoginPermissionPromptPending().first()
            val list =
                if (!pending) {
                    emptyList()
                } else {
                    buildList {
                        if (appContext.needsOverlayPermission()) add(PermissionType.OVERLAY)
                        if (appContext.needsPostNotificationsPermission()) add(PermissionType.NOTIFICATION)
                    }
                }
            updateSuccessState { it.copy(pendingPermissions = list) }
            if (pending && list.isEmpty()) {
                dataStoreHelper.setFirstLoginPermissionPromptPending(false)
            }
        }
    }

    private fun dismissPermission(type: PermissionType) {
        viewModelScope.launch {
            updateSuccessState { state ->
                state.copy(pendingPermissions = state.pendingPermissions - type)
            }
            val remaining = (_uiState.value as? UiState.Success)?.pendingPermissions.orEmpty()
            if (remaining.isEmpty()) {
                dataStoreHelper.setFirstLoginPermissionPromptPending(false)
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
                _uiState.value =
                    UiState.Error(
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
        fetchJob =
            viewModelScope.launch {
                delay(LOADING_DELAY)
                combine(
                    taskRepository.observeTasksByDate(date, includeRecurringInstances = false),
                    taskRepository.observePendingTasksInAWeek(date),
                    taskRepository.countCompletedTasksInAWeek(date),
                    taskRepository.observeTaskPhotoUrls(),
                ) { tasks, pendingTaskCount, completedTaskCount, photoUrls ->
                    val withPhotos =
                        tasks.map { t ->
                            val urls = t.remoteId?.let { photoUrls[it] } ?: emptyList()
                            if (urls.isNotEmpty()) t.copy(photoUrls = urls) else t
                        }
                    timber.log.Timber.tag("TaskFetch").d(
                        "Home tasks=${withPhotos.size}, with photos=${withPhotos.count { it.photoUrls.isNotEmpty() }}, " +
                            "in-mem map size=${photoUrls.size}",
                    )
                    DailyData(withPhotos, pendingTaskCount, completedTaskCount)
                }.collect { data ->
                    var becameSuccess = false
                    _uiState.update { current ->
                        when (current) {
                            is UiState.Success ->
                                current.copy(
                                    tasks = data.tasks,
                                    pendingTaskCountThisWeek = data.pendingTaskCountThisWeek,
                                    completedTaskCountThisWeek = data.completedTaskCountThisWeek,
                                )

                            else -> {
                                becameSuccess = true
                                createInitialState(date, data)
                            }
                        }
                    }
                    if (becameSuccess) refreshPermissions()
                }
            }
    }

    private fun createInitialState(
        date: LocalDate,
        data: DailyData,
    ) = UiState.Success(
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
        val newMonth = current.displayedMonth.plusMonths(1)
        val newDate = if (newMonth == YearMonth.now()) LocalDate.now() else newMonth.atDay(1)
        updateSuccessState { it.copy(displayedMonth = newMonth, selectedDate = newDate) }
        fetchDailyTask(newDate)
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
                    description = form.taskDescription.ifBlank { null },
                    date = form.dialogSelectedDate!!,
                    timeStart = form.taskTimeStart!!,
                    timeEnd = form.taskTimeEnd!!,
                    isCompleted = false,
                    isSecret = form.isTaskSecret,
                    reminderOffsetMinutes = form.reminderOffsetMinutes,
                    category = form.selectedCategory,
                    customCategoryName = form.customCategoryName.takeIf {
                        form.selectedCategory == com.todoapp.mobile.domain.model.TaskCategory.OTHER &&
                            it.isNotBlank()
                    },
                    recurrence = form.selectedRecurrence,
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
            val task = uiAction.task
            if (task.recurrence != com.todoapp.mobile.domain.model.Recurrence.NONE) {
                val state = _uiState.value as? UiState.Success
                val date = state?.selectedDate ?: LocalDate.now()
                taskRepository.setInstanceCompletion(
                    taskId = task.id,
                    date = date,
                    completed = !task.isCompleted,
                )
            } else {
                taskRepository.updateTaskCompletion(
                    task.id,
                    isCompleted = !task.isCompleted,
                )
            }
        }
    }

    private fun deleteTask(task: Task) {
        flushPendingDelete()
        updateSuccessState { it.copy(pendingDeleteTask = task) }
        pendingDeleteJob =
            viewModelScope.launch {
                delay(UNDO_DELAY_MS)
                taskRepository.delete(task)
                updateSuccessState { it.copy(pendingDeleteTask = null) }
            }
    }

    private fun flushPendingDelete() {
        val previous = (_uiState.value as? UiState.Success)?.pendingDeleteTask ?: return
        if (pendingDeleteJob?.isActive != true) return
        pendingDeleteJob?.cancel()
        pendingDeleteJob = null
        updateSuccessState { it.copy(pendingDeleteTask = null) }
        viewModelScope.launch { taskRepository.delete(previous) }
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
                ).onFailure { t ->
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
        updateSuccessState {
            it.copy(
                taskFormState = it.taskFormState.copy(taskDescription = uiAction.description),
            )
        }
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

    private fun changeReminderOffset(minutes: Long?) {
        updateSuccessState {
            it.copy(taskFormState = it.taskFormState.copy(reminderOffsetMinutes = minutes))
        }
    }

    private fun changeCategory(category: com.todoapp.mobile.domain.model.TaskCategory) {
        updateSuccessState { state ->
            val form = state.taskFormState
            // BIRTHDAY auto-defaults to YEARLY when the user hasn't picked one;
            // moving off BIRTHDAY reverts that auto-set so the explainer doesn't linger.
            val nextRecurrence = when {
                category == com.todoapp.mobile.domain.model.TaskCategory.BIRTHDAY &&
                    form.selectedRecurrence == com.todoapp.mobile.domain.model.Recurrence.NONE ->
                    com.todoapp.mobile.domain.model.Recurrence.YEARLY

                form.selectedCategory == com.todoapp.mobile.domain.model.TaskCategory.BIRTHDAY &&
                    category != com.todoapp.mobile.domain.model.TaskCategory.BIRTHDAY &&
                    form.selectedRecurrence == com.todoapp.mobile.domain.model.Recurrence.YEARLY ->
                    com.todoapp.mobile.domain.model.Recurrence.NONE

                else -> form.selectedRecurrence
            }
            state.copy(
                taskFormState = form.copy(
                    selectedCategory = category,
                    selectedRecurrence = nextRecurrence,
                    customCategoryName = if (category == com.todoapp.mobile.domain.model.TaskCategory.OTHER) {
                        form.customCategoryName
                    } else {
                        ""
                    },
                ),
            )
        }
    }

    private fun changeCustomCategoryName(name: String) {
        updateSuccessState {
            it.copy(taskFormState = it.taskFormState.copy(customCategoryName = name))
        }
    }

    private fun changeRecurrence(recurrence: com.todoapp.mobile.domain.model.Recurrence) {
        updateSuccessState { state ->
            val form = state.taskFormState
            // Recurring tasks anchor on a date (start date). Default to today if none set.
            val anchor = form.dialogSelectedDate
                ?: if (recurrence != com.todoapp.mobile.domain.model.Recurrence.NONE) LocalDate.now() else null
            state.copy(
                taskFormState = form.copy(
                    selectedRecurrence = recurrence,
                    dialogSelectedDate = anchor,
                ),
            )
        }
    }

    private fun changeFilter(filter: HomeContract.HomeFilter) {
        val state = _uiState.value as? UiState.Success ?: return
        if (state.selectedFilter == filter) return
        updateSuccessState { it.copy(selectedFilter = filter) }
        // Restart the data flow with the new filter source.
        when (filter) {
            HomeContract.HomeFilter.TODAY -> fetchDailyTask(state.selectedDate)
            else -> fetchByRecurrence(filterToRecurrence(filter))
        }
    }

    private fun filterToRecurrence(filter: HomeContract.HomeFilter): com.todoapp.mobile.domain.model.Recurrence = when (filter) {
        HomeContract.HomeFilter.TODAY -> com.todoapp.mobile.domain.model.Recurrence.NONE
        HomeContract.HomeFilter.DAILY -> com.todoapp.mobile.domain.model.Recurrence.DAILY
        HomeContract.HomeFilter.WEEKLY -> com.todoapp.mobile.domain.model.Recurrence.WEEKLY
        HomeContract.HomeFilter.MONTHLY -> com.todoapp.mobile.domain.model.Recurrence.MONTHLY
        HomeContract.HomeFilter.YEARLY -> com.todoapp.mobile.domain.model.Recurrence.YEARLY
    }

    private fun fetchByRecurrence(recurrence: com.todoapp.mobile.domain.model.Recurrence) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            taskRepository.observeRecurringByType(recurrence).collect { tasks ->
                updateSuccessState { it.copy(tasks = tasks) }
            }
        }
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

    private fun scheduleTaskReminders(task: Task) {
        // Recurring tasks are scheduled by TaskRepositoryImpl via AlarmScheduler.scheduleRecurring
        // and self-rescheduled in AlarmFireReceiver — skip the one-shot path here.
        if (task.recurrence != com.todoapp.mobile.domain.model.Recurrence.NONE) return
        val offset = task.reminderOffsetMinutes ?: return // null = user opted out
        viewModelScope.launch(Dispatchers.IO) {
            alarmScheduler.schedule(
                task.toAlarmItem(remindBeforeMinutes = offset),
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

    private fun showBottomSheet() {
        updateSuccessState { it.copy(isSheetOpen = true) }
        viewModelScope.launch {
            groupRepository
                .observeAllGroups()
                .collect { groups ->
                    val items =
                        groups.mapNotNull { group ->
                            group.remoteId?.let { remoteId ->
                                HomeContract.GroupSelectionItem(
                                    remoteId,
                                    group.name,
                                )
                            }
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
                taskFormState =
                it.taskFormState.copy(
                    isAdvancedSettingsExpanded = !it.taskFormState.isAdvancedSettingsExpanded,
                ),
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
            val isActive =
                secretModePreferences
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

    companion object {
        private const val LOADING_DELAY = 200L
        private const val UNDO_DELAY_MS = 5000L
    }
}
