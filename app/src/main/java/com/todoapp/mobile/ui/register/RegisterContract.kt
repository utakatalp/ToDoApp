package com.todoapp.mobile.ui.register

import android.content.Context
import com.todoapp.mobile.common.passwordValidation.PasswordStrength

object RegisterContract {

    data class UiState(
        val fullName: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val isPasswordVisible: Boolean = false,
        val isEmailFieldEnabled: Boolean = false,
        val isFullNameFieldEnabled: Boolean = false,
        val isPasswordConfirmationFieldEnabled: Boolean = false,
        val isPasswordFieldEnabled: Boolean = false,
        val emailError: RegisterError? = null,
        val passwordError: RegisterError? = null,
        val confirmPasswordError: RegisterError? = null,
        val generalError: RegisterError? = null,
        val isWebViewAvailable: Boolean = false,
        val passwordStrength: PasswordStrength? = null,
        val isRedirecting: Boolean = false,
    )

    sealed interface UiAction {
        data object OnSignUpTap : UiAction
        data object OnTermsOfServiceTap : UiAction
        data object OnPrivacyPolicyTap : UiAction
        data object OnLoginTap : UiAction
        data object OnFacebookSignInTap : UiAction
        data class OnGoogleSignInTap(val activityContext: Context) : UiAction
        data object OnPasswordVisibilityTap : UiAction
        data object OnPasswordFieldTap : UiAction
        data object OnConfirmPasswordFieldTap : UiAction
        data object OnFullNameFieldTap : UiAction
        data object OnEmailFieldTap : UiAction
        data class OnFullNameChange(val fullName: String) : UiAction
        data class OnEmailChange(val email: String) : UiAction
        data class OnPasswordChange(val password: String) : UiAction
        data class OnConfirmPasswordChange(val confirmPassword: String) : UiAction
        data class OnUpdateWebViewVisibility(val isVisible: Boolean) : UiAction
        data class OnSuccessfulFacebookLogin(val token: String) : UiAction
        data class OnFacebookLoginFail(val throwable: Throwable) : UiAction
    }

    sealed interface UiEffect {
        data object FacebookLogin : UiEffect
        data class ShowToast(val message: String) : UiEffect
    }

    data class RegisterError(val message: String)
}
