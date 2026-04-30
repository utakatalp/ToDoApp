package com.todoapp.mobile.ui.changepassword

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

object ChangePasswordContract {
    @Immutable
    data class UiState(
        val currentPassword: String = "",
        val newPassword: String = "",
        val confirmPassword: String = "",
        val isCurrentVisible: Boolean = false,
        val isNewVisible: Boolean = false,
        val isConfirmVisible: Boolean = false,
        val isSubmitting: Boolean = false,
        @StringRes val currentError: Int? = null,
        @StringRes val newError: Int? = null,
        @StringRes val confirmError: Int? = null,
    ) {
        val canSubmit: Boolean
            get() = !isSubmitting &&
                currentPassword.isNotBlank() &&
                newPassword.length >= MIN_PASSWORD_LENGTH &&
                confirmPassword == newPassword &&
                newPassword != currentPassword
    }

    sealed interface UiAction {
        data class OnCurrentChange(val value: String) : UiAction
        data class OnNewChange(val value: String) : UiAction
        data class OnConfirmChange(val value: String) : UiAction
        data object OnToggleCurrentVisibility : UiAction
        data object OnToggleNewVisibility : UiAction
        data object OnToggleConfirmVisibility : UiAction
        data object OnSubmit : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(@StringRes val messageRes: Int) : UiEffect
    }

    const val MIN_PASSWORD_LENGTH = 8
}
