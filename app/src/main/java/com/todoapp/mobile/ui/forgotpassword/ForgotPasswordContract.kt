package com.todoapp.mobile.ui.forgotpassword

import androidx.annotation.StringRes

object ForgotPasswordContract {
    data class UiState(
        val email: String = "",
        val isEmailFieldEnabled: Boolean = false,
        val error: String? = null,
        val isSubmitting: Boolean = false,
        val isSent: Boolean = false,
    )

    sealed interface UiAction {
        data class OnEmailChange(
            val email: String,
        ) : UiAction

        data object OnEmailFieldTap : UiAction

        data object OnForgotPasswordTap : UiAction

        data object OnBackToLoginTap : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(@StringRes val messageRes: Int) : UiEffect
    }
}
