package com.todoapp.mobile.domain.repository

interface SessionPreferences {

    suspend fun setAccessToken(token: String)

    suspend fun getAccessToken(): String?

    suspend fun setRefreshToken(token: String)

    suspend fun getRefreshToken(): String?

    suspend fun setExpiresAt(expiresIn: Long)

    suspend fun getExpiresAt(): Long?

    suspend fun clear(): Boolean
}
