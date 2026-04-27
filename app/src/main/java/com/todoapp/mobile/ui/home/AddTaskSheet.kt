package com.todoapp.mobile.ui.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.domain.model.Recurrence
import com.todoapp.mobile.domain.model.TaskCategory
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDCategoryOption
import com.todoapp.uikit.components.TDCategoryPicker
import com.todoapp.uikit.components.TDCompactOutlinedTextField
import com.todoapp.uikit.components.TDDatePickerDialog
import com.todoapp.uikit.components.TDPickerField
import com.todoapp.uikit.components.TDRecurrenceOption
import com.todoapp.uikit.components.TDRecurrencePicker
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDWheelTimePickerDialog
import com.todoapp.uikit.theme.TDTheme
import java.time.format.DateTimeFormatter

@Composable
internal fun AddTaskSheet(
    formState: TaskFormState,
    onAction: (TaskFormUiAction) -> Unit,
    availableGroups: List<HomeContract.GroupSelectionItem> = emptyList(),
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.add_new_task),
                color = TDTheme.colors.onBackground,
            )
            IconButton(
                onClick = { onAction(TaskFormUiAction.Dismiss) },
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
            value = formState.taskTitle,
            onValueChange = { onAction(TaskFormUiAction.TitleChange(it)) },
            isError = formState.titleErrorRes != null,
            supportingText = formState.titleErrorRes?.let { stringResource(it) },
        )
        Spacer(Modifier.height(12.dp))
        TDText(
            text = stringResource(com.todoapp.mobile.R.string.category_label),
            style = TDTheme.typography.heading6,
            color = TDTheme.colors.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        TDCategoryPicker(
            selectedKey = formState.selectedCategory.name,
            options = categoryOptions(),
            onSelected = { key ->
                onAction(TaskFormUiAction.CategoryChange(TaskCategory.valueOf(key)))
            },
        )
        if (formState.selectedCategory == TaskCategory.OTHER) {
            Spacer(Modifier.height(8.dp))
            TDCompactOutlinedTextField(
                label = stringResource(com.todoapp.mobile.R.string.category_other_hint),
                value = formState.customCategoryName,
                onValueChange = { onAction(TaskFormUiAction.CustomCategoryNameChange(it)) },
            )
        }
        Spacer(Modifier.height(12.dp))
        TDText(
            text = stringResource(com.todoapp.mobile.R.string.recurrence_label),
            style = TDTheme.typography.heading6,
            color = TDTheme.colors.onBackground,
        )
        Spacer(Modifier.height(8.dp))
        TDRecurrencePicker(
            selectedKey = formState.selectedRecurrence.name,
            options = recurrenceOptions(),
            onSelected = { key ->
                onAction(TaskFormUiAction.RecurrenceChange(Recurrence.valueOf(key)))
            },
        )
        if (formState.selectedRecurrence != Recurrence.NONE) {
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TDTheme.colors.background, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_warning),
                    contentDescription = null,
                    tint = TDTheme.colors.orange,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                TDText(
                    text = recurrenceExplainer(formState.selectedRecurrence),
                    style = TDTheme.typography.subheading1,
                    color = TDTheme.colors.orange,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        TDDatePickerDialog(
            selectedDate = formState.dialogSelectedDate,
            onDateSelect = { onAction(TaskFormUiAction.DateSelect(it)) },
            onDateDeselect = { onAction(TaskFormUiAction.DateDeselect) },
            isError = formState.dateErrorRes != null,
            supportingText = formState.dateErrorRes?.let { stringResource(it) },
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                TDPickerField(
                    title = stringResource(com.todoapp.mobile.R.string.set_time),
                    value =
                    formState.taskTimeStart?.format(timeFormatter)
                        ?: stringResource(com.todoapp.mobile.R.string.starts),
                    onClick = { showStartTimePicker = true },
                    isError = formState.timeErrorRes != null,
                    supportingText = formState.timeErrorRes?.let { stringResource(it) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
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
                    value =
                    formState.taskTimeEnd?.format(timeFormatter)
                        ?: stringResource(com.todoapp.mobile.R.string.ends),
                    onClick = { showEndTimePicker = true },
                    isError = formState.timeErrorRes != null,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            tint = TDTheme.colors.onBackground,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        TDCompactOutlinedTextField(
            label = stringResource(com.todoapp.mobile.R.string.description),
            value = formState.taskDescription,
            onValueChange = { onAction(TaskFormUiAction.DescriptionChange(it)) },
            singleLine = false,
        )
        Spacer(Modifier.height(12.dp))
        PendingPhotosRow(
            pending = formState.pendingPhotos,
            onPick = { bytes, mime -> onAction(TaskFormUiAction.PhotoPicked(bytes, mime)) },
            onRemoveAt = { idx -> onAction(TaskFormUiAction.PhotoRemoveAt(idx)) },
        )
        Spacer(Modifier.height(12.dp))
        AdvancedSettings(
            isExpanded = formState.isAdvancedSettingsExpanded,
            isSecret = formState.isTaskSecret,
            reminderOffsetMinutes = formState.reminderOffsetMinutes,
            availableGroups = availableGroups,
            selectedGroupId = formState.selectedGroupId,
            onToggleExpanded = { onAction(TaskFormUiAction.ToggleAdvancedSettings) },
            onSecretChange = { onAction(TaskFormUiAction.SecretChange(it)) },
            onReminderOffsetChange = { onAction(TaskFormUiAction.ReminderOffsetChange(it)) },
            onGroupSelected = { groupId ->
                onAction(
                    TaskFormUiAction.GroupSelectionChanged(
                        if (formState.selectedGroupId == groupId) null else groupId,
                    ),
                )
            },
        )
        Spacer(Modifier.height(12.dp))
        TDButton(
            text = stringResource(com.todoapp.mobile.R.string.create_task),
            onClick = { onAction(TaskFormUiAction.Create) },
            size = TDButtonSize.SMALL,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (showStartTimePicker) {
        TDWheelTimePickerDialog(
            initialTime = formState.taskTimeStart,
            onConfirm = {
                onAction(TaskFormUiAction.TimeStartChange(it))
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false },
        )
    }

    if (showEndTimePicker) {
        TDWheelTimePickerDialog(
            initialTime = formState.taskTimeEnd,
            onConfirm = {
                onAction(TaskFormUiAction.TimeEndChange(it))
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false },
        )
    }
}

@Composable
private fun GroupAssignmentSection(
    availableGroups: List<HomeContract.GroupSelectionItem>,
    selectedGroupId: Long?,
    onGroupSelected: (Long) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TDText(
            text = stringResource(com.todoapp.mobile.R.string.assign_to_a_group),
            style = TDTheme.typography.subheading2,
            color = TDTheme.colors.gray,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        availableGroups.forEach { group ->
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onGroupSelected(group.groupId) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(com.todoapp.mobile.R.drawable.ic_groups),
                    contentDescription = null,
                    tint = TDTheme.colors.pendingGray,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(12.dp))
                TDText(
                    text = group.name,
                    style = TDTheme.typography.subheading3,
                    color = TDTheme.colors.onBackground,
                    modifier = Modifier.weight(1f),
                )
                Checkbox(
                    checked = selectedGroupId == group.groupId,
                    onCheckedChange = { onGroupSelected(group.groupId) },
                    colors =
                    CheckboxDefaults.colors(
                        checkedColor = TDTheme.colors.pendingGray,
                        uncheckedColor = TDTheme.colors.onBackground.copy(alpha = 0.5f),
                    ),
                )
            }
        }
    }
}

@Composable
private fun AdvancedSettings(
    isExpanded: Boolean,
    isSecret: Boolean,
    reminderOffsetMinutes: Long?,
    availableGroups: List<HomeContract.GroupSelectionItem>,
    selectedGroupId: Long?,
    onToggleExpanded: () -> Unit,
    onSecretChange: (Boolean) -> Unit,
    onReminderOffsetChange: (Long?) -> Unit,
    onGroupSelected: (Long) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onToggleExpanded() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.advanced_settings),
                style = TDTheme.typography.heading3,
                color = TDTheme.colors.onBackground.copy(alpha = 0.7f),
            )
            Icon(
                painter =
                painterResource(
                    if (isExpanded) {
                        R.drawable.ic_outline_expand_circle_down_24
                    } else {
                        R.drawable.ic_outline_expand_circle_right_24
                    },
                ),
                contentDescription = null,
                tint = TDTheme.colors.onBackground.copy(alpha = 0.7f),
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = isSecret,
                        onCheckedChange = { onSecretChange(it) },
                        colors =
                        CheckboxDefaults.colors(
                            checkedColor = TDTheme.colors.pendingGray,
                            uncheckedColor = TDTheme.colors.onBackground.copy(alpha = 0.6f),
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TDText(
                        text = stringResource(com.todoapp.mobile.R.string.secret_task),
                        style = TDTheme.typography.heading6,
                        color = TDTheme.colors.onBackground,
                    )
                }
                Spacer(Modifier.height(12.dp))
                TDText(
                    text = stringResource(com.todoapp.mobile.R.string.reminder_label),
                    style = TDTheme.typography.heading6,
                    color = TDTheme.colors.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                com.todoapp.uikit.components.TDReminderOffsetPicker(
                    selectedMinutes = reminderOffsetMinutes,
                    options = reminderOffsetOptions(),
                    onSelected = onReminderOffsetChange,
                )
                if (availableGroups.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    GroupAssignmentSection(
                        availableGroups = availableGroups,
                        selectedGroupId = selectedGroupId,
                        onGroupSelected = onGroupSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun categoryOptions(): List<TDCategoryOption> = listOf(
    TDCategoryOption(
        key = TaskCategory.SHOPPING.name,
        label = stringResource(com.todoapp.mobile.R.string.category_shopping),
        iconRes = R.drawable.ic_shopping_label,
    ),
    TDCategoryOption(
        key = TaskCategory.MEDICINE.name,
        label = stringResource(com.todoapp.mobile.R.string.category_medicine),
        iconRes = R.drawable.ic_medication_label,
    ),
    TDCategoryOption(
        key = TaskCategory.HEALTH.name,
        label = stringResource(com.todoapp.mobile.R.string.category_health),
        iconRes = R.drawable.ic_health_label,
    ),
    TDCategoryOption(
        key = TaskCategory.WORK.name,
        label = stringResource(com.todoapp.mobile.R.string.category_work),
        iconRes = R.drawable.ic_work_label,
    ),
    TDCategoryOption(
        key = TaskCategory.STUDY.name,
        label = stringResource(com.todoapp.mobile.R.string.category_study),
        iconRes = R.drawable.ic_study_label,
    ),
    TDCategoryOption(
        key = TaskCategory.BIRTHDAY.name,
        label = stringResource(com.todoapp.mobile.R.string.category_birthday),
        iconRes = R.drawable.ic_birthday_label,
    ),
    TDCategoryOption(
        key = TaskCategory.PERSONAL.name,
        label = stringResource(com.todoapp.mobile.R.string.category_personal),
        iconRes = R.drawable.ic_personal_label,
    ),
    TDCategoryOption(
        key = TaskCategory.OTHER.name,
        label = stringResource(com.todoapp.mobile.R.string.category_other),
        iconRes = R.drawable.ic_label_label,
    ),
)

@Composable
private fun recurrenceOptions(): List<TDRecurrenceOption> = listOf(
    TDRecurrenceOption(Recurrence.NONE.name, stringResource(com.todoapp.mobile.R.string.recurrence_none)),
    TDRecurrenceOption(Recurrence.DAILY.name, stringResource(com.todoapp.mobile.R.string.recurrence_daily)),
    TDRecurrenceOption(Recurrence.WEEKLY.name, stringResource(com.todoapp.mobile.R.string.recurrence_weekly)),
    TDRecurrenceOption(Recurrence.MONTHLY.name, stringResource(com.todoapp.mobile.R.string.recurrence_monthly)),
    TDRecurrenceOption(Recurrence.YEARLY.name, stringResource(com.todoapp.mobile.R.string.recurrence_yearly)),
)

@Composable
private fun recurrenceExplainer(recurrence: Recurrence): String = when (recurrence) {
    Recurrence.NONE -> ""
    Recurrence.DAILY -> stringResource(com.todoapp.mobile.R.string.recurrence_explainer_daily)
    Recurrence.WEEKLY -> stringResource(com.todoapp.mobile.R.string.recurrence_explainer_weekly)
    Recurrence.MONTHLY -> stringResource(com.todoapp.mobile.R.string.recurrence_explainer_monthly)
    Recurrence.YEARLY -> stringResource(com.todoapp.mobile.R.string.recurrence_explainer_yearly)
}

@Composable
private fun reminderOffsetOptions(): List<com.todoapp.uikit.components.TDReminderOption> = listOf(
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_none),
        minutes = null,
    ),
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_on_time),
        minutes = 0L,
    ),
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_1_min),
        minutes = 1L,
    ),
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_30_min),
        minutes = 30L,
    ),
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_1_hour),
        minutes = 60L,
    ),
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_3_hours),
        minutes = 180L,
    ),
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_6_hours),
        minutes = 360L,
    ),
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_12_hours),
        minutes = 720L,
    ),
    com.todoapp.uikit.components.TDReminderOption(
        label = stringResource(com.todoapp.mobile.R.string.reminder_1_day),
        minutes = 1440L,
    ),
)

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun AddTaskSheetPreview() {
    TDTheme {
        AddTaskSheet(
            formState = TaskFormState(),
            onAction = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AddTaskSheetPreview_Dark() {
    TDTheme(darkTheme = true) {
        AddTaskSheet(
            formState = TaskFormState(),
            onAction = {},
        )
    }
}
