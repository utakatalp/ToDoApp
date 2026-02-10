package com.todoapp.mobile.ui.home

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.uikit.R
import com.todoapp.mobile.common.maskTitle
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiEffect
import com.todoapp.mobile.ui.home.HomeContract.UiState
import com.todoapp.mobile.ui.security.biometric.BiometricAuthenticator
import com.todoapp.uikit.components.TDAddTaskButton
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDCompactOutlinedTextField
import com.todoapp.uikit.components.TDDatePickerDialog
import com.todoapp.uikit.components.TDScreenWithSheet
import com.todoapp.uikit.components.TDStatisticCard
import com.todoapp.uikit.components.TDTaskCardWithCheckbox
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDTimePickerDialog
import com.todoapp.uikit.components.TDWeeklyDatePicker
import com.todoapp.uikit.extensions.collectWithLifecycle
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HomeScreen(
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

            is UiEffect.ShowBiometricAuthenticator -> {
                handleBiometricAuthentication(context) {
                    onAction(UiAction.OnSuccessfulBiometricAuthenticationHandle)
                }
            }

            is UiEffect.ShowError -> TODO()
        }
    }
    TDScreenWithSheet(
        isSheetOpen = uiState.isSheetOpen,
        sheetContent = {
            AddTaskSheet(
                uiState = uiState,
                onClick = { onAction(UiAction.OnTaskCreate) },
                onAction = onAction,
            )
        },
        onDismissSheet = { onAction(UiAction.OnDismissBottomSheet) },
    ) {
        HomeContent(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(TDTheme.colors.background)
                    .padding(horizontal = 16.dp),
            uiState = uiState,
            onAction = onAction,
        )
    }
}

