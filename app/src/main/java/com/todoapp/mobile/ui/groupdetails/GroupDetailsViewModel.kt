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
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailsViewModel @Inject constructor(
    private val taskRepository: GroupTaskRepository,
    private val groupManagementRepository: GroupManagementRepository,
    savedStateHandle: SavedStateHandle,
    private val dataStoreHelper: DataStoreHelper,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    // UI filter selection (Assigned-to-me toggle)
    private val _assignedToMeChecked = MutableStateFlow(false)
    val assignedToMeChecked: StateFlow<Boolean> = _assignedToMeChecked.asStateFlow()

    val userId: StateFlow<Long?> =
        dataStoreHelper.observeUser()
            .map { it?.id }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null
            )
    val groupId: Long =
        checkNotNull(savedStateHandle.get<String>("groupId")).toLong()

    private fun observeTasksFlow(groupId: Long): Flow<List<Task.Group>> = flow {
        emitAll(taskRepository.observeTasks(groupId))
    }

    val tasks: StateFlow<List<Task.Group>> =
        observeTasksFlow(groupId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )

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

    fun onAction(action: UiAction) {
        when (action) {
            UiAction.OnAllTap -> _assignedToMeChecked.value = false
            UiAction.OnAssignedToMeTap -> _assignedToMeChecked.value = true
            is UiAction.OnTaskCardTap -> TODO()
            is UiAction.OnTaskCheckboxTap -> TODO()
            is UiAction.OnTaskLongPress -> TODO()
            is UiAction.OnAddTaskTap -> TODO()
        }
    }

    fun filterTasks(userId: Long, list: List<Task.Group>): List<Task.Group> {
        val filteredList = list.filter {
            it.assignedToUserId == userId
        }
        return filteredList
    }
}
