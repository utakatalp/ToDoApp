package com.todoapp.mobile.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDTextField

@Composable
fun SettingsScreen(
    uiState: UiState,
    // uiEffect: Flow<UiEffect>,
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
    Column(
        modifier = modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(horizontal = 16.dp)
    ) {
        TDText(text = stringResource(R.string.privacy_security))
        ReopenSecretModeDropdown(
            selected = uiState.selectedSecretMode.label,
            onSelected = { onAction(UiAction.onSelectedSecretModeChange(it)) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReopenSecretModeDropdown(
    selected: String,
    onSelected: (ReopenSecretMode) -> Unit,
) {
    val options = ReopenSecretMode.entries
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
private fun SettingsContentPreview() {
    SettingsContent(
        modifier = Modifier.padding(16.dp),
        uiState = UiState(secretMode = true, ReopenSecretMode.IMMEDIATE),
        onAction = {},
    )
}

@Suppress("MagicNumber")
enum class ReopenSecretMode(
    val minutes: Long,
    val label: String
) {
    IMMEDIATE(0, "Now"),
    ONE_MIN(1, "1 Minute"),
    THREE_MIN(3, "3 Minutes"),
    FIVE_MIN(5, "5 Minutes"),
    TEN_MIN(10, "10 Minutes"),
    FIFTEEN_MIN(15, "15 Minutes"),
    UNTIL_APP_CLOSED(-1, "Until the app closed")
}
