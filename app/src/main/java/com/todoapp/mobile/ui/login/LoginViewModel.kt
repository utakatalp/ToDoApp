package com.todoapp.mobile.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.common.passwordValidation.ValidationManager
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.login.LoginContract.UiAction
import com.todoapp.mobile.ui.login.LoginContract.UiEffect
import com.todoapp.mobile.ui.login.LoginContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnEmailChange -> onEmailChange(uiAction.value)
            is UiAction.OnPasswordChange -> onPasswordChange(uiAction.value)
            UiAction.OnEmailFieldTap -> enableEmailField()
            UiAction.OnFacebookSignInTap -> {}
            UiAction.OnForgotPasswordTap -> {}
            UiAction.OnGoogleSignInTap -> {}
            UiAction.OnLoginTap -> handleLoginClick()
            UiAction.OnPasswordFieldTap -> enablePasswordField()
            UiAction.OnPasswordVisibilityTap -> togglePasswordVisibility()
            UiAction.OnPrivacyPolicyTap -> showPrivacyPolicy()
            UiAction.OnRegisterTap -> navigateToRegister()
            UiAction.OnTermsOfServiceTap -> showTermsOfService()
        }
    }

    private fun login() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        userRepository.login(
            request = LoginRequest(
                email = uiState.value.email,
                password = uiState.value.password,
            )
        ).fold(
            onSuccess = {
                _uiState.update { it.copy(isLoading = false) }
                _navEffect.trySend(
                    NavigationEffect.Navigate(
                        route = Screen.Home,
                        popUpTo = Screen.Onboarding,
                        isInclusive = true
                    )
                )
            },
            onFailure = { throwable ->
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.trySend(UiEffect.ShowToast(throwable.message.orEmpty()))
            }
        )
    }

    private fun navigateToRegister() {
        _navEffect.trySend(
            NavigationEffect.Navigate(
                route = Screen.Register
            )
        )
    }

    private fun handleLoginClick() {
        val currentState = uiState.value
        val emailError = ValidationManager.validateEmail(currentState.email).toLocalizedError()
        val passwordError = ValidationManager.validatePassword(currentState.password).toLocalizedError()

        if (emailError == null && passwordError == null) {
            login()
        } else {
            _uiState.update { current ->
                current.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    isLoading = false,
                    hasSubmittedOnce = true
                )
            }
        }
    }

    private fun onEmailChange(email: String) {
        _uiState.update { current ->
            current.copy(
                email = email,
                emailError = if (current.hasSubmittedOnce) {
                    if (email.isBlank()) {
                        ValidationManager.validateEmail(email).toLocalizedError()
                    } else {
                        null
                    }
                } else {
                    null
                }
            )
        }
    }

    private fun onPasswordChange(password: String) {
        _uiState.update { current ->
            current.copy(
                password = password,
                passwordError = if (current.hasSubmittedOnce) {
                    ValidationManager.validatePassword(password).toLocalizedError()
                } else {
                    null
                }
            )
        }
    }

    private fun String.toLocalizedError(): String? {
        if (this.isEmpty()) return null

        return when (this) {
            ValidationManager.ValidationErrors.EMAIL_BLANK ->
                context.getString(R.string.error_email_blank)

            ValidationManager.ValidationErrors.EMAIL_INVALID ->
                context.getString(R.string.error_email_invalid)

            ValidationManager.ValidationErrors.PASSWORD_BLANK ->
                context.getString(R.string.error_password_blank)

            ValidationManager.ValidationErrors.PASSWORD_MIN_LENGTH ->
                context.getString(R.string.error_password_min_length, ValidationManager.PasswordRules.MIN_LENGTH)

            else -> this
        }
    }

    private fun togglePasswordVisibility() {
        val state = uiState.value
        _uiState.update { it.copy(isPasswordVisible = !state.isPasswordVisible) }
    }

    private fun enableEmailField() {
        _uiState.update { it.copy(isEmailFieldEnabled = true) }
    }

    private fun enablePasswordField() {
        _uiState.update { it.copy(isPasswordFieldEnabled = true) }
    }

    private fun showPrivacyPolicy() {
        _navEffect.trySend(NavigationEffect.Navigate(route = Screen.WebView("https://www.google.com")))
    }

    private fun showTermsOfService() {
        _navEffect.trySend(NavigationEffect.Navigate(route = Screen.WebView("https://www.google.com")))
    }
}
