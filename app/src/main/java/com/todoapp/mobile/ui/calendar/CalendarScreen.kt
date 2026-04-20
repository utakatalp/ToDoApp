package com.todoapp.mobile.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.uikit.R
import com.todoapp.mobile.ui.calendar.CalendarContract.UiAction
import com.todoapp.mobile.ui.calendar.CalendarContract.UiEffect
import com.todoapp.mobile.ui.calendar.CalendarContract.UiState
import com.todoapp.mobile.ui.home.AddTaskSheet
import com.todoapp.mobile.ui.home.HomeFabMenu
import com.todoapp.mobile.ui.home.TaskFormUiAction
import com.todoapp.mobile.ui.security.biometric.BiometricAuthenticator
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDDatePicker
import com.todoapp.uikit.components.TDLoadingBar
import com.todoapp.uikit.components.TDScreenWithSheet
import com.todoapp.uikit.components.TDTaskCardListByDay
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TaskCardItem
import com.todoapp.uikit.components.TaskDayItem
import com.todoapp.uikit.extensions.collectWithLifecycle
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Composable
fun CalendarScreen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
) {
    val context = LocalContext.current

    uiEffect.collectWithLifecycle {
        when (it) {
            is UiEffect.ShowBiometricAuthenticator -> {
                val activity = context as? FragmentActivity ?: return@collectWithLifecycle
                val isAuthenticated = BiometricAuthenticator.authenticate(activity)
                if (isAuthenticated) onAction(UiAction.OnSuccessfulBiometricAuthenticationHandle)
            }
        }
    }

    when (uiState) {
        is UiState.Loading -> CalendarLoadingContent()
        is UiState.Error -> CalendarErrorContent(message = uiState.message, onAction = onAction)
        is UiState.Success -> CalendarSuccessContent(uiState = uiState, onAction = onAction)
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
    TDScreenWithSheet(
        isSheetOpen = uiState.isSheetOpen,
        sheetContent = {
            AddTaskSheet(
                formState = uiState.taskFormState,
                onAction = { action ->
                    when (action) {
                        is TaskFormUiAction.Dismiss -> onAction(UiAction.OnDismissBottomSheet)
                        is TaskFormUiAction.Create -> onAction(UiAction.OnTaskCreate)
                        is TaskFormUiAction.TitleChange -> onAction(UiAction.OnTaskTitleChange(action.title))
                        is TaskFormUiAction.DateSelect -> onAction(UiAction.OnDialogDateSelect(action.date))
                        is TaskFormUiAction.DateDeselect -> onAction(UiAction.OnDialogDateDeselect)
                        is TaskFormUiAction.TimeStartChange -> onAction(UiAction.OnTaskTimeStartChange(action.time))
                        is TaskFormUiAction.TimeEndChange -> onAction(UiAction.OnTaskTimeEndChange(action.time))
                        is TaskFormUiAction.DescriptionChange -> onAction(
                            UiAction.OnTaskDescriptionChange(action.description)
                        )
                        is TaskFormUiAction.ToggleAdvancedSettings -> onAction(UiAction.OnToggleAdvancedSettings)
                        is TaskFormUiAction.SecretChange -> onAction(UiAction.OnTaskSecretChange(action.isSecret))
                        else -> Unit
                    }
                },
            )
        },
        onDismissSheet = { onAction(UiAction.OnDismissBottomSheet) },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = TDTheme.colors.background),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    TDDatePicker(
                        selectedDate = uiState.selectedDate,
                        selectedMonth = uiState.selectedMonth,
                        onMonthForward = { onAction(UiAction.OnMonthForward) },
                        onMonthBack = { onAction(UiAction.OnMonthBack) },
                        taskDates = uiState.taskDatesInMonth,
                        onDaySelect = { onAction(UiAction.OnDateSelect(it)) },
                        onDayDeselect = { onAction(UiAction.OnDateDeselect) },
                    )
                }
                items(
                    items = uiState.taskDayItems,
                    key = { it.date },
                ) { item ->
                    TDTaskCardListByDay(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        date = item.date,
                        tasks = item.tasks,
                        onTaskClick = { onAction(UiAction.OnTaskClick(it)) },
                    )
                }
            }
            HomeFabMenu(
                onAddTask = { onAction(UiAction.OnShowBottomSheet) },
                onPomodoro = { onAction(UiAction.OnPomodoroTap) },
            )
        }
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
                selectedDate = LocalDate.of(2025, 1, 12),
                taskDayItems = listOf(
                    TaskDayItem(
                        date = LocalDate.of(2025, 1, 12),
                        tasks = listOf(
                            TaskCardItem(
                                1L,
                                "Read Book",
                                "09:30",
                                "10:15",
                                isCompleted = true,
                                description = "Chapter 5 of Clean Code"
                            ),
                            TaskCardItem(2L, "Gym", "18:00", "19:00", isCompleted = false),
                        ),
                    ),
                    TaskDayItem(
                        date = LocalDate.of(2025, 1, 13),
                        tasks = listOf(
                            TaskCardItem(
                                3L,
                                "Study Kotlin",
                                "10:00",
                                "12:00",
                                isCompleted = false,
                                description = "Coroutines deep dive"
                            )
                        ),
                    ),
                    TaskDayItem(
                        date = LocalDate.of(2025, 1, 14),
                        tasks = listOf(TaskCardItem(4L, "Project Review", "16:30", "17:30", isCompleted = true)),
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
