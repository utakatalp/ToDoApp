package com.todoapp.mobile.ui

object OnboardingContract {
    data class UiState(
        val bgIndex: Int = 0,
    )

    sealed interface UiAction {
        data object OnLoginClick : UiAction
        data object OnRegisterClick : UiAction
    }

    sealed interface UiEffect {
        data object NavigateToLogin : UiEffect
        data object NavigateToRegister : UiEffect
    }
}