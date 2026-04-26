package com.todoapp.mobile.ui.resetpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.resetpassword.ResetPasswordContract.UiAction
import com.todoapp.mobile.ui.resetpassword.ResetPasswordContract.UiEffect
import com.todoapp.mobile.ui.resetpassword.ResetPasswordContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val token: String = savedStateHandle.toRoute<Screen.ResetPassword>().token

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<UiEffect>()
    val effect = _effect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnNewChange ->
                _uiState.update {
                    it.copy(
                        newPassword = action.value,
                        newError = validateNew(action.value),
                        confirmError = validateConfirm(action.value, it.confirmPassword),
                    )
                }
            is UiAction.OnConfirmChange ->
                _uiState.update {
                    it.copy(
                        confirmPassword = action.value,
                        confirmError = validateConfirm(it.newPassword, action.value),
                    )
                }
            UiAction.OnToggleNewVisibility ->
                _uiState.update { it.copy(isNewVisible = !it.isNewVisible) }
            UiAction.OnToggleConfirmVisibility ->
                _uiState.update { it.copy(isConfirmVisible = !it.isConfirmVisible) }
            UiAction.OnSubmit -> submit()
            UiAction.OnBackToLogin ->
                _navEffect.trySend(
                    NavigationEffect.NavigateClearingBackstack(Screen.Login()),
                )
        }
    }

    private fun validateNew(newPassword: String): Int? = when {
        newPassword.isBlank() -> null
        newPassword.length < ResetPasswordContract.MIN_PASSWORD_LENGTH ->
            R.string.error_password_min_length
        else -> null
    }

    private fun validateConfirm(newPassword: String, confirmPassword: String): Int? = when {
        confirmPassword.isBlank() -> null
        newPassword != confirmPassword -> R.string.error_passwords_dont_match
        else -> null
    }

    private fun submit() {
        val state = _uiState.value
        if (!state.canSubmit) return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            userRepository.resetPassword(token, state.newPassword)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false, isDone = true) }
                    _effect.trySend(UiEffect.ShowToast(R.string.reset_password_success))
                }
                .onFailure {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _effect.trySend(UiEffect.ShowToast(R.string.reset_token_invalid))
                }
        }
    }
}
