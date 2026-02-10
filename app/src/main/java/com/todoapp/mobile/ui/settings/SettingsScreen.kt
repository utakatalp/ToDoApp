package com.todoapp.mobile.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.mobile.domain.security.SecretModeReopenOptions
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import com.todoapp.uikit.components.TDNotificationPermissionItem
import com.todoapp.uikit.components.TDOverlayPermissionItem
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun SettingsScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    SettingsContent(
        uiState = uiState,
        onAction = onAction
    )
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        TDText(
            text = stringResource(R.string.privacy_security),
            style = TDTheme.typography.heading1
        )

        ThemeSelector(
            currentTheme = uiState.currentTheme,
            onThemeChange = { theme ->
                onAction(UiAction.OnThemeChange(theme))
            }
        )

        HorizontalDivider(
            color = TDTheme.colors.onBackground.copy(alpha = 0.3f)
        )

        Row(
            modifier
                .fillMaxWidth()
                .clickable { (onAction(UiAction.OnNavigateToSecretModeSettings)) }
        ) {
            TDText(
                text = stringResource(R.string.privacy_security),
                style = TDTheme.typography.heading4,
                color = TDTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(com.example.uikit.R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = TDTheme.colors.onBackground
            )
        }
        TDNotificationPermissionItem()
        TDOverlayPermissionItem(context)
    }
}
/*
        HorizontalDivider(
            color = TDTheme.colors.onBackground.copy(alpha = 0.3f)
        )
    }
}

 */

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    TDTheme {
        SettingsScreen(
            uiState = UiState(
                currentTheme = ThemePreference.SYSTEM_DEFAULT,
                selectedSecretMode = SecretModeReopenOptions.Immediate,
                remainedSecretModeTime = "",
                isSecretModeActive = true
            ),
            onAction = {}
        )
    }
}