@Composable
fun HomeContent(
    modifier: Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        onAction(UiAction.OnMoveTask(from.index, to.index))
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }
    Column(modifier = modifier.fillMaxSize()) {
        TDWeeklyDatePicker(
            modifier = Modifier,
            selectedDate = uiState.selectedDate,
            onDateSelect = { onAction(UiAction.OnDateSelect(it)) },
        )
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            TDStatisticCard(
                text = stringResource(com.todoapp.mobile.R.string.task_complete),
                taskAmount = uiState.completedTaskCountThisWeek,
                modifier = Modifier.weight(1f),
                isCompleted = true,
            )
            Spacer(modifier = Modifier.size(20.dp))
            TDStatisticCard(
                text = stringResource(com.todoapp.mobile.R.string.task_pending),
                taskAmount = uiState.pendingTaskCountThisWeek,
                modifier = Modifier.weight(1f),
                isCompleted = false,
            )
        }
        Spacer(Modifier.height(32.dp))
        TDText(
            text = stringResource(com.todoapp.mobile.R.string.tasks_today),
            color = TDTheme.colors.onBackground,
            style = TDTheme.typography.heading3
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.tasks,
                    key = { task -> task.id }
                ) { task ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = task.id
                    ) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                        Surface(
                            shadowElevation = elevation,
                            color = TDTheme.colors.background
                        ) {
                            TDTaskCardWithCheckbox(
                                modifier = Modifier
                                    .combinedClickable(
                                        onLongClick = { onAction(UiAction.OnTaskLongPress(task)) },
                                        onClick = { onAction(UiAction.OnTaskClick(task)) }
                                    )
                                    .draggableHandle(
                                        onDragStarted = {
                                            hapticFeedback.performHapticFeedback(
                                                HapticFeedbackType.GestureThresholdActivate
                                            )
                                        },
                                        onDragStopped = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        }
                                    ),
                                taskText = if (task.isSecret) task.title.maskTitle() else task.title,
                                isChecked = task.isCompleted,
                                onCheckBoxClick = {
                                    onAction(UiAction.OnTaskCheck(task))
                                },
                                onEditClick = { onAction(UiAction.OnEditClick(task)) }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    modifier = Modifier.size(56.dp),
                    onClick = { onAction(UiAction.OnPomodoroTap) }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_pomodoro_technique),
                        contentDescription = null
                    )
                }

                TDAddTaskButton(
                    modifier = Modifier.size(56.dp),
                    onClick = { onAction(UiAction.OnShowBottomSheet) }
                )
            }
        }
        if (uiState.isDeleteDialogOpen) {
            AlertDialog(
                onDismissRequest = { onAction(UiAction.OnDeleteDialogDismiss) },
                title = { Text("Delete task?") },
                titleContentColor = TDTheme.colors.onBackground,
                containerColor = TDTheme.colors.background,
                textContentColor = TDTheme.colors.onBackground,
                text = { Text("Do you want to delete the task?") },
                confirmButton = {
                    TextButton(onClick = { onAction(UiAction.OnDeleteDialogConfirm) }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onAction(UiAction.OnDeleteDialogDismiss) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun AdvancedSettings(
    isExpanded: Boolean,
    isSecret: Boolean,
    onToggleExpanded: () -> Unit,
    onSecretChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpanded() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.advanced_settings),
                style = TDTheme.typography.heading3,
                color = TDTheme.colors.onBackground.copy(alpha = 0.7f)
            )
            Icon(
                painter = painterResource(
                    if (isExpanded) {
                        R.drawable.ic_outline_expand_circle_down_24
                    } else {
                        R.drawable.ic_outline_expand_circle_right_24
                    }
                ),
                contentDescription = null,
                tint = TDTheme.colors.onBackground.copy(alpha = 0.7f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSecret,
                    onCheckedChange = { onSecretChange(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = TDTheme.colors.primary,
                        uncheckedColor = TDTheme.colors.onBackground.copy(alpha = 0.6f)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                TDText(
                    text = stringResource(com.todoapp.mobile.R.string.secret_task),
                    style = TDTheme.typography.heading6,
                    color = TDTheme.colors.onBackground
                )
            }
        }
    }
}

@Composable
private fun AddTaskSheet(
    uiState: UiState,
    onClick: () -> Unit,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TDText(text = stringResource(com.todoapp.mobile.R.string.add_new_task), color = TDTheme.colors.onBackground)
            IconButton(
                onClick = { onAction(UiAction.OnDismissBottomSheet) },
            ) {
                Icon(
                    painterResource(R.drawable.ic_close),
                    tint = TDTheme.colors.onBackground,
                    contentDescription = stringResource(com.todoapp.mobile.R.string.close_button),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        TDCompactOutlinedTextField(
            label = stringResource(com.todoapp.mobile.R.string.task_title),
            value = uiState.taskTitle,
            onValueChange = { onAction(UiAction.OnTaskTitleChange(it)) },
            isError = uiState.isTitleError
        )
        Spacer(Modifier.height(12.dp))
        TDDatePickerDialog(
            selectedDate = uiState.dialogSelectedDate,
            onDateSelect = { onAction(UiAction.OnDialogDateSelect(it)) },
            onDateDeselect = { onAction(UiAction.OnDialogDateDeselect) },
            isError = uiState.isDateError
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            TDTimePickerDialog(
                modifier = Modifier.weight(1f),
                title = stringResource(com.todoapp.mobile.R.string.set_time),
                placeholder = stringResource(com.todoapp.mobile.R.string.starts),
                selectedTime = uiState.taskTimeStart,
                onTimeChange = { onAction(UiAction.OnTaskTimeStartChange(it)) },
                isError = uiState.isTimeError
            )
            Spacer(Modifier.width(12.dp))
            TDTimePickerDialog(
                modifier = Modifier.weight(1f),
                title = "",
                placeholder = stringResource(com.todoapp.mobile.R.string.ends),
                selectedTime = uiState.taskTimeEnd,
                onTimeChange = { onAction(UiAction.OnTaskTimeEndChange(it)) },
                isError = uiState.isTimeError
            )
        }
        Spacer(Modifier.height(12.dp))
        TDCompactOutlinedTextField(
            label = stringResource(com.todoapp.mobile.R.string.description),
            value = uiState.taskDescription,
            onValueChange = { onAction(UiAction.OnTaskDescriptionChange(it)) },
            singleLine = false,
        )
        Spacer(Modifier.height(12.dp))

        AdvancedSettings(
            isExpanded = uiState.isAdvancedSettingsExpanded,
            isSecret = uiState.isTaskSecret,
            onToggleExpanded = { onAction(UiAction.OnToggleAdvancedSettings) },
            onSecretChange = { onAction(UiAction.OnTaskSecretChange(it)) }
        )

        Spacer(Modifier.height(12.dp))

        TDButton(
            text = stringResource(com.todoapp.mobile.R.string.create_task),
            onClick = onClick,
            size = TDButtonSize.SMALL,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private suspend fun handleBiometricAuthentication(
    context: Context,
    onSuccess: () -> Unit,
) {
    val activity = context as? FragmentActivity ?: return

    val isAuthenticated = BiometricAuthenticator.authenticate(activity)
    if (isAuthenticated) onSuccess()
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    val fakeUiState =
        UiState(
            selectedDate = LocalDate.now(),
            tasks =
                listOf(
                    Task(
                        id = 1L,
                        title = "Design the main screen",
                        description = "Draft layout & components",
                        date = LocalDate.now(),
                        timeStart = LocalTime.of(9, 30),
                        timeEnd = LocalTime.of(10, 15),
                        isCompleted = false,
                        isSecret = false,

                        ),
                    Task(
                        id = 2L,
                        title = "Develop the API client",
                        description = "Retrofit + serialization setup",
                        date = LocalDate.now().minusDays(1),
                        timeStart = LocalTime.of(11, 0),
                        timeEnd = LocalTime.of(12, 0),
                        isCompleted = true,
                        isSecret = false

                    ),
                    Task(
                        id = 3L,
                        title = "Fix the login bug",
                        description = null,
                        date = LocalDate.now(),
                        timeStart = LocalTime.of(14, 0),
                        timeEnd = LocalTime.of(14, 30),
                        isCompleted = false,
                        isSecret = false

                    ),
                ),
            completedTaskCountThisWeek = 5,
            pendingTaskCountThisWeek = 8,
        )

    HomeContent(
        uiState = fakeUiState,
        onAction = {},
        modifier = Modifier.padding(start = 24.dp, end = 24.dp),
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun HomeContentPreview_Dark() {
    val fakeUiState =
        UiState(
            selectedDate = LocalDate.now(),
            tasks =
                listOf(
                    Task(
                        id = 1L,
                        title = "Design the main screen",
                        description = "Draft layout & components",
                        date = LocalDate.now(),
                        timeStart = LocalTime.of(9, 30),
                        timeEnd = LocalTime.of(10, 15),
                        isCompleted = false,
                        isSecret = false

                    ),
                    Task(
                        id = 2L,
                        title = "Develop the API client",
                        description = "Retrofit + serialization setup",
                        date = LocalDate.now().minusDays(1),
                        timeStart = LocalTime.of(11, 0),
                        timeEnd = LocalTime.of(12, 0),
                        isCompleted = true,
                        isSecret = false

                    ),
                    Task(
                        id = 3L,
                        title = "Fix the login bug",
                        description = null,
                        date = LocalDate.now(),
                        timeStart = LocalTime.of(14, 0),
                        timeEnd = LocalTime.of(14, 30),
                        isCompleted = false,
                        isSecret = false

                    ),
                ),
            completedTaskCountThisWeek = 5,
            pendingTaskCountThisWeek = 8,
        )

    TDTheme(darkTheme = true) {
        HomeContent(
            uiState = fakeUiState,
            onAction = {},
            modifier = Modifier.padding(start = 24.dp, end = 24.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun AddTaskSheetPreview() {
    AddTaskSheet(
        uiState =
            UiState(
                selectedDate = LocalDate.of(2025, 12, 25),
                taskTitle = "Read 10 pages",
                taskTimeEnd = LocalTime.of(10, 15),
                taskDescription = "Focus mode on. No phone.",
            ),
        onClick = {},
        onAction = {},
    )
}

@Preview(
    showBackground = true,
    widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun AddTaskSheetPreview_Dark() {
    TDTheme(darkTheme = true) {
        AddTaskSheet(
            uiState =
                UiState(
                    selectedDate = LocalDate.of(2025, 12, 25),
                    taskTitle = "Read 10 pages",
                    taskTimeEnd = LocalTime.of(10, 15),
                    taskDescription = "Focus mode on. No phone.",
                ),
            onClick = {},
            onAction = {},
        )
    }
}
