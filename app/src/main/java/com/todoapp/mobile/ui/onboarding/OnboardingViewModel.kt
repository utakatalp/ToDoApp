package com.todoapp.mobile.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.onboarding.OnboardingContract.UiAction
import com.todoapp.mobile.ui.onboarding.OnboardingContract.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val INTERVAL = 1500L

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    init {
        viewModelScope.launch {
            while (true) {
                delay(INTERVAL)
                _uiState.update { state ->
                    state.copy(bgIndex = (state.bgIndex + 1) % 4)
                }
            }
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnLoginClick -> _navEffect.trySend(NavigationEffect.Navigate(Screen.Home))
            is UiAction.OnRegisterClick -> _navEffect.trySend(NavigationEffect.Navigate(Screen.Register))
        }
    }
}
