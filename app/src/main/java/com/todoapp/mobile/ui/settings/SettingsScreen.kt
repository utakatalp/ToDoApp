package com.todoapp.mobile.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.mobile.domain.security.SecretModeReopenOption
import com.todoapp.mobile.domain.security.SecretModeReopenOptions
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDOverlayPermissionItem
import com.todoapp.uikit.components.TDPlanTimePickerField
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDTextField
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalTime

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
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TDOverlayPermissionItem(context)

        Spacer(modifier = Modifier.height(8.dp))

        ThemeSelector(
            currentTheme = uiState.currentTheme,
            onThemeChange = { theme ->
                onAction(UiAction.OnThemeChange(theme))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TDPlanTimePickerField(
            title = stringResource(R.string.plan_your_day),
            subtitle = stringResource(R.string.when_do_you_want_to_get_notified),
            time = uiState.dailyPlanTime,
            onTimeChange = { onAction(UiAction.OnDailyPlanTimeChange(it)) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TDText(
            text = stringResource(R.string.privacy_security),
            style = TDTheme.typography.heading1,
            color = TDTheme.colors.onBackground
        )

        TDText(
            text = stringResource(R.string.the_time_of_reopening_for_secret_mode),
            style = TDTheme.typography.heading3,
            color = TDTheme.colors.onBackground
        )

        ReopenSecretModeDropdown(
            selected = uiState.selectedSecretMode.label,
            onSelected = { onAction(UiAction.OnSelectedSecretModeChange(it)) }
        )

        if (uiState.remainedSecretModeTime.isNotBlank()) {
            TDText(
                text = uiState.remainedSecretModeTime,
                style = TDTheme.typography.heading6,
                color = TDTheme.colors.onBackground
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TDButton(
            text = stringResource(R.string.save),
            modifier = Modifier.fillMaxWidth()
        ) {
            onAction(UiAction.OnSettingsSave)
        }

        TDButton(
            text = stringResource(R.string.disable_secret_mode),
            type = TDButtonType.CANCEL,
            modifier = Modifier.fillMaxWidth(),
            isEnable = uiState.isSecretModeActive
        ) {
            onAction(UiAction.OnDisableSecretModeTap)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReopenSecretModeDropdown(
    selected: String,
    onSelected: (SecretModeReopenOption) -> Unit,
) {
    val options = SecretModeReopenOptions.all
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TDTextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            value = selected,
            onValueChange = {},
            label = stringResource(R.string.the_time_of_reopening_for_secret_mode),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = false
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { TDText(text = option.label) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    TDTheme {
        SettingsScreen(
            uiState = UiState(
                currentTheme = ThemePreference.SYSTEM_DEFAULT,
                selectedSecretMode = SecretModeReopenOptions.Immediate,
                remainedSecretModeTime = "",
                isSecretModeActive = true,
                dailyPlanTime = LocalTime.of(9, 0)
            ),
            onAction = {}
        )
    }
}
