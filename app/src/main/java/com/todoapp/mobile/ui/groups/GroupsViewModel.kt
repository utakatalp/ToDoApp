package com.todoapp.mobile.ui.groups

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.data.model.network.data.GroupSummaryData
import com.todoapp.mobile.domain.model.Group
import com.todoapp.mobile.domain.repository.GroupRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.groups.GroupsContract.UiAction
import com.todoapp.mobile.ui.groups.GroupsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private var remoteSummaryCache = mapOf<Long, GroupSummaryData>()

    init {
        fetchRemoteGroups()
        observeLocalGroups()
    }

    fun onAction(action: UiAction) {
        when (action) {
            UiAction.OnCreateNewGroupTap ->
                _navEffect.trySend(NavigationEffect.Navigate(Screen.CreateNewGroup))

            is UiAction.OnDeleteGroupTap -> deleteGroup(action)
            is UiAction.OnGroupTap -> TODO("Grup Detayı gelecek...")
            is UiAction.OnMoveGroup -> reorderGroups(action)
        }
    }

    private fun fetchRemoteGroups() {
        viewModelScope.launch {
            groupRepository.getGroups()
                .onSuccess { result ->
                    remoteSummaryCache = result.groups.associateBy { it.id }
                }
                .onFailure { t ->
                    Log.e("GroupsViewModel", "Failed to fetch remote groups", t)
                }
        }
    }

    private fun observeLocalGroups() {
        viewModelScope.launch {
            groupRepository.observeAllGroups().collect { groups ->
                _uiState.update {
                    if (groups.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(groups = groups.map { it.toUiItem() })
                    }
                }
            }
        }
    }

    private fun deleteGroup(action: UiAction.OnDeleteGroupTap) {
        viewModelScope.launch {
            groupRepository.deleteGroup(action.id)
                .onFailure { t ->
                    Log.e("GroupsViewModel", "Failed to delete group", t)
                }
        }
    }

    private fun reorderGroups(action: UiAction.OnMoveGroup) {
        val current = (_uiState.value as? UiState.Success) ?: return

        val mutable = current.groups.toMutableList()
        val item = mutable.removeAt(action.from)
        mutable.add(action.to, item)
        _uiState.value = current.copy(groups = mutable)

        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.reorderGroups(
                fromIndex = action.from,
                toIndex = action.to,
            ).onFailure { t ->
                Log.e("GroupsViewModel", "Failed to persist group reorder", t)
            }
        }
    }

    private fun Group.toUiItem(): GroupsContract.GroupUiItem {
        val remote = remoteSummaryCache[remoteId]
        return GroupsContract.GroupUiItem(
            id = id,
            name = name,
            role = remote?.role ?: "",
            description = description,
            memberCount = remote?.memberCount ?: 0,
            pendingTaskCount = remote?.pendingTaskCount ?: 0,
            createdAt = formatTimestamp(createdAt),
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.ENGLISH)
        return sdf.format(java.util.Date(timestamp))
    }
}
