package com.todoapp.mobile.ui.login

import android.content.Context

object LoginContract {

    data class UiState(
        val email: String = "",
        val password: String = "",
        val isPasswordVisible: Boolean = false,
        val isEmailFieldEnabled: Boolean = false,
        val isPasswordFieldEnabled: Boolean = false,
        val emailError: LoginError? = null,
        val passwordError: LoginError? = null,
        val generalError: LoginError? = null,
        val isLoading: Boolean = false,
        val hasSubmittedOnce: Boolean = false,
    )

    sealed interface UiAction {
        data class OnEmailChange(val value: String) : UiAction
        data class OnPasswordChange(val value: String) : UiAction
        data object OnEmailFieldTap : UiAction
        data object OnPasswordFieldTap : UiAction
        data object OnPasswordVisibilityTap : UiAction
        data object OnLoginTap : UiAction
        data object OnForgotPasswordTap : UiAction
        data class OnGoogleSignInTap(val activityContext: Context) : UiAction
        data object OnFacebookSignInTap : UiAction
        data object OnRegisterTap : UiAction
        data object OnTermsOfServiceTap : UiAction
        data object OnPrivacyPolicyTap : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(val message: String) : UiEffect
    }

    data class LoginError(val message: String)
}
