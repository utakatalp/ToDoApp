package com.todoapp.mobile.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun ThemeSelector(
    modifier: Modifier = Modifier,
    currentTheme: ThemePreference,
    onThemeChange: (ThemePreference) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TDText(
            text = stringResource(R.string.app_theme),
            style = TDTheme.typography.heading4,
            color = TDTheme.colors.onBackground,
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = TDTheme.colors.background,
            border = BorderStroke(1.dp, TDTheme.colors.gray)
        ) {
            Row {
                ThemeItem(
                    selected = currentTheme == ThemePreference.LIGHT_MODE,
                    onClick = { onThemeChange(ThemePreference.LIGHT_MODE) },
                    icon = painterResource(com.example.uikit.R.drawable.ic_light_mode)
                )

                ThemeItem(
                    selected = currentTheme == ThemePreference.DARK_MODE,
                    onClick = { onThemeChange(ThemePreference.DARK_MODE) },
                    icon = painterResource(com.example.uikit.R.drawable.ic_dark_mode)
                )

                ThemeItem(
                    selected = currentTheme == ThemePreference.SYSTEM_DEFAULT,
                    onClick = { onThemeChange(ThemePreference.SYSTEM_DEFAULT) },
                    icon = painterResource(com.example.uikit.R.drawable.ic_system_default)
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: Painter,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) {
                    TDTheme.colors.primary
                } else {
                    Color.Transparent
                }
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = if (selected) {
                TDTheme.colors.onBackground
            } else {
                TDTheme.colors.gray
            },
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true, uiMode = AndroidUiModes.UI_MODE_NIGHT_NO)
@Composable
private fun ThemeSelectorAllStatesPreview_light() {
    TDTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeSelector(
                currentTheme = ThemePreference.LIGHT_MODE,
                onThemeChange = {}
            )
            ThemeSelector(
                currentTheme = ThemePreference.DARK_MODE,
                onThemeChange = {}
            )
            ThemeSelector(
                currentTheme = ThemePreference.SYSTEM_DEFAULT,
                onThemeChange = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun ThemeSelectorAllStatesPreview_dark() {
    TDTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ThemeSelector(
                currentTheme = ThemePreference.LIGHT_MODE,
                onThemeChange = {}
            )
            ThemeSelector(
                currentTheme = ThemePreference.DARK_MODE,
                onThemeChange = {}
            )
            ThemeSelector(
                currentTheme = ThemePreference.SYSTEM_DEFAULT,
                onThemeChange = {}
            )
        }
    }
}
