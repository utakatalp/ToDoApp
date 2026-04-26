@file:Suppress("MagicNumber", "LongParameterList")

package com.todoapp.mobile.ui.home

import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.ui.home.HomeContract.UiState
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

object HomePreviewData {
    fun successState(
        selectedDate: LocalDate = LocalDate.now(),
        displayedMonth: YearMonth = YearMonth.now(),
        tasks: List<Task> = emptyList(),
        completedTaskCountThisWeek: Int = 0,
        pendingTaskCountThisWeek: Int = 0,
        isSheetOpen: Boolean = false,
        isDeleteDialogOpen: Boolean = false,
        isSecretModeEnabled: Boolean = true,
        taskTitle: String = "",
        dialogSelectedDate: LocalDate? = null,
        taskTimeStart: LocalTime? = null,
        taskTimeEnd: LocalTime? = null,
        taskDescription: String = "",
        isAdvancedSettingsExpanded: Boolean = false,
        isTaskSecret: Boolean = false,
        titleErrorRes: Int? = null,
        timeErrorRes: Int? = null,
        dateErrorRes: Int? = null,
    ) = UiState.Success(
        selectedDate = selectedDate,
        displayedMonth = displayedMonth,
        tasks = tasks,
        completedTaskCountThisWeek = completedTaskCountThisWeek,
        pendingTaskCountThisWeek = pendingTaskCountThisWeek,
        isSheetOpen = isSheetOpen,
        isDeleteDialogOpen = isDeleteDialogOpen,
        isSecretModeEnabled = isSecretModeEnabled,
        taskFormState =
        TaskFormState(
            taskTitle = taskTitle,
            dialogSelectedDate = dialogSelectedDate,
            taskTimeStart = taskTimeStart,
            taskTimeEnd = taskTimeEnd,
            taskDescription = taskDescription,
            isAdvancedSettingsExpanded = isAdvancedSettingsExpanded,
            isTaskSecret = isTaskSecret,
            titleErrorRes = titleErrorRes,
            timeErrorRes = timeErrorRes,
            dateErrorRes = dateErrorRes,
        ),
    )

    val sampleTasks =
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
                isSecret = false,
            ),
            Task(
                id = 3L,
                title = "Fix the login bug",
                description = null,
                date = LocalDate.now(),
                timeStart = LocalTime.of(14, 0),
                timeEnd = LocalTime.of(14, 30),
                isCompleted = false,
                isSecret = false,
            ),
        )
}
