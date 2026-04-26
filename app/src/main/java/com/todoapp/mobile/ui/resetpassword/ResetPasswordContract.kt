package com.todoapp.mobile.ui.resetpassword

import androidx.annotation.StringRes

object ResetPasswordContract {
    data class UiState(
        val newPassword: String = "",
        val confirmPassword: String = "",
        val isNewVisible: Boolean = false,
        val isConfirmVisible: Boolean = false,
        val isSubmitting: Boolean = false,
        val isDone: Boolean = false,
        @StringRes val newError: Int? = null,
        @StringRes val confirmError: Int? = null,
    ) {
        val canSubmit: Boolean
            get() = !isSubmitting &&
                !isDone &&
                newPassword.length >= MIN_PASSWORD_LENGTH &&
                confirmPassword == newPassword
    }

    sealed interface UiAction {
        data class OnNewChange(val value: String) : UiAction
        data class OnConfirmChange(val value: String) : UiAction
        data object OnToggleNewVisibility : UiAction
        data object OnToggleConfirmVisibility : UiAction
        data object OnSubmit : UiAction
        data object OnBackToLogin : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(@StringRes val messageRes: Int) : UiEffect
    }

    const val MIN_PASSWORD_LENGTH = 8
}
