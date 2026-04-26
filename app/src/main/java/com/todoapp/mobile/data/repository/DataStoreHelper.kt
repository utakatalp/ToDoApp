package com.todoapp.mobile.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.todoapp.mobile.data.model.network.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DataStoreHelper
@Inject
constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun getString(
        key: String,
        defaultValue: String = "",
    ): Flow<String> {
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[prefKey] ?: defaultValue
        }
    }

    fun observeOptionalString(key: String): Flow<String?> {
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.map { preferences ->
            preferences[prefKey]
        }
    }

    suspend fun saveString(
        key: String,
        value: String,
    ) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[prefKey] = value
        }
    }

    fun observeUser(): Flow<UserData?> = dataStore.data.map { preferences ->
        preferences[USER_KEY]?.let { rawJson ->
            runCatching { json.decodeFromString<UserData>(rawJson) }
                .getOrNull()
        }
    }

    suspend fun setUser(userData: UserData) {
        val rawJson = json.encodeToString(userData)
        dataStore.edit { preferences ->
            preferences[USER_KEY] = rawJson
        }
    }

    suspend fun clearUser() {
        dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
        }
    }

    val isLoggedIn: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }

    suspend fun setLoggedIn(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = value
        }
    }

    fun observeFirstLoginPermissionPromptPending(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[FIRST_LOGIN_PERMISSION_PROMPT_PENDING] ?: false
    }

    suspend fun setFirstLoginPermissionPromptPending(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_LOGIN_PERMISSION_PROMPT_PENDING] = value
        }
    }

    companion object {
        private val json =
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = false
            }
        private val USER_KEY = stringPreferencesKey("user")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val FIRST_LOGIN_PERMISSION_PROMPT_PENDING =
            booleanPreferencesKey("first_login_permission_prompt_pending")
    }
}
