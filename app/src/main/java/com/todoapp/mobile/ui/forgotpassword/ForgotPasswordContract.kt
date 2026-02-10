package com.todoapp.mobile.ui.forgotpassword

object ForgotPasswordContract {

    data class UiState(
        val email: String = "",
        val isEmailFieldEnabled: Boolean = false,
        val error: String? = null
    )

    sealed interface UiAction {
        data class OnEmailChange(val email: String) : UiAction
        data object OnEmailFieldTap : UiAction
        data object OnForgotPasswordTap : UiAction
        data object OnBackToLoginTap : UiAction
    }

    sealed interface UiEffect
}
