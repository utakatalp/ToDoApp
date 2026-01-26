package com.todoapp.mobile.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.mobile.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : ThemeRepository {

    private companion object {
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    override val themeFlow: Flow<ThemePreference> = dataStore.data.map { preferences ->
        val themeName = preferences[THEME_KEY] ?: ThemePreference.SYSTEM_DEFAULT.name
        ThemePreference.valueOf(themeName)
    }

    override suspend fun saveTheme(theme: ThemePreference) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}
