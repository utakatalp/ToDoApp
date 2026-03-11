package com.todoapp.mobile.ui.groupdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.data.repository.DataStoreHelper
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.group.GroupManagementRepository
import com.todoapp.mobile.domain.repository.group.GroupTaskRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.groupdetails.GroupDetailsContract.UiAction
import com.todoapp.mobile.ui.groupdetails.GroupDetailsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val taskRepository: GroupTaskRepository,
    private val groupManagementRepository: GroupManagementRepository,
    savedStateHandle: SavedStateHandle,
    dataStoreHelper: DataStoreHelper,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private val _assignedToMeChecked = MutableStateFlow(false)
    val assignedToMeChecked: StateFlow<Boolean> = _assignedToMeChecked.asStateFlow()

    val groupId: Long =
        checkNotNull(savedStateHandle.get<String>("groupId")).toLong()

    val userId: StateFlow<Long?> =
        dataStoreHelper.observeUser()
            .map { it?.id }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )

    val tasks: StateFlow<List<Task.Group>> =
        observeTasksFlow(groupId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

    fun onAction(action: UiAction) {
        when (action) {
            UiAction.OnAllTap -> _assignedToMeChecked.update { false }
            UiAction.OnAssignedToMeTap -> _assignedToMeChecked.update { true }
            is UiAction.OnTaskCardTap -> TODO() // task detail
            is UiAction.OnTaskCheckboxTap -> {
                viewModelScope.launch {
                    taskRepository.updateTaskCompletion(action.task.id)
                }
            }
            is UiAction.OnTaskLongPress -> {
                viewModelScope.launch {
                    taskRepository.deleteTask(action.task.id, action.task.remoteId)
                }
            }
            is UiAction.OnAddTaskTap -> TODO() // task ekleme ekranı daha yok
        }
    }

    init {
        viewModelScope.launch {
            launch { taskRepository.syncRemoteTasksToLocal(groupId) }
            launch { groupManagementRepository.syncGroupDetails(groupId) }

            combine(
                tasks,
                groupManagementRepository.observeMemberCount(groupId),
                userId,
                assignedToMeChecked,
            ) { tasks, memberCount, userId, assignedToMeChecked ->

                if (tasks.isEmpty()) {
                    _uiState.value = UiState.Empty(groupId)
                    return@combine UiState.Empty(groupId)
                }
                val visibleTasks = if (assignedToMeChecked) {
                    userId?.let { id -> filterTasks(id, tasks) } ?: emptyList()
                } else {
                    tasks
                }

                val completedTaskCount = visibleTasks.count { task -> task.isCompleted }
                val pendingTaskCount = visibleTasks.count { task -> !task.isCompleted }

                UiState.Success(
                    groupTasks = visibleTasks,
                    memberCount = memberCount,
                    pendingTaskCount = pendingTaskCount,
                    completedTaskCount = completedTaskCount,
                    checked = assignedToMeChecked
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun observeTasksFlow(groupId: Long): Flow<List<Task.Group>> = taskRepository.observeTasks(groupId)

    private fun filterTasks(userId: Long, list: List<Task.Group>): List<Task.Group> {
        val filteredList = list.filter {
            it.assignedToUserId == userId
        }
        return filteredList
    }
}
