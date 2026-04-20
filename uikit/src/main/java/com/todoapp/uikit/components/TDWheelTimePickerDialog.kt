package com.todoapp.uikit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.uikit.R
import com.todoapp.uikit.previews.TDPreviewDialog
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalTime

@Composable
fun TDWheelTimePickerDialog(
    initialTime: LocalTime?,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val now = LocalTime.now()
    var hour by remember { mutableIntStateOf(initialTime?.hour ?: now.hour) }
    var minute by remember { mutableIntStateOf(initialTime?.minute ?: now.minute) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            contentColor = TDTheme.colors.onBackground,
            color = TDTheme.colors.background,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TDWheelTimePicker(
                    hour = hour,
                    minute = minute,
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it },
                )
                TDButton(
                    text = stringResource(R.string.ok),
                    onClick = { onConfirm(LocalTime.of(hour, minute)) },
                    size = TDButtonSize.SMALL,
                )
            }
        }
    }
}

@TDPreviewDialog
@Composable
private fun TDWheelTimePickerDialogPreview() {
    TDTheme {
        TDWheelTimePickerDialog(
            initialTime = LocalTime.of(10, 30),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
