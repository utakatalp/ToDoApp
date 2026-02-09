package com.todoapp.mobile.data.auth

import com.todoapp.mobile.data.repository.DataStoreHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenManager @Inject constructor(
    private val dataStoreHelper: DataStoreHelper,
) {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_AVATAR_URL = "avatar_url"
    }

    suspend fun saveTokens(session: AuthModel) {
        dataStoreHelper.saveString(KEY_ACCESS_TOKEN, session.accessToken)
        dataStoreHelper.saveString(KEY_REFRESH_TOKEN, session.refreshToken)
        dataStoreHelper.saveString(KEY_USER_ID, session.userId.toString())
        dataStoreHelper.saveString(KEY_EMAIL, session.email)
        dataStoreHelper.saveString(KEY_DISPLAY_NAME, session.displayName)
        session.avatarUrl?.let { dataStoreHelper.saveString(KEY_AVATAR_URL, it) }
    }

    suspend fun getSession(): AuthModel? {
        val accessToken = getAccessTokenSync() ?: return null
        val refreshToken = getRefreshToken().first()
        val userId = getUserId().first().toLongOrNull() ?: return null
        val email = getEmail().first()
        val displayName = getDisplayName().first()
        val avatarUrl = getAvatarUrl().first().ifBlank { null }

        return AuthModel(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = email,
            displayName = displayName,
            avatarUrl = avatarUrl,
        )
    }

    fun getAccessToken(): Flow<String> {
        return dataStoreHelper.getString(KEY_ACCESS_TOKEN)
    }

    private suspend fun getAccessTokenSync(): String? {
        return dataStoreHelper.getString(KEY_ACCESS_TOKEN).first().ifBlank { null }
    }

    fun getRefreshToken(): Flow<String> {
        return dataStoreHelper.getString(KEY_REFRESH_TOKEN)
    }

    fun getUserId(): Flow<String> {
        return dataStoreHelper.getString(KEY_USER_ID)
    }

    fun getEmail(): Flow<String> {
        return dataStoreHelper.getString(KEY_EMAIL)
    }

    fun getDisplayName(): Flow<String> {
        return dataStoreHelper.getString(KEY_DISPLAY_NAME)
    }

    fun getAvatarUrl(): Flow<String> {
        return dataStoreHelper.getString(KEY_AVATAR_URL)
    }

    suspend fun isLoggedIn(): Boolean {
        return getAccessTokenSync()?.isNotBlank() == true
    }

    suspend fun clearTokens() {
        dataStoreHelper.saveString(KEY_ACCESS_TOKEN, "")
        dataStoreHelper.saveString(KEY_REFRESH_TOKEN, "")
        dataStoreHelper.saveString(KEY_USER_ID, "")
        dataStoreHelper.saveString(KEY_EMAIL, "")
        dataStoreHelper.saveString(KEY_DISPLAY_NAME, "")
        dataStoreHelper.saveString(KEY_AVATAR_URL, "")
    }
}
