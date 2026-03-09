package com.todoapp.mobile.ui.groups

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.data.model.network.data.GroupSummaryData
import com.todoapp.mobile.domain.repository.group.GroupManagementRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.groups.GroupsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupManagementRepository: GroupManagementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    init {
        loadFamilyGroups()
        viewModelScope.launch {
            groupManagementRepository.updateGroupSummaries()
        }
    }

    fun onAction(action: GroupsContract.UiAction) {
        when (action) {
            GroupsContract.UiAction.OnCreateNewGroupTap -> _navEffect.trySend(
                NavigationEffect.Navigate(Screen.CreateNewGroup)
            )
            is GroupsContract.UiAction.OnDeleteGroupTap -> {
                viewModelScope.launch {
                    groupManagementRepository.deleteGroup(action.id)
                }
            }

            is GroupsContract.UiAction.OnGroupTap -> {
                _navEffect.trySend(
                    NavigationEffect.Navigate(Screen.GroupDetails.Overview(action.id.toString()))
                )
            }
        }
    }

    private fun loadFamilyGroups() {
        viewModelScope.launch {
            groupManagementRepository.observeGroupSummaries().collect { data ->
                Log.d("GroupsViewModel", "loadFamilyGroups: $data")
                if (data.isNotEmpty()) {
                    _uiState.update { UiState.Success(groups = data.map { it.toUiItem() }) }
                } else {
                    _uiState.update { UiState.Empty }
                }
            }
        }
    }

    private fun GroupSummaryData.toUiItem() = GroupsContract.GroupUiItem(
        id = id,
        name = name,
        role = role,
        description = description,
        memberCount = memberCount,
        pendingTaskCount = pendingTaskCount,
        createdAt = formatTimestamp(createdAt)
    )

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.ENGLISH)
        return sdf.format(java.util.Date(timestamp))
    }
}
