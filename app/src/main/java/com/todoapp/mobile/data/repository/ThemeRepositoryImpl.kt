package com.todoapp.mobile.data.repository

import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.mobile.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val dataStoreHelper: DataStoreHelper,
) : ThemeRepository {

    private companion object {
        const val THEME_KEY = "app_theme"
    }

    override val themeFlow: Flow<ThemePreference> =
        dataStoreHelper.getString(THEME_KEY, ThemePreference.SYSTEM_DEFAULT.name)
            .map { ThemePreference.valueOf(it) }

    override suspend fun saveTheme(theme: ThemePreference) {
        dataStoreHelper.saveString(THEME_KEY, theme.name)
    }
}
