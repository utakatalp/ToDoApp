package com.todoapp.mobile.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.mobile.domain.repository.ThemeRepository
import com.todoapp.uikit.theme.TDTheme

@Composable
fun ThemedApp(
    themeRepository: ThemeRepository
) {
    val themePreference by themeRepository.themeFlow
        .collectAsState(initial = ThemePreference.SYSTEM_DEFAULT)

    val darkTheme = when (themePreference) {
        ThemePreference.DARK_MODE -> true
        ThemePreference.LIGHT_MODE -> false
        ThemePreference.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    TDTheme(darkTheme = darkTheme) {
        ToDoApp()
    }
}
