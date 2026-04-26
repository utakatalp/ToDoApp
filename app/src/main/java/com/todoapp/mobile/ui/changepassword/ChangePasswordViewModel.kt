package com.todoapp.mobile.ui.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.changepassword.ChangePasswordContract.UiAction
import com.todoapp.mobile.ui.changepassword.ChangePasswordContract.UiEffect
import com.todoapp.mobile.ui.changepassword.ChangePasswordContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel
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

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnCurrentChange ->
                _uiState.update { it.copy(currentPassword = action.value, currentError = null) }
            is UiAction.OnNewChange ->
                _uiState.update {
                    it.copy(
                        newPassword = action.value,
                        newError = validateNew(action.value, it.currentPassword),
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
            UiAction.OnToggleCurrentVisibility ->
                _uiState.update { it.copy(isCurrentVisible = !it.isCurrentVisible) }
            UiAction.OnToggleNewVisibility ->
                _uiState.update { it.copy(isNewVisible = !it.isNewVisible) }
            UiAction.OnToggleConfirmVisibility ->
                _uiState.update { it.copy(isConfirmVisible = !it.isConfirmVisible) }
            UiAction.OnSubmit -> submit()
        }
    }

    private fun validateNew(newPassword: String, currentPassword: String): Int? = when {
        newPassword.isBlank() -> null
        newPassword.length < ChangePasswordContract.MIN_PASSWORD_LENGTH ->
            R.string.error_password_min_length
        newPassword == currentPassword && currentPassword.isNotBlank() ->
            R.string.error_new_password_same
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
            userRepository.changePassword(state.currentPassword, state.newPassword)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _effect.trySend(UiEffect.ShowToast(R.string.password_changed))
                    _navEffect.trySend(NavigationEffect.Back)
                }
                .onFailure { t ->
                    _uiState.update { it.copy(isSubmitting = false) }
                    when (t.message) {
                        "current_password_incorrect" ->
                            _uiState.update { it.copy(currentError = R.string.error_current_password_incorrect) }
                        "new_password_same" ->
                            _uiState.update { it.copy(newError = R.string.error_new_password_same) }
                        else ->
                            _effect.trySend(UiEffect.ShowToast(R.string.error_generic))
                    }
                }
        }
    }
}
