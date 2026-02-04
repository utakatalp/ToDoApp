package com.todoapp.mobile.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("data") val data: RegisterResponseData
)

@Serializable
data class RegisterResponseData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: RegisterUser
)

@Serializable
data class RegisterUser(
    val id: Long,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val emailVerified: Boolean,
    val providers: List<String>,
    val createdAt: String
)
