package com.todoapp.mobile.ui.forgotpassword

import androidx.lifecycle.ViewModel
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordContract.UiAction
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    var firstSubmit: Boolean = true

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnEmailChange -> _uiState.update {
                it.copy(
                    email = uiAction.email,
                    error = validateEmail(uiAction.email)
                )
            }
            UiAction.OnForgotPasswordTap -> handleForgotPassword()
            UiAction.OnBackToLoginTap -> {
                /* _navEffect.trySend(NavigationEffect.Navigate(Screen.Login))*/
            }
            UiAction.OnEmailFieldTap -> _uiState.update { it.copy(isEmailFieldEnabled = true) }
        }
    }

    private fun handleForgotPassword() {
        firstSubmit = false
        val emailError = validateEmail(uiState.value.email)

        _uiState.update {
            it.copy(error = emailError)
        }

        if (emailError != null) return

        // Api request vb.
    }

    private fun validateEmail(email: String): String? {
        if (firstSubmit) return null

        return if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Please enter a valid email address"
        } else {
            null
        }
    }
}
