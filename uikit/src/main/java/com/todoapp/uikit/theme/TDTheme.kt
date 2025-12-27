package com.todoapp.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

object TDTheme {
    val colors: TDColor
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) LocalDarkColors.current else LocalLightColors.current

    // val icons: TDIcons
    //    @Composable
    //    @ReadOnlyComposable
    //    get() = LocalIcons.current

    val typography: TDTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

@Composable
fun TDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) darkColors() else lightColors()

    CompositionLocalProvider(
        LocalLightColors provides colors,
        LocalDarkColors provides colors,
        // LocalIcons provides TDTheme.icons,
        LocalTypography provides TDTheme.typography,
    ) {
        content()
    }
}
