package com.todoapp.mobile.ui.createnewgroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.data.model.network.request.CreateFamilyGroupRequest
import com.todoapp.mobile.domain.repository.FamilyGroupRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.createnewgroup.CreateNewGroupContract.UiAction
import com.todoapp.mobile.ui.createnewgroup.CreateNewGroupContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewGroupViewModel @Inject constructor(
    private val familyGroupRepository: FamilyGroupRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    var isErrorFlagActive = false

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnCreateTap -> createGroup()
            is UiAction.OnGroupDescriptionChange -> _uiState.update {
                it.copy(
                groupDescription = action.groupDescription
            )
            }
            is UiAction.OnGroupNameChange -> updateGroupName(action.groupName)
        }
    }

    private fun updateGroupName(updatedGroupName: String) {
        if (isErrorFlagActive) {
            validateGroupName(updatedGroupName)
        }
        _uiState.update { it.copy(groupName = updatedGroupName) }
    }

    private fun createGroup() {
        val uiStateSnapshot = uiState.value

        if (!validateGroupName(uiStateSnapshot.groupName)) return

        viewModelScope.launch {
            familyGroupRepository.createFamilyGroup(
                CreateFamilyGroupRequest(
                    uiState.value.groupName,
                    uiState.value.groupDescription ?: ""
                )
            ).onSuccess {
                _navEffect.send(NavigationEffect.Navigate(Screen.Groups, popUpTo = Screen.Groups, isInclusive = true))
            }.onFailure {
                _uiState.update { it.copy(error = "Something went wrong. Try again later.") }
            }
        }
    }
    private fun validateGroupName(groupName: String): Boolean {
        if (groupName.isEmpty()) {
            _uiState.update { it.copy(error = "Group name is required.") }
            isErrorFlagActive = true
            return false
        }
        _uiState.update { it.copy(error = null) }
        return true
    }
}
