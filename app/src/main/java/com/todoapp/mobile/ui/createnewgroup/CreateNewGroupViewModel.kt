package com.todoapp.mobile.ui.createnewgroup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.domain.repository.GroupRepository
import com.todoapp.mobile.domain.repository.UserRepository
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
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val cameFromAuth: Boolean = savedStateHandle["cameFromAuth"] ?: false

    private val _uiState = MutableStateFlow(UiState(isUserAuthenticated = false))
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private var isErrorFlagActive = false

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnCreateTap -> createGroup()
            is UiAction.OnGroupDescriptionChange -> _uiState.update {
                it.copy(
                    groupDescription = action.groupDescription
                )
            }

            UiAction.OnBackClick -> onBackClick()

            is UiAction.OnGroupNameChange -> updateGroupName(action.groupName)
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            if (cameFromAuth) {
                _navEffect.send(
                    NavigationEffect.Navigate(
                        route = Screen.Home,
                        popUpTo = Screen.Home,
                        isInclusive = false
                    )
                )
            } else {
                _navEffect.send(
                    NavigationEffect.Back
                )
            }
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
            val isUserAuthenticated = userRepository.getUserInfo().isSuccess
            updateAuthenticationState(isUserAuthenticated)

            if (!isUserAuthenticated) {
                _navEffect.send(
                    NavigationEffect.Navigate(
                        Screen.Login(redirectAfterLogin = Screen.CreateNewGroup::class.qualifiedName)
                    )
                )
                return@launch
            }

            groupRepository.createGroup(
                CreateGroupRequest(
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

    private fun updateAuthenticationState(isAuthenticated: Boolean) {
        _uiState.update { it.copy(isUserAuthenticated = isAuthenticated) }
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
