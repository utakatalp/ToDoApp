package com.todoapp.mobile.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiEffect
import com.todoapp.mobile.ui.home.HomeContract.UiState
import com.todoapp.uikit.components.TDAddTaskButton
import com.todoapp.uikit.components.TDCompactOutlinedTextField
import com.todoapp.uikit.components.TDDatePickerDialog
import com.todoapp.uikit.components.TDScreenWithSheet
import com.todoapp.uikit.components.TDStatisticCard
import com.todoapp.uikit.components.TDTaskCardWithCheckbox
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDTimePickerDialog
import com.todoapp.uikit.components.TDWeeklyDatePicker
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HomeScreen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
) {
    TDScreenWithSheet(
        isSheetOpen = uiState.isSheetOpen,
        uiEffect = uiEffect,
        isLoading = false,
        sheetContent = {
            AddTaskSheet(
                uiState = uiState,
                onClick = { onAction(UiAction.OnDismissBottomSheet) },
                onAction = onAction,
            )
        },
        onDismissSheet = { onAction(UiAction.OnDismissBottomSheet) },
    ) {
        HomeContent(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color = TDTheme.colors.white)
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
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
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
        TDText(text = stringResource(com.todoapp.mobile.R.string.tasks_today), style = TDTheme.typography.heading3)
        Spacer(Modifier.height(16.dp))
        uiState.tasks.forEach { task ->
            TDTaskCardWithCheckbox(
                modifier = Modifier.height(60.dp),
                taskText = task.text,
                isChecked = task.isDone,
                onCheckBoxClick = { onAction(UiAction.OnTaskClick(task)) },
            )
        }
        Spacer(Modifier.weight(1f))
        TDAddTaskButton(
            modifier =
                Modifier
                    .size(96.dp)
                    .align(Alignment.End),
            onClick = {
                onAction(UiAction.OnShowBottomSheet)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
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
    HomeContent(
        uiState = fakeUiState,
        onAction = {},
        modifier = Modifier.padding(start = 24.dp, end = 24.dp),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddTaskSheet(
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
            TDText(text = stringResource(com.todoapp.mobile.R.string.add_new_task))
            IconButton(
                onClick = onClick,
            ) {
                Icon(
                    painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(com.todoapp.mobile.R.string.close_button),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        TDCompactOutlinedTextField(
            label = stringResource(com.todoapp.mobile.R.string.task_title),
            value = uiState.taskTitle,
            onValueChange = { onAction(UiAction.OnTaskTitleChange(it)) },
        )
        Spacer(Modifier.height(12.dp))
        TDDatePickerDialog(
            selectedDate = uiState.selectedDate,
            onDateSelect = { onAction(UiAction.OnDateSelect(it)) },
            onDateDeselect = { onAction(UiAction.OnDateDeselect) },
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            TDTimePickerDialog(
                modifier = Modifier.weight(1f),
                title = stringResource(com.todoapp.mobile.R.string.set_time),
                placeholder = stringResource(com.todoapp.mobile.R.string.starts),
                selectedTime = uiState.taskTimeStart,
                onTimeChange = { onAction(UiAction.OnTaskTimeStartChange(it)) },
            )
            Spacer(Modifier.width(12.dp))
            TDTimePickerDialog(
                modifier = Modifier.weight(1f),
                title = "",
                placeholder = stringResource(com.todoapp.mobile.R.string.ends),
                selectedTime = uiState.taskTimeEnd,
                onTimeChange = { onAction(UiAction.OnTaskTimeEndChange(it)) },
            )
        }
        Spacer(Modifier.height(12.dp))
        TDCompactOutlinedTextField(
            label = stringResource(com.todoapp.mobile.R.string.description),
            value = uiState.taskDescription,
            onValueChange = { onAction(UiAction.OnTaskDescriptionChange(it)) },
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onClick,
        ) {
            Text(stringResource(com.todoapp.mobile.R.string.create_task))
        }
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
