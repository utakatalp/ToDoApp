package com.todoapp.mobile.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordContract.UiAction
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordContract.UiEffect
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<UiEffect>()
    val effect = _effect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    var firstSubmit: Boolean = true

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnEmailChange ->
                _uiState.update {
                    it.copy(
                        email = uiAction.email,
                        error = validateEmail(uiAction.email),
                    )
                }

            UiAction.OnForgotPasswordTap -> handleForgotPassword()
            UiAction.OnBackToLoginTap -> navigateToBackToLogin()
            UiAction.OnEmailFieldTap -> _uiState.update { it.copy(isEmailFieldEnabled = true) }
        }
    }

    private fun handleForgotPassword() {
        firstSubmit = false
        val emailError = validateEmail(uiState.value.email)

        _uiState.update { it.copy(error = emailError) }

        if (emailError != null) return
        if (_uiState.value.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            userRepository.forgotPassword(uiState.value.email.trim())
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false, isSent = true) }
                    _effect.trySend(UiEffect.ShowToast(R.string.reset_link_sent))
                }
                .onFailure {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _effect.trySend(UiEffect.ShowToast(R.string.error_generic))
                }
        }
    }

    private fun navigateToBackToLogin() {
        _navEffect.trySend(NavigationEffect.Navigate(Screen.Login()))
    }

    private fun validateEmail(email: String): String? {
        if (firstSubmit) return null

        return if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            "Please enter a valid email address"
        } else {
            null
        }
    }
}
