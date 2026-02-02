package com.todoapp.mobile.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.ui.calendar.CalendarContract.UiAction
import com.todoapp.mobile.ui.calendar.CalendarContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDDatePicker
import com.todoapp.uikit.components.TDLoadingBar
import com.todoapp.uikit.components.TDTaskCardList
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TaskCardItem
import com.todoapp.uikit.components.TaskDayItem
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalDate

@Composable
fun CalendarScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    when (uiState) {
        is UiState.Loading -> {
            CalendarLoadingContent()
        }
        is UiState.Error -> {
            CalendarErrorContent(
                message = uiState.message,
                onAction = onAction
            )
        }
        is UiState.Success -> {
            CalendarSuccessContent(
                uiState = uiState,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun CalendarLoadingContent() {
    TDLoadingBar()
}

@Composable
private fun CalendarErrorContent(
    message: String,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = TDTheme.colors.crossRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        TDText(
            text = message,
            style = TDTheme.typography.heading3,
            color = TDTheme.colors.onBackground
        )
        Spacer(Modifier.height(24.dp))
        TDButton(
            text = stringResource(com.todoapp.mobile.R.string.retry),
            onClick = { onAction(UiAction.OnRetry) },
            size = TDButtonSize.SMALL
        )
    }
}

@Composable
private fun CalendarSuccessContent(
    uiState: UiState.Success,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = TDTheme.colors.background)
    ) {
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

@Preview(showBackground = true)
@Composable
private fun CalendarLoadingPreview() {
    TDTheme {
        CalendarLoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarErrorPreview() {
    TDTheme {
        CalendarErrorContent(
            message = "Something went wrong",
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun CalendarSuccessPreview() {
    TDTheme {
        CalendarSuccessContent(
            uiState = UiState.Success(
                selectedFirstDate = LocalDate.of(2025, 1, 12),
                selectedSecondDate = LocalDate.of(2025, 1, 14),
                taskDayItems = listOf(
                    TaskDayItem(
                        date = LocalDate.of(2025, 1, 12),
                        tasks = listOf(
                            TaskCardItem("Read Book", "09:30", "10:15"),
                            TaskCardItem("Gym", "18:00", "19:00"),
                        ),
                    ),
                    TaskDayItem(
                        date = LocalDate.of(2025, 1, 13),
                        tasks = listOf(TaskCardItem("Study Kotlin", "10:00", "12:00")),
                    ),
                    TaskDayItem(
                        date = LocalDate.of(2025, 1, 14),
                        tasks = listOf(TaskCardItem("Project Review", "16:30", "17:30")),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
