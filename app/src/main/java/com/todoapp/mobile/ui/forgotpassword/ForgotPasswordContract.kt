package com.todoapp.mobile.ui.forgotpassword

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

object ForgotPasswordContract {
    @Immutable
    data class UiState(
        val email: String = "",
        val error: String? = null,
        val isSubmitting: Boolean = false,
        val isSent: Boolean = false,
    )

    sealed interface UiAction {
        data class OnEmailChange(
            val email: String,
        ) : UiAction

        data object OnForgotPasswordTap : UiAction

        data object OnBackToLoginTap : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(@StringRes val messageRes: Int) : UiEffect
    }
}
