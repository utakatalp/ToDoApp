package com.todoapp.mobile.ui.changepassword

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.todoapp.mobile.R

class ChangePasswordPreviewProvider : PreviewParameterProvider<ChangePasswordContract.UiState> {
    override val values: Sequence<ChangePasswordContract.UiState>
        get() =
            sequenceOf(
                ChangePasswordContract.UiState(),
                ChangePasswordContract.UiState(
                    currentPassword = "wrongpass",
                    currentError = R.string.error_current_password_incorrect,
                ),
                ChangePasswordContract.UiState(
                    currentPassword = "oldpass1",
                    newPassword = "abc",
                    newError = R.string.error_password_min_length,
                ),
                ChangePasswordContract.UiState(
                    currentPassword = "oldpass1",
                    newPassword = "newpassword",
                    confirmPassword = "different",
                    confirmError = R.string.error_passwords_dont_match,
                ),
                ChangePasswordContract.UiState(
                    currentPassword = "oldpass1",
                    newPassword = "newpassword",
                    confirmPassword = "newpassword",
                    isSubmitting = true,
                ),
                ChangePasswordContract.UiState(
                    currentPassword = "oldpass1",
                    newPassword = "newpassword",
                    confirmPassword = "newpassword",
                    isCurrentVisible = true,
                    isNewVisible = true,
                    isConfirmVisible = true,
                ),
            )
}
