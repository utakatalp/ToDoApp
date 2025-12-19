package com.todoapp.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.ui.OnboardingContract.UiAction
import com.todoapp.mobile.ui.OnboardingContract.UiEffect
import com.todoapp.mobile.ui.OnboardingContract.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }


    init {
        viewModelScope.launch {
            while (true) {
                delay(1500)
                _uiState.update { state ->
                    state.copy(bgIndex = (state.bgIndex + 1) % 4)
                }
            }
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnLoginClick -> _uiEffect.trySend(UiEffect.NavigateToLogin)
            is UiAction.OnRegisterClick -> _uiEffect.trySend(UiEffect.NavigateToRegister)
        }
    }
}