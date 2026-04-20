package com.todoapp.mobile.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.model.Group
import com.todoapp.mobile.domain.model.GroupTask
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.GroupRepository
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.domain.security.SecretModeConditionFactory
import com.todoapp.mobile.domain.security.SecretModeReopenOptions
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.search.SearchContract.SearchFilter
import com.todoapp.mobile.ui.search.SearchContract.SearchResultItem
import com.todoapp.mobile.ui.search.SearchContract.UiAction
import com.todoapp.mobile.ui.search.SearchContract.UiEffect
import com.todoapp.mobile.ui.search.SearchContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val taskRepository: TaskRepository,
    private val groupRepository: GroupRepository,
    private val secretPreferences: SecretPreferences,
) : ViewModel() {
    private val queryFlow = MutableStateFlow("")

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: kotlinx.coroutines.flow.Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    private var pendingSecretTask: Task? = null
    private var groupSearchJob: Job? = null

    private var lastPersonalTasks: List<Task> = emptyList()
    private var lastGroupPairs: List<Pair<Group, List<GroupTask>>> = emptyList()
    private var activeFilter: SearchFilter = SearchFilter.ALL

    init {
        observeSearchQuery()
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnQueryChange -> queryFlow.update { action.query }
            is UiAction.OnFilterChange -> applyFilter(action.filter)
            is UiAction.OnTaskClick -> openTaskDetail(action.task)
            is UiAction.OnTaskCheck -> toggleCompletion(action.task)
            is UiAction.OnGroupClick -> navigateToGroup(action.group)
            is UiAction.OnGroupTaskClick -> navigateToGroupTask(action.group, action.groupTask)
            is UiAction.OnBiometricSuccess -> handleSuccessfulBiometricAuthentication()
            is UiAction.OnBack -> _navEffect.trySend(NavigationEffect.Back)
        }
    }

    private fun applyFilter(filter: SearchFilter) {
        activeFilter = filter
        _uiState.update { current ->
            if (current is UiState.Success) {
                current.copy(
                    results = buildResultList(lastPersonalTasks, lastGroupPairs, filter),
                    activeFilter = filter,
                )
            } else {
                current
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            queryFlow
                .debounce(300L)
                .distinctUntilChanged()
                .flatMapLatest { raw ->
                    groupSearchJob?.cancel()
                    activeFilter = SearchFilter.ALL
                    if (raw.isBlank()) {
                        lastPersonalTasks = emptyList()
                        lastGroupPairs = emptyList()
                        _uiState.update { UiState.Idle }
                        flowOf(emptyList<Task>() to "")
                    } else {
                        _uiState.update { UiState.Loading }
                        taskRepository
                            .searchTasks(raw)
                            .map { tasks -> tasks to raw }
                            .catch { e -> _uiState.update { UiState.Error(e.message ?: "Search failed") } }
                    }
                }.collect { (personalTasks, raw) ->
                    if (raw.isNotBlank()) {
                        lastPersonalTasks = personalTasks
                        lastGroupPairs = emptyList()
                        _uiState.update {
                            UiState.Success(
                                results = buildResultList(personalTasks, emptyList(), SearchFilter.ALL),
                                query = raw,
                                activeFilter = SearchFilter.ALL,
                            )
                        }
                        groupSearchJob =
                            viewModelScope.launch {
                                fetchAndMergeGroupResults(raw, personalTasks)
                            }
                    }
                }
        }
    }

    private suspend fun fetchAndMergeGroupResults(
        query: String,
        personalTasks: List<Task>,
    ) {
        groupRepository.searchGroupTasksAcrossGroups(query).onSuccess { groupPairs ->
            lastGroupPairs = groupPairs
            _uiState.update { current ->
                if (current is UiState.Success && current.query == query) {
                    current.copy(
                        results = buildResultList(personalTasks, groupPairs, activeFilter),
                        activeFilter = activeFilter,
                    )
                } else {
                    current
                }
            }
        }
    }

    private fun buildResultList(
        personalTasks: List<Task>,
        groupPairs: List<Pair<Group, List<GroupTask>>>,
        filter: SearchFilter,
    ): List<SearchResultItem> {
        val items = mutableListOf<SearchResultItem>()
        if (filter == SearchFilter.ALL || filter == SearchFilter.TASKS) {
            personalTasks.forEach { items.add(SearchResultItem.PersonalTask(it)) }
        }
        if (filter == SearchFilter.ALL || filter == SearchFilter.GROUPS || filter == SearchFilter.GROUP_TASKS) {
            groupPairs.forEach { (group, tasks) ->
                val includeTasks = filter == SearchFilter.ALL || filter == SearchFilter.GROUP_TASKS
                val includeHeader = filter == SearchFilter.ALL || filter == SearchFilter.GROUPS || tasks.isNotEmpty()
                if (includeHeader) items.add(SearchResultItem.GroupHeader(group))
                if (includeTasks) tasks.forEach { items.add(SearchResultItem.GroupTaskResult(group, it)) }
            }
        }
        return items
    }

    private fun navigateToGroup(group: Group) {
        _navEffect.trySend(
            NavigationEffect.Navigate(Screen.GroupDetail(group.remoteId ?: group.id, group.name)),
        )
    }

    private fun navigateToGroupTask(
        group: Group,
        groupTask: GroupTask,
    ) {
        val groupRemoteId = group.remoteId ?: return
        _navEffect.trySend(
            NavigationEffect.Navigate(Screen.GroupTaskDetail(groupRemoteId, groupTask.id)),
        )
    }

    private fun openTaskDetail(task: Task) {
        if (!task.isSecret) {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.Task(task.id)))
            return
        }
        pendingSecretTask = task
        viewModelScope.launch {
            val isActive =
                secretPreferences
                    .getCondition()
                    .isActive(System.currentTimeMillis())
            if (isActive) {
                _navEffect.trySend(NavigationEffect.Navigate(Screen.Task(task.id)))
            } else {
                _uiEffect.trySend(UiEffect.ShowBiometricAuthenticator)
            }
        }
    }

    private fun handleSuccessfulBiometricAuthentication() {
        viewModelScope.launch {
            val selectedOption =
                SecretModeReopenOptions.byId(
                    secretPreferences.getLastSelectedOptionId(),
                )
            val condition =
                SecretModeConditionFactory(
                    clock = Clock.systemDefaultZone(),
                ).create(selectedOption)
            secretPreferences.saveCondition(condition)
            pendingSecretTask?.let {
                _navEffect.send(NavigationEffect.Navigate(Screen.Task(it.id)))
            }
            pendingSecretTask = null
        }
    }

    private fun toggleCompletion(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTaskCompletion(task.id, isCompleted = !task.isCompleted)
        }
    }
}
