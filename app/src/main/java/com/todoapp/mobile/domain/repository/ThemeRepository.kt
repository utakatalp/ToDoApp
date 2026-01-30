package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.domain.model.ThemePreference
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    val themeFlow: Flow<ThemePreference>
    suspend fun saveTheme(theme: ThemePreference)
}
