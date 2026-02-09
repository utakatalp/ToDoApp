package com.todoapp.mobile.ui.login

object LoginContract {

    data class UiState(
        val email: String = "",
        val password: String = "",
        val isPasswordVisible: Boolean = false,
        val isEmailFieldEnabled: Boolean = true,
        val isPasswordFieldEnabled: Boolean = true,
        val emailError: String? = null,
        val passwordError: String? = null,
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
        data object OnGoogleSignInTap : UiAction
        data object OnFacebookSignInTap : UiAction
        data object OnRegisterTap : UiAction
        data object OnTermsOfServiceTap : UiAction
        data object OnPrivacyPolicyTap : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(val message: String) : UiEffect
    }
}
