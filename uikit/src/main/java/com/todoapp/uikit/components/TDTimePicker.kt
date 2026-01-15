package com.todoapp.uikit.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme
import com.todoapp.uikit.theme.timePickerColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDTimePickerDialog(
    modifier: Modifier = Modifier,
    title: String,
    placeholder: String,
    selectedTime: LocalTime?,
    onTimeChange: (LocalTime) -> Unit,
    isError: Boolean = false,
) {
    var isPickerOpen by rememberSaveable { mutableStateOf(false) }
    val currentTime = Calendar.getInstance()
    val timePickerState =
        rememberTimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            is24Hour = true,
        )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        TDPickerField(
            title = title,
            value =
                selectedTime?.format(
                    DateTimeFormatter.ofPattern(
                        "HH:mm",
                    ),
                ) ?: placeholder,
            onClick = { isPickerOpen = true },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    tint = TDTheme.colors.onBackground,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            },
            isError = isError
        )
        if (isPickerOpen) {
            Dialog(
                onDismissRequest = { isPickerOpen = false },
            ) {
                Surface(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    contentColor = TDTheme.colors.onBackground,
                    color = TDTheme.colors.background,
                ) {
                    TDTimePicker(
                        timePickerState = timePickerState,
                        onConfirm = {
                            onTimeChange(it)
                            isPickerOpen = false
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDTimePicker(
    modifier: Modifier = Modifier,
    onConfirm: (LocalTime) -> Unit,
    timePickerState: TimePickerState,
) {
    Column(
        modifier = modifier
            .widthIn(max = 300.dp)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        TimeInput(
            state = timePickerState,
            colors = timePickerColors(),
        )
        TDButton(
            text = stringResource(R.string.ok),
            onClick = {
                onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
            },
            size = TDButtonSize.SMALL,
        )
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun TDTimePickerPreview() {
    TDTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TDTimePickerDialog(
                title = "Set Time",
                placeholder = "HH:MM",
                selectedTime = LocalTime.now(),
                onTimeChange = { },
            )
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun TDTimePickerPreview_Dark() {
    TDTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TDTimePickerDialog(
                title = "Set Time",
                placeholder = "HH:MM",
                selectedTime = LocalTime.now(),
                onTimeChange = { },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    name = "Time Picker Dialog (Open) – Light",
)
@Composable
fun TDTimePickerDialogOpenPreview_Light() {
    TDTheme {
        val timePickerState = rememberTimePickerState(
            initialHour = 10,
            initialMinute = 15,
            is24Hour = true,
        )

        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                contentColor = TDTheme.colors.onBackground,
                color = TDTheme.colors.background,
            ) {
                TDTimePicker(
                    timePickerState = timePickerState,
                    onConfirm = {},
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Time Picker Dialog (Open) – Dark",
)
@Composable
fun TDTimePickerDialogOpenPreview_Dark() {
    TDTheme {
        val timePickerState = rememberTimePickerState(
            initialHour = 10,
            initialMinute = 15,
            is24Hour = true,
        )

        Dialog(onDismissRequest = {}) {
            Surface(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                contentColor = TDTheme.colors.onBackground,
                color = TDTheme.colors.background,
            ) {
                TDTimePicker(
                    timePickerState = timePickerState,
                    onConfirm = {},
                )
            }
        }
    }
}
