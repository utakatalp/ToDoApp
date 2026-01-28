package com.todoapp.mobile.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.navigation.NavEffect
import com.todoapp.mobile.ui.onboarding.OnboardingContract.UiAction
import com.todoapp.mobile.ui.onboarding.OnboardingContract.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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
    private val _navEffect = Channel<NavEffect>()
    val navEffect: Flow<NavEffect> = _navEffect.receiveAsFlow()

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
            is UiAction.OnLoginClick -> _navEffect.trySend(NavEffect.NavigateToLogin)
            is UiAction.OnRegisterClick -> _navEffect.trySend(NavEffect.NavigateToRegister)
        }
    }
}
