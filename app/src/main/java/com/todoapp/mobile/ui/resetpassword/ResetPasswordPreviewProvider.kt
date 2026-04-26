package com.todoapp.mobile.ui.resetpassword

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.todoapp.mobile.R

class ResetPasswordPreviewProvider : PreviewParameterProvider<ResetPasswordContract.UiState> {
    override val values: Sequence<ResetPasswordContract.UiState>
        get() =
            sequenceOf(
                ResetPasswordContract.UiState(),
                ResetPasswordContract.UiState(
                    newPassword = "abc",
                    newError = R.string.error_password_min_length,
                ),
                ResetPasswordContract.UiState(
                    newPassword = "newpassword",
                    confirmPassword = "different",
                    confirmError = R.string.error_passwords_dont_match,
                ),
                ResetPasswordContract.UiState(
                    newPassword = "newpassword",
                    confirmPassword = "newpassword",
                    isSubmitting = true,
                ),
                ResetPasswordContract.UiState(
                    newPassword = "newpassword",
                    confirmPassword = "newpassword",
                    isDone = true,
                ),
                ResetPasswordContract.UiState(
                    newPassword = "newpassword",
                    confirmPassword = "newpassword",
                    isNewVisible = true,
                    isConfirmVisible = true,
                ),
            )
}
