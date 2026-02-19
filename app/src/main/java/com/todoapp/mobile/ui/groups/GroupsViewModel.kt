package com.todoapp.mobile.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.data.model.network.data.FamilyGroupSummaryData
import com.todoapp.mobile.domain.repository.FamilyGroupRepository
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
    private val familyGroupRepository: FamilyGroupRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private val useMockData = true

    init {
        if (useMockData) {
            _uiState.value = UiState.Success(groups = mockGroups)
        } else {
            loadFamilyGroups()
        }
    }

    fun onAction(action: GroupsContract.UiAction) {
        when (action) {
            GroupsContract.UiAction.OnCreateNewGroupTap -> _navEffect.trySend(
                NavigationEffect.Navigate(Screen.CreateNewGroup)
            )

            is GroupsContract.UiAction.OnDeleteGroupTap -> {
                if (useMockData) {
                    val current = (_uiState.value as? UiState.Success) ?: return
                    val updated = current.groups.filter { it.id != action.id }
                    _uiState.value = if (updated.isEmpty()) UiState.Empty else UiState.Success(updated)
                } else {
                    viewModelScope.launch {
                        familyGroupRepository.deleteFamilyGroup(action.id)
                            .onSuccess {
                                loadFamilyGroups()
                            }
                    }
                }
            }

            is GroupsContract.UiAction.OnGroupTap -> TODO("Grup Detayı gelecek...")
            is GroupsContract.UiAction.OnMoveGroup -> {
                val current = (_uiState.value as? UiState.Success) ?: return
                val mutable = current.groups.toMutableList()
                val item = mutable.removeAt(action.from)
                mutable.add(action.to, item)
                _uiState.value = current.copy(groups = mutable)
            }
        }
    }

    private fun loadFamilyGroups() {
        viewModelScope.launch {
            familyGroupRepository
                .getFamilyGroups()
                .onSuccess { result ->
                    if (result.count > 0) {
                        _uiState.update { UiState.Success(groups = result.familyGroups.map { it.toUiItem() }) }
                    } else {
                        _uiState.update { UiState.Empty }
                    }
                }
                .onFailure {
                    _uiState.update { UiState.Empty }
                }
        }
    }

    private fun FamilyGroupSummaryData.toUiItem() = GroupsContract.GroupUiItem(
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
