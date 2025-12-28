package com.todoapp.mobile.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.ui.calendar.CalendarContract.UiAction
import com.todoapp.mobile.ui.calendar.CalendarContract.UiState
import com.todoapp.uikit.components.TDDatePicker
import com.todoapp.uikit.components.TDTaskCardList
import com.todoapp.uikit.components.TaskCardItem
import com.todoapp.uikit.components.TaskDayItem
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalDate

@Composable
fun CalendarScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    CalendarContent(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = TDTheme.colors.background),
        uiState = uiState,
        onAction = onAction,
    )
}

@Composable
fun CalendarContent(
    modifier: Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    Column(modifier = modifier) {
        TDDatePicker(
            selectedFirstDate = uiState.selectedFirstDate,
            selectedSecondDate = uiState.selectedSecondDate,
            onFirstDaySelect = { onAction(UiAction.OnFirstDateSelect(it)) },
            onSecondDaySelect = { onAction(UiAction.OnSecondDateSelect(it)) },
            onFirstDayDeselect = { onAction(UiAction.OnFirstDateDeselect) },
            onSecondDayDeselect = { onAction(UiAction.OnSecondDateDeselect) },
        )
        Spacer(Modifier.height(48.dp))
        TDTaskCardList(
            modifier = Modifier.padding(horizontal = 24.dp),
            tasks = uiState.taskDayItems,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun CalendarContentPreview() {
    TDTheme {
        CalendarContent(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(TDTheme.colors.background),
            uiState =
                UiState(
                    selectedFirstDate = LocalDate.of(2025, 1, 12),
                    selectedSecondDate = LocalDate.of(2025, 1, 14),
                    taskDayItems =
                        listOf(
                            TaskDayItem(
                                date = LocalDate.of(2025, 1, 12),
                                tasks =
                                    listOf(
                                        TaskCardItem("Read Book", "09:30", "10:15"),
                                        TaskCardItem("Gym", "18:00", "19:00"),
                                    ),
                            ),
                            TaskDayItem(
                                date = LocalDate.of(2025, 1, 13),
                                tasks =
                                    listOf(
                                        TaskCardItem("Study Kotlin", "10:00", "12:00"),
                                    ),
                            ),
                            TaskDayItem(
                                date = LocalDate.of(2025, 1, 14),
                                tasks =
                                    listOf(
                                        TaskCardItem("Project Review", "16:30", "17:30"),
                                    ),
                            ),
                        ),
                ),
            onAction = {},
        )
    }
}
