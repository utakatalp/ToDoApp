package com.todoapp.uikit.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun textFieldColors(): TextFieldColors =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        focusedContainerColor = MaterialTheme.colorScheme.background,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        disabledContainerColor = TDTheme.colors.background.copy(alpha = 0.8f),
        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledBorderColor = TDTheme.colors.onBackground.copy(alpha = 0.8f),
        disabledLabelColor = TDTheme.colors.onBackground.copy(alpha = 0.8f),
        disabledTrailingIconColor = TDTheme.colors.onBackground.copy(alpha = 0.8f),
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.primary,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun timePickerColors(): TimePickerColors =
    TimePickerDefaults.colors(
        containerColor = TDTheme.colors.purple,
        timeSelectorSelectedContainerColor = TDTheme.colors.lightPurple,
        timeSelectorUnselectedContainerColor = TDTheme.colors.lightPurple,
    )
