package com.todoapp.mobile.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiState
import com.todoapp.uikit.components.TDStatisticCard
import com.todoapp.uikit.components.TDTaskCardWithCheckbox
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDWeeklyDatePicker
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalDate

@Composable
fun HomeScreen(
    modifier: Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        TDWeeklyDatePicker(
            modifier = Modifier,
            selectedDate = uiState.selectedDate,
            onDateSelect = { onAction(UiAction.OnDateSelect(it)) },
        )
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            TDStatisticCard(
                text = "Task Complete",
                taskAmount = uiState.completedTaskCountThisWeek,
                modifier = Modifier.weight(1f),
                isCompleted = true,
            )
            Spacer(modifier = Modifier.size(20.dp))
            TDStatisticCard(
                text = "Task Pending",
                taskAmount = uiState.pendingTaskCountThisWeek,
                modifier = Modifier.weight(1f),
                isCompleted = false,
            )
        }
        Spacer(Modifier.height(32.dp))
        TDText(text = "Tasks Today", style = TDTheme.typography.heading3)
        Spacer(Modifier.height(16.dp))
        uiState.tasks.forEach { task ->
            TDTaskCardWithCheckbox(
                modifier = Modifier.height(60.dp),
                taskText = task.text,
                isChecked = task.isDone,
                onCheckBoxClick = { onAction(UiAction.OnTaskClick(task)) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val fakeUiState =
        UiState(
            selectedDate = LocalDate.now(),
            tasks =
                listOf(
                    Task("1", "Design the main screen", false, LocalDate.now()),
                    Task("2", "Develop the API client", true, LocalDate.now().minusDays(1)),
                    Task("3", "Fix the login bug", false, LocalDate.now()),
                ),
            completedTaskCountThisWeek = 5,
            pendingTaskCountThisWeek = 8,
        )
    HomeScreen(
        uiState = fakeUiState,
        onAction = {},
        modifier = Modifier.padding(start = 24.dp, end = 24.dp),
    )
}
