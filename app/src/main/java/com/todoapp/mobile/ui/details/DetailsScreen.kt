package com.todoapp.mobile.ui.details

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.details.DetailsContract.UiAction
import com.todoapp.mobile.ui.details.DetailsContract.UiEffect
import com.todoapp.mobile.ui.details.DetailsContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDCompactOutlinedTextField
import com.todoapp.uikit.components.TDDatePickerDialog
import com.todoapp.uikit.components.TDLoadingBar
import com.todoapp.uikit.components.TDPickerField
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDWheelTimePicker
import com.todoapp.uikit.extensions.collectWithLifecycle
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DetailsScreen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
) {
    val context = LocalContext.current

    uiEffect.collectWithLifecycle {
        when (it) {
            is UiEffect.ShowToast -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
    ) {
        when (uiState) {
            is UiState.Loading -> DetailsLoadingContent()
            is UiState.Error -> DetailsErrorContent(uiState.message, onAction)
            is UiState.Success -> DetailsSuccessContent(uiState, onAction)
        }
    }
}

@Composable
private fun DetailsLoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TDLoadingBar()
    }
}

@Composable
private fun DetailsErrorContent(
    message: String,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(com.example.uikit.R.drawable.ic_error),
            contentDescription = null,
            tint = TDTheme.colors.crossRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        TDText(
            text = message,
            style = TDTheme.typography.heading3,
            color = TDTheme.colors.onSurface
        )
        Spacer(Modifier.height(24.dp))
        TDButton(
            text = stringResource(R.string.retry),
            onClick = { onAction(UiAction.OnRetry) },
            size = TDButtonSize.SMALL
        )
    }
}

@Composable
private fun DetailsSuccessContent(
    uiState: UiState.Success,
    onAction: (UiAction) -> Unit,
) {
    val verticalScroll = rememberScrollState()
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(verticalScroll)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TDCompactOutlinedTextField(
                    label = stringResource(R.string.task_title),
                    value = uiState.taskTitle,
                    onValueChange = { onAction(UiAction.OnTaskTitleEdit(it)) },
                    isError = uiState.titleError != null,
                    supportingText = uiState.titleError?.let { stringResource(it) },
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TDText(
                        text = stringResource(R.string.task_date),
                        style = TDTheme.typography.heading6,
                        color = TDTheme.colors.onSurface
                    )
                    TDDatePickerDialog(
                        selectedDate = uiState.dialogSelectedDate,
                        onDateSelect = { onAction(UiAction.OnDialogDateSelect(it)) },
                        onDateDeselect = { onAction(UiAction.OnDialogDateDeselect) },
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        TDPickerField(
                            title = stringResource(R.string.set_time),
                            value = uiState.taskTimeStart?.format(timeFormatter)
                                ?: stringResource(R.string.starts),
                            onClick = { showStartTimePicker = true },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(com.example.uikit.R.drawable.ic_clock),
                                    tint = TDTheme.colors.onBackground,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        TDPickerField(
                            title = "",
                            value = uiState.taskTimeEnd?.format(timeFormatter)
                                ?: stringResource(R.string.ends),
                            onClick = { showEndTimePicker = true },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(com.example.uikit.R.drawable.ic_clock),
                                    tint = TDTheme.colors.onBackground,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                        )
                    }
                }

                TDCompactOutlinedTextField(
                    label = stringResource(R.string.description),
                    value = uiState.taskDescription,
                    onValueChange = { onAction(UiAction.OnTaskDescriptionEdit(it)) },
                    singleLine = false,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TDText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.edit_details_hint),
                    style = TDTheme.typography.subheading3,
                    color = TDTheme.colors.onBackground.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(12.dp))
                Image(
                    painter = painterResource(
                        if (TDTheme.isDark) R.drawable.ic_edit_robot_dark else R.drawable.ic_edit_robot_light
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.28f)
                        .aspectRatio(1f)
                )
            }

            Spacer(Modifier.height(4.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TDTheme.colors.background)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TDButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.save_changes),
                onClick = { onAction(UiAction.OnSaveChanges) },
                size = TDButtonSize.MEDIUM,
                isEnable = uiState.isDirty && !uiState.isSaving,
                fullWidth = true
            )
            TDButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.cancel),
                onClick = { onAction(UiAction.OnCancelClick) },
                size = TDButtonSize.MEDIUM,
                type = TDButtonType.SECONDARY,
                fullWidth = true
            )
        }
    }

    if (showStartTimePicker) {
        WheelTimePickerDialog(
            initialTime = uiState.taskTimeStart,
            onConfirm = {
                onAction(UiAction.OnTaskTimeStartEdit(it))
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false },
        )
    }

    if (showEndTimePicker) {
        WheelTimePickerDialog(
            initialTime = uiState.taskTimeEnd,
            onConfirm = {
                onAction(UiAction.OnTaskTimeEndEdit(it))
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false },
        )
    }
}

@Composable
private fun WheelTimePickerDialog(
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
                    text = stringResource(com.example.uikit.R.string.ok),
                    onClick = { onConfirm(LocalTime.of(hour, minute)) },
                    size = TDButtonSize.SMALL,
                )
            }
        }
    }
}

@Preview("Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun DetailsLoadingPreview() {
    TDTheme { DetailsLoadingContent() }
}

@Preview("Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun DetailsErrorPreview() {
    TDTheme { DetailsErrorContent("Task not found") {} }
}

@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun DetailsSuccessPreview_Dark() {
    TDTheme { DetailsSuccessContent(DetailsPreviewData.successState()) {} }
}

@Preview("Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun DetailsSuccessPreview_Light() {
    TDTheme { DetailsSuccessContent(DetailsPreviewData.successState()) {} }
}
