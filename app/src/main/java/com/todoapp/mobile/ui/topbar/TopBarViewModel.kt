package com.todoapp.mobile.ui.topbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.data.repository.DataStoreHelper
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.topbar.TopBarContract.UiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopBarViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreHelper: DataStoreHelper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopBarContract.UiState(isUserAuthenticated = false))
    val uiState = _uiState.asStateFlow()

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    init {
        startObservingUserAuthState()
    }

    fun onAction(action: UiAction) {
        when (action) {
            UiAction.OnBackClick -> sendNavEffect(
                effect = NavigationEffect.Back
            )

            UiAction.OnNotificationClick -> handleNotificationClick()
            UiAction.OnProfileClick -> sendNavEffect(
                NavigationEffect.Navigate(
                    Screen.Login(
                        redirectAfterLogin = Screen.Home::class.qualifiedName
                    )
                )
            )

            UiAction.OnSettingsClick -> sendNavEffect(NavigationEffect.Navigate(Screen.Settings))
            UiAction.OnSearchClick -> sendNavEffect(NavigationEffect.Navigate(Screen.Search))
            UiAction.OnAuthenticationUpdate -> refreshAuthenticationState()
            is UiAction.OnGroupSettingsClick -> sendNavEffect(
                NavigationEffect.Navigate(Screen.GroupSettings(action.groupId))
            )
        }
    }

    private fun startObservingUserAuthState() {
        viewModelScope.launch {
            dataStoreHelper.observeUser().collect { user ->
                updateAuthenticationState(isAuthenticated = user != null)
            }
        }
    }

    private fun handleNotificationClick() {
        // will be added
    }

    private fun refreshAuthenticationState() {
        viewModelScope.launch {
            updateAuthenticationState(
                isAuthenticated = userRepository.getUserInfo().isSuccess
            )
        }
    }

    private fun updateAuthenticationState(isAuthenticated: Boolean) {
        _uiState.update { it.copy(isUserAuthenticated = isAuthenticated) }
    }

    private fun sendNavEffect(effect: NavigationEffect) {
        _navEffect.trySend(effect)
    }
}
