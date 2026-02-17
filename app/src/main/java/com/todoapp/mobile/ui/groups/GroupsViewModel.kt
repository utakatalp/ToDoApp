package com.todoapp.mobile.ui.groups

import androidx.lifecycle.ViewModel
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.groups.GroupsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    init {
        _uiState.update { UiState.Empty }
    }

    fun onAction(action: GroupsContract.UiAction) {
        when (action) {
            GroupsContract.UiAction.OnCreateNewGroupTap -> _navEffect.trySend(
                NavigationEffect.Navigate(Screen.CreateNewGroup)
            )
        }
    }
}
