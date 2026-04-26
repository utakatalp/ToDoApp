package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForgotPasswordRequest(
    @SerialName("email") val email: String,
)

@Serializable
data class ResetPasswordRequest(
    @SerialName("token") val token: String,
    @SerialName("newPassword") val newPassword: String,
)

@Serializable
data class ChangePasswordRequest(
    @SerialName("currentPassword") val currentPassword: String,
    @SerialName("newPassword") val newPassword: String,
)
