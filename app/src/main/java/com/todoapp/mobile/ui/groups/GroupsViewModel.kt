package com.todoapp.mobile.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val familyGroupRepository: FamilyGroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    init {
        loadFamilyGroups()
    }

    fun onAction(action: GroupsContract.UiAction) {
        when (action) {
            GroupsContract.UiAction.OnCreateNewGroupTap -> _navEffect.trySend(
                NavigationEffect.Navigate(Screen.CreateNewGroup)
            )
            is GroupsContract.UiAction.OnDeleteGroupTap -> {
                viewModelScope.launch {
                    familyGroupRepository.deleteFamilyGroup(action.id)
                        .onSuccess {
                            loadFamilyGroups()
                        }
                }
            }
        }
    }

    private fun loadFamilyGroups() {
        viewModelScope.launch {
            familyGroupRepository
                .getFamilyGroups()
                .onSuccess { result ->
                    if (result.count > 0) {
                        _uiState.update { UiState.Success(groups = result.familyGroups) }
                    } else {
                        _uiState.update { UiState.Empty }
                    }
                }
                .onFailure {
                    _uiState.update { UiState.Empty }
                }
        }
    }
}
