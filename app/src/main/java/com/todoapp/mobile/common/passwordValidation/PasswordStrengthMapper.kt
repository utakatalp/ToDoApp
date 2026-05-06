package com.todoapp.mobile.common.passwordValidation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.todoapp.mobile.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun PasswordStrength.toProgress(): Triple<Float, Color, String> = when (this) {
    PasswordStrength.STRONG ->
        Triple(
            1f,
            TDTheme.colors.green,
            stringResource(R.string.register_password_strength_STRONG),
        )

    PasswordStrength.MEDIUM ->
        Triple(
            0.50f,
            // lightYellow (#FFF8E1) is a near-white surface tint — invisible on the light
            // theme's near-white background. Use the saturated orange accent so the bar
            // and trailing label read clearly on both themes.
            TDTheme.colors.orange,
            stringResource(R.string.register_password_strength_MEDIUM),
        )

    PasswordStrength.WEAK ->
        Triple(
            0.25f,
            TDTheme.colors.crossRed,
            stringResource(R.string.register_password_strength_WEAK),
        )
}
