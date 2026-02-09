package com.todoapp.mobile.data.auth

data class AuthModel(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long,
    val email: String,
    val displayName: String,
    val avatarUrl: String? = null,
)
