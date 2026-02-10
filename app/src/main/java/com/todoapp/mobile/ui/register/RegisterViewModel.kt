package com.todoapp.mobile.ui.register

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.domain.repository.SessionPreferences
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.register.RegisterContract.RegisterError
import com.todoapp.mobile.ui.register.RegisterContract.UiAction
import com.todoapp.mobile.ui.register.RegisterContract.UiEffect
import com.todoapp.mobile.ui.register.RegisterContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionPreferences: SessionPreferences,
    private val taskRepository: TaskRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private var validationMode: ValidationMode = ValidationMode.Pristine

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnConfirmPasswordChange -> onConfirmPasswordChange(uiAction.confirmPassword)
            is UiAction.OnEmailChange -> onEmailChange(uiAction.email)
            is UiAction.OnFullNameChange -> onFullNameChange(uiAction.fullName)
            is UiAction.OnPasswordChange -> onPasswordChange(uiAction.password)
            is UiAction.OnUpdateWebViewVisibility -> updateWebViewVisibility(uiAction.isVisible)
            UiAction.OnFacebookSignInTap -> _uiEffect.trySend(UiEffect.FacebookLogin)
            UiAction.OnLoginTap -> {}
            UiAction.OnPrivacyPolicyTap -> showPrivacyPolicy()
            UiAction.OnSignUpTap -> onSignUpTap()
            UiAction.OnTermsOfServiceTap -> showTermsOfService()
            UiAction.OnPasswordVisibilityTap -> togglePasswordVisibility()
            UiAction.OnConfirmPasswordFieldTap -> enableConfirmPasswordField()
            UiAction.OnEmailFieldTap -> enableEmailField()
            UiAction.OnFullNameFieldTap -> enableFullNameField()
            UiAction.OnPasswordFieldTap -> enablePasswordField()
            is UiAction.OnSuccessfulFacebookLogin -> loginWithFacebook(uiAction.token)
            is UiAction.OnFacebookLoginFail -> handleFacebookLoginFailure(uiAction.throwable)
        }
    }

    private fun updateWebViewVisibility(isVisible: Boolean) {
        _uiState.update { state ->
            state.copy(
                isWebViewAvailable = isVisible,
            )
        }
    }

    private fun showPrivacyPolicy() {
        _navEffect.trySend(NavigationEffect.Navigate(route = Screen.WebView("https://www.google.com")))
    }

    private fun showTermsOfService() {
        _navEffect.trySend(NavigationEffect.Navigate(route = Screen.WebView("https://www.google.com")))
    }

    private fun updateValidationMode() {
        // OVER ENGINEERING FARKINDAYIM
        validationMode = ValidationMode.AfterSubmit(
            getState = { uiState.value },
            validateEmail = { validateEmail(it) },
            validatePassword = { validatePassword(it) },
            validateConfirm = { confirmPassword, password -> validateConfirmPassword(confirmPassword, password) }
        )
    }

    private fun onSignUpTap() {
        val state = uiState.value

        updateValidationMode()

        val hasEmptyField =
            state.email.isBlank() ||
                    state.password.isBlank() ||
                    state.confirmPassword.isBlank() ||
                    state.fullName.isBlank()

        if (hasEmptyField) {
            _uiState.update {
                it.copy(
                    confirmPasswordError = RegisterError("Please fill in the required fields.")
                )
            }
            return
        }

        val emailError = validationMode.validate(field = Field.Email(state.email))
        val passwordError = validationMode.validate(field = Field.Password(state.password))
        val confirmPasswordError =
            validationMode.validate(field = Field.ConfirmPassword(state.confirmPassword))

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
            )
        }

        val hasAnyError =
            uiState.value.emailError != null ||
                    uiState.value.passwordError != null ||
                    uiState.value.confirmPasswordError != null

        if (hasAnyError) return

        // sign-up flow devamı, api request vb.
        viewModelScope.launch {
            userRepository.register(RegisterRequest(state.email, state.password, state.fullName))
                .onSuccess { handleSuccessfulRegister(it) }
                .onFailure { throwable ->
                    if (throwable is DomainException.Server) {
                        _uiState.update {
                            it.copy(emailError = RegisterError(throwable.message ?: "Try again later"))
                        }
                    } else {
                        _uiState.update {
                            it.copy(generalError = RegisterError(throwable.message ?: "Try again later"))
                        }
                    }
                }
        }
    }

    private fun loginWithFacebook(token: String) {
        viewModelScope.launch {
            setRedirecting(true)

            userRepository.facebookLogin(FacebookLoginRequest(token))
                .onSuccess { handleSuccessfulRegister(it) }
                .onFailure { throwable ->
                    Log.d("facebook_login", throwable.message.orEmpty())
                    handleFacebookLoginFailure(throwable)
                }
        }
    }

    private fun handleFacebookLoginFailure(throwable: Throwable) {
        setRedirecting(false)

        val message = when (throwable) {
            is DomainException.NoInternet -> "No internet connection."
            is DomainException.Unauthorized -> "Facebook session expired. Please try again."
            is DomainException.Server -> throwable.message ?: "Try again later."
            else -> throwable.message ?: "Try again later."
        }

        _uiState.update { state ->
            state.copy(
                generalError = RegisterError(message)
            )
        }
    }

    private fun setRedirecting(isRedirecting: Boolean) {
        _uiState.update { state -> state.copy(isRedirecting = isRedirecting) }
    }

    private fun handleSuccessfulRegister(registerResponseData: RegisterResponseData) {
        viewModelScope.launch {
            sessionPreferences.setAccessToken(registerResponseData.accessToken)
            sessionPreferences.setExpiresAt(registerResponseData.expiresIn)
            sessionPreferences.setRefreshToken(registerResponseData.refreshToken)

            taskRepository.syncLocalTasksToServer()

            _navEffect.trySend(
                NavigationEffect.Navigate(
                    route = Screen.Home,
                    popUpTo = Screen.Onboarding,
                    isInclusive = true
                )
            )
        }
    }

    private fun onEmailChange(email: String) {
        _uiState.update { current ->
            current.copy(
                email = email,
                emailError = validationMode.validate(Field.Email(email)),
            )
        }
    }

    private fun onPasswordChange(password: String) {
        _uiState.update { state ->
            state.copy(
                password = password,
                passwordError = validationMode.validate(Field.Password(password)),
            )
        }
    }

    private fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { state ->
            state.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = validationMode.validate(Field.ConfirmPassword(confirmPassword)),
            )
        }
    }

    private fun onFullNameChange(fullName: String) {
        _uiState.update { state ->
            state.copy(fullName = fullName)
        }
    }

    private fun enableEmailField() {
        _uiState.update { it.copy(isEmailFieldEnabled = true) }
    }

    private fun enableFullNameField() {
        _uiState.update { it.copy(isFullNameFieldEnabled = true) }
    }

    private fun enablePasswordField() {
        _uiState.update { it.copy(isPasswordFieldEnabled = true) }
    }

    private fun enableConfirmPasswordField() {
        _uiState.update { it.copy(isPasswordConfirmationFieldEnabled = true) }
    }

    private fun validateEmail(email: String): RegisterError? {
        if (email.isBlank()) return null
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            null
        } else {
            RegisterError("Invalid email address.")
        }
    }

    private fun interface PasswordRule {
        fun validate(password: String): RegisterError?
    }

    private val passwordRules: List<PasswordRule> =
        listOf(
            MinLengthPasswordRule(minLength = MIN_PASSWORD_LENGTH),
            ContainsDigitOrSymbolPasswordRule(),
        )

    private class MinLengthPasswordRule(
        private val minLength: Int,
    ) : PasswordRule {
        override fun validate(password: String): RegisterError? {
            return if (password.length >= minLength) {
                null
            } else {
                RegisterError("Password must be at least $minLength characters.")
            }
        }
    }

    private class ContainsDigitOrSymbolPasswordRule : PasswordRule {
        override fun validate(password: String): RegisterError? {
            val hasDigit = password.any { it.isDigit() }
            val hasSymbol = password.any { !it.isLetterOrDigit() }

            return if (hasDigit || hasSymbol) {
                null
            } else {
                RegisterError("Password must contain at least one digit or symbol.")
            }
        }
    }

    private fun validatePassword(password: String): RegisterError? {
        if (password.isBlank()) {
            return null
        }

        for (rule in passwordRules) {
            val error = rule.validate(password)
            if (error != null) {
                return error
            }
        }
        return null
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): RegisterError? {
        if (confirmPassword.isBlank()) return null
        return if (password == confirmPassword) {
            null
        } else {
            RegisterError("Passwords do not match.")
        }
    }

    private fun togglePasswordVisibility() {
        val state = uiState.value
        _uiState.update { it.copy(isPasswordVisible = !state.isPasswordVisible) }
    }

    private companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
}

sealed interface ValidationMode {
    fun validate(field: Field): RegisterError?

    class AfterSubmit(
        private val getState: () -> UiState,
        private val validateEmail: (String) -> RegisterError?,
        private val validatePassword: (String) -> RegisterError?,
        private val validateConfirm: (String, String) -> RegisterError?
    ) : ValidationMode {

        override fun validate(field: Field): RegisterError? {
            return when (field) {
                is Field.Email -> validateEmail(field.email)
                is Field.Password -> validatePassword(field.password)
                is Field.ConfirmPassword -> validateConfirm(field.confirmPassword, getState().password)
            }
        }
    }

    data object Pristine : ValidationMode {

        override fun validate(field: Field): RegisterError? {
            return null
        }
    }
}

sealed class Field {
    data class Email(val email: String) : Field()
    data class Password(val password: String) : Field()
    data class ConfirmPassword(val confirmPassword: String) : Field()
}
