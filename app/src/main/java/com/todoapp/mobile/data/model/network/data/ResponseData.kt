package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponseData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: RegisterUser,
)

@Serializable
data class RegisterUser(
    val id: Long,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val emailVerified: Boolean,
    val providers: List<String>,
    val createdAt: String,
)

@Serializable
data class LoginResponseData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: LoginUser,
)

@Serializable
data class LoginUser(
    val id: Long,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val emailVerified: Boolean,
    val providers: List<String>,
    val createdAt: String,
)
