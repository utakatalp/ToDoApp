package com.todoapp.mobile.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.todoapp.mobile.domain.repository.SessionPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SessionPreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SessionPreferences {

    override suspend fun setAccessToken(token: String) {
        return withContext(Dispatchers.IO) {
            sharedPreferences.edit { putString(KEY_ACCESS_TOKEN, token) }
        }
    }

    override suspend fun getAccessToken(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        }
    }

    override suspend fun setRefreshToken(token: String) {
        return withContext(Dispatchers.IO) {
            sharedPreferences.edit { putString(KEY_REFRESH_TOKEN, token) }
        }
    }

    override suspend fun getRefreshToken(): String? {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        }
    }

    override suspend fun setExpiresAt(expiresIn: Long) {
        return withContext(Dispatchers.IO) {
            val expiresAtMillis = System.currentTimeMillis() + (expiresIn * MILLIS_IN_SECOND)
            sharedPreferences.edit {
                putLong(KEY_EXPIRES_AT, expiresAtMillis)
            }
        }
    }

    override suspend fun getExpiresAt(): Long {
        return withContext(Dispatchers.IO) {
            sharedPreferences.getLong(KEY_EXPIRES_AT, 0)
        }
    }

    override suspend fun clear(): Boolean {
        return withContext(Dispatchers.IO) {
            sharedPreferences.edit { clear() }
            true
        }
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EXPIRES_AT = "expires_at"
    }
}
