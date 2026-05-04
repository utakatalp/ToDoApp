package com.todoapp.mobile.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.uikit.R
import com.todoapp.mobile.domain.model.Recurrence
import com.todoapp.mobile.domain.model.TaskCategory
import com.todoapp.uikit.components.TDCategoryOption
import com.todoapp.uikit.components.TDRecurrenceOption
import com.todoapp.uikit.components.TDReminderOption

@Composable
internal fun categoryOptions(): List<TDCategoryOption> = listOf(
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
internal fun recurrenceOptions(): List<TDRecurrenceOption> = listOf(
    TDRecurrenceOption(Recurrence.NONE.name, stringResource(com.todoapp.mobile.R.string.recurrence_none)),
    TDRecurrenceOption(Recurrence.DAILY.name, stringResource(com.todoapp.mobile.R.string.recurrence_daily)),
    TDRecurrenceOption(Recurrence.WEEKLY.name, stringResource(com.todoapp.mobile.R.string.recurrence_weekly)),
    TDRecurrenceOption(Recurrence.MONTHLY.name, stringResource(com.todoapp.mobile.R.string.recurrence_monthly)),
    TDRecurrenceOption(Recurrence.YEARLY.name, stringResource(com.todoapp.mobile.R.string.recurrence_yearly)),
)

@Composable
internal fun recurrenceExplainer(recurrence: Recurrence): String = when (recurrence) {
    Recurrence.NONE -> ""
    Recurrence.DAILY -> stringResource(com.todoapp.mobile.R.string.recurrence_explainer_daily)
    Recurrence.WEEKLY -> stringResource(com.todoapp.mobile.R.string.recurrence_explainer_weekly)
    Recurrence.MONTHLY -> stringResource(com.todoapp.mobile.R.string.recurrence_explainer_monthly)
    Recurrence.YEARLY -> stringResource(com.todoapp.mobile.R.string.recurrence_explainer_yearly)
}

@Composable
internal fun reminderOffsetOptions(): List<TDReminderOption> = listOf(
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_none), minutes = null),
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_on_time), minutes = 0L),
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_1_min), minutes = 1L),
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_30_min), minutes = 30L),
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_1_hour), minutes = 60L),
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_3_hours), minutes = 180L),
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_6_hours), minutes = 360L),
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_12_hours), minutes = 720L),
    TDReminderOption(label = stringResource(com.todoapp.mobile.R.string.reminder_1_day), minutes = 1440L),
)
