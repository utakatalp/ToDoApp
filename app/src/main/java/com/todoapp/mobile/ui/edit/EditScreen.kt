package com.todoapp.mobile.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.edit.EditContract.UiAction
import com.todoapp.mobile.ui.edit.EditContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDCompactOutlinedTextField
import com.todoapp.uikit.components.TDDatePickerDialog
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDTimePickerDialog
import com.todoapp.uikit.theme.TDTheme

@Composable
fun EditScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    val verticalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .systemBarsPadding()
            .imePadding()
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
                text = stringResource(R.string.edit_task_details),
                style = TDTheme.typography.heading3,
                color = TDTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.weight(1f))
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(verticalScroll),
                verticalArrangement = Arrangement.Center,
            ) {
                TDCompactOutlinedTextField(
                    label = stringResource(R.string.task_title),
                    value = uiState.taskTitle,
                    onValueChange = { onAction(UiAction.OnTaskTitleEdit(it)) },
                    isError = uiState.titleError != null,
                    supportingText = uiState.titleError?.let { stringResource(it) },
                )

                Spacer(Modifier.height(12.dp))

                TDDatePickerDialog(
                    selectedDate = uiState.dialogSelectedDate,
                    onDateSelect = { onAction(UiAction.OnDialogDateSelect(it)) },
                    onDateDeselect = { onAction(UiAction.OnDialogDateDeselect) },
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    TDTimePickerDialog(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.set_time),
                        placeholder = stringResource(R.string.starts),
                        selectedTime = uiState.taskTimeStart,
                        onTimeChange = { onAction(UiAction.OnTaskTimeStartEdit(it)) },
                    )
                    Spacer(Modifier.width(12.dp))

                    TDTimePickerDialog(
                        modifier = Modifier.weight(1f),
                        title = "",
                        placeholder = stringResource(R.string.ends),
                        selectedTime = uiState.taskTimeEnd,
                        onTimeChange = { onAction(UiAction.OnTaskTimeEndEdit(it)) },
                    )
                }
                Spacer(Modifier.height(12.dp))

                TDCompactOutlinedTextField(
                    label = stringResource(R.string.description),
                    value = uiState.taskDescription,
                    onValueChange = { onAction(UiAction.OnTaskDescriptionEdit(it)) },
                    singleLine = false,
                )
                Spacer(Modifier.height(24.dp))

                Column {
                    TDButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.save_changes),
                        onClick = { onAction(UiAction.OnSaveChanges) },
                        size = TDButtonSize.SMALL,
                        isEnable = uiState.isDirty
                    )
                    Spacer(Modifier.height(12.dp))
                    TDButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.cancel),
                        onClick = { onAction(UiAction.OnCancelClick) },
                        size = TDButtonSize.SMALL,
                        type = TDButtonType.SECONDARY,
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Preview("Light", uiMode = AndroidUiModes.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun EditScreenPreview_Light() {
    TDTheme {
        EditScreen(
            uiState = UiState(),
            onAction = {}
        )
    }
}

@Preview("Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun EditScreenPreview_Dark() {
    TDTheme {
        EditScreen(
            uiState = UiState(),
            onAction = {}
        )
    }
}
