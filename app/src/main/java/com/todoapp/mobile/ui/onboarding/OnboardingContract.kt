package com.todoapp.mobile.ui.onboarding

import androidx.compose.runtime.Immutable

object OnboardingContract {
    @Immutable
    data class UiState(
        val bgIndex: Int = 0,
    )

    sealed interface UiAction {
        data object OnLoginClick : UiAction

        data object OnGetStartedClick : UiAction
    }

    sealed interface UiEffect
}
