package com.todoapp.mobile.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import com.todoapp.uikit.components.TDOverlayPermissionItem
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun SettingsScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { onAction(UiAction.OnBackClick) },
            ) {
                Icon(
                    painter = painterResource(com.example.uikit.R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = TDTheme.colors.onBackground,
                )
            }

            Spacer(Modifier.weight(1f))

            TDText(
                text = stringResource(R.string.settings_title),
                style = TDTheme.typography.heading3,
                color = TDTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TDOverlayPermissionItem(context)

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ThemeSelector(
                currentTheme = uiState.currentTheme,
                onThemeChange = { theme ->
                    onAction(UiAction.OnThemeChange(theme))
                }
            )
        }
    }
}

@Preview("Light", uiMode = AndroidUiModes.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun SettingsScreenPreview_Light() {
    TDTheme {
        SettingsScreen(
            uiState = UiState(currentTheme = ThemePreference.LIGHT_MODE),
            onAction = {}
        )
    }
}

@Preview("Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun SettingsScreenPreview_Dark() {
    TDTheme {
        SettingsScreen(
            uiState = UiState(currentTheme = ThemePreference.DARK_MODE),
            onAction = {}
        )
    }
}
