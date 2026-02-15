@file:Suppress("MagicNumber", "LongParameterList")

package com.todoapp.mobile.ui.home

import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.ui.home.HomeContract.UiState
import java.time.LocalDate
import java.time.LocalTime

object HomePreviewData {

    fun successState(
        selectedDate: LocalDate = LocalDate.now(),
        tasks: List<Task> = emptyList(),
        completedTaskCountThisWeek: Int = 0,
        pendingTaskCountThisWeek: Int = 0,
        dialogSelectedDate: LocalDate? = null,
        taskTitle: String = "",
        taskTimeStart: LocalTime? = null,
        taskTimeEnd: LocalTime? = null,
        taskDate: LocalDate = LocalDate.now(),
        taskDescription: String = "",
        isSheetOpen: Boolean = false,
        isDeleteDialogOpen: Boolean = false,
        isAdvancedSettingsExpanded: Boolean = false,
        isTaskSecret: Boolean = false,
        isSecretModeEnabled: Boolean = true,
        isTitleError: Boolean = false,
        isTimeError: Boolean = false,
        isDateError: Boolean = false,
    ) = UiState.Success(
        selectedDate = selectedDate,
        tasks = tasks,
        completedTaskCountThisWeek = completedTaskCountThisWeek,
        pendingTaskCountThisWeek = pendingTaskCountThisWeek,
        dialogSelectedDate = dialogSelectedDate,
        taskTitle = taskTitle,
        taskTimeStart = taskTimeStart,
        taskTimeEnd = taskTimeEnd,
        taskDate = taskDate,
        taskDescription = taskDescription,
        isSheetOpen = isSheetOpen,
        isDeleteDialogOpen = isDeleteDialogOpen,
        isAdvancedSettingsExpanded = isAdvancedSettingsExpanded,
        isTaskSecret = isTaskSecret,
        isSecretModeEnabled = isSecretModeEnabled,
        isTitleError = isTitleError,
        isTimeError = isTimeError,
        isDateError = isDateError,
        isUserAuthenticated = false
    )

    val sampleTasks = listOf(
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
    )
}
