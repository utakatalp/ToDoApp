package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserData,
)

@Serializable
data class UserData(
    val id: Long,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val emailVerified: Boolean,
    val providers: List<String>,
    val createdAt: String,
)

@Serializable
data class RefreshTokenData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
