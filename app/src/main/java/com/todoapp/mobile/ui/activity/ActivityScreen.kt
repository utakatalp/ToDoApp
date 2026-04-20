package com.todoapp.mobile.ui.activity

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.uikit.R
import com.todoapp.mobile.ui.activity.ActivityContract.UiAction
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import com.todoapp.mobile.ui.home.AddTaskSheet
import com.todoapp.mobile.ui.home.HomeFabMenu
import com.todoapp.mobile.ui.home.TaskFormUiAction
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDGeneralProgressBar
import com.todoapp.uikit.components.TDLoadingBar
import com.todoapp.uikit.components.TDScreenWithSheet
import com.todoapp.uikit.components.TDStatisticCard
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDWeekNavigator
import com.todoapp.uikit.components.TDWeeklyBarChart
import com.todoapp.uikit.components.TDWeeklyCircularProgressIndicator
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalDate

@Composable
fun ActivityScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    when (uiState) {
        is UiState.Loading -> ActivityLoadingContent()
        is UiState.Error -> ActivityErrorContent(message = uiState.message, onAction = onAction)
        is UiState.Success -> ActivitySuccessContent(uiState = uiState, onAction = onAction)
    }
}

@Composable
private fun ActivityLoadingContent() {
    TDLoadingBar()
}

@Composable
private fun ActivityErrorContent(
    message: String,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = TDTheme.colors.crossRed,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(16.dp))
        TDText(
            text = message,
            style = TDTheme.typography.heading3,
            color = TDTheme.colors.onBackground,
        )
        Spacer(Modifier.height(24.dp))
        TDButton(
            text = stringResource(com.todoapp.mobile.R.string.retry),
            onClick = { onAction(UiAction.OnRetry) },
            size = TDButtonSize.SMALL,
        )
    }
}

@Composable
private fun ActivitySuccessContent(
    uiState: UiState.Success,
    onAction: (UiAction) -> Unit,
) {
    var showFullScreen by remember { mutableStateOf(false) }

    if (showFullScreen) {
        Dialog(
            onDismissRequest = { showFullScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(initialScale = 0.88f, animationSpec = tween(300)) + fadeIn(tween(300)),
                exit = scaleOut(animationSpec = tween(200)) + fadeOut(tween(200)),
            ) {
                Surface(
                    modifier =
                    Modifier
                        .fillMaxWidth(0.92f),
                    shape = RoundedCornerShape(20.dp),
                    color = TDTheme.colors.background,
                    shadowElevation = 8.dp,
                ) {
                    Column(
                        modifier =
                        Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        TDWeeklyBarChart(
                            modifier = Modifier.fillMaxWidth(),
                            title =
                            stringResource(
                                id = com.todoapp.mobile.R.string.activity_screen_bar_chart_component_title_text,
                            ),
                            values = uiState.weeklyBarValues,
                            pendingValues = uiState.weeklyPendingBarValues,
                            height = 220.dp,
                        )
                    }
                }
            }
        }
    }

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
                        is TaskFormUiAction.DescriptionChange ->
                            onAction(
                                UiAction.OnTaskDescriptionChange(action.description),
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
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .background(TDTheme.colors.background),
        ) {
            Column(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                TDWeekNavigator(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    selectedDate = uiState.selectedDate,
                    onPreviousWeek = { onAction(UiAction.OnWeekSelected(uiState.selectedDate.minusWeeks(1))) },
                    onNextWeek = { onAction(UiAction.OnWeekSelected(uiState.selectedDate.plusWeeks(1))) },
                )

                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TDText(
                        text = stringResource(id = com.todoapp.mobile.R.string.activity_screen_statistic_text),
                        style = TDTheme.typography.heading2,
                        color = TDTheme.colors.onBackground,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TDWeeklyCircularProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        strokeWidth = 16.dp,
                        strokeCap = Butt,
                        progress = uiState.weeklyProgress,
                        inProgress = uiState.weeklyPendingProgress,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TDStatisticCard(
                        modifier = Modifier.weight(1f),
                        text = stringResource(com.todoapp.mobile.R.string.home_screen_task_completed_text),
                        taskAmount = uiState.weeklyCompleted,
                        isCompleted = true,
                        onClick = { onAction(UiAction.OnCompletedStatCardTap) },
                    )
                    TDStatisticCard(
                        modifier = Modifier.weight(1f),
                        text = stringResource(com.todoapp.mobile.R.string.home_screen_task_pending_text),
                        taskAmount = uiState.weeklyPending,
                        isCompleted = false,
                        onClick = { onAction(UiAction.OnPendingStatCardTap) },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TDText(
                        text = stringResource(id = com.todoapp.mobile.R.string.activity_screen_activity_text),
                        style = TDTheme.typography.heading2,
                        color = TDTheme.colors.onBackground,
                    )
                    TDWeeklyBarChart(
                        modifier = Modifier.fillMaxWidth(),
                        title =
                        stringResource(
                            id = com.todoapp.mobile.R.string.activity_screen_bar_chart_component_title_text,
                        ),
                        values = uiState.weeklyBarValues,
                        pendingValues = uiState.weeklyPendingBarValues,
                        height = 220.dp,
                        scrollableHeight = 180.dp,
                        onExpandClick = { showFullScreen = true },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp, end = 16.dp, start = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TDText(
                        text = stringResource(com.todoapp.mobile.R.string.activity_screen_progress_text),
                        style = TDTheme.typography.heading2,
                        color = TDTheme.colors.onBackground,
                    )
                    TDGeneralProgressBar(
                        progress = uiState.yearlyProgress,
                        completedCount = uiState.yearlyCompleted,
                        totalCount = uiState.yearlyTotal,
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
private fun ActivityLoadingPreview() {
    TDTheme {
        ActivityLoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityErrorPreview() {
    TDTheme {
        ActivityErrorContent(
            message = "Something went wrong",
            onAction = {},
        )
    }
}

@Preview("Light", uiMode = AndroidUiModes.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun ActivityScreenPreview_Light() {
    TDTheme {
        ActivitySuccessContent(
            onAction = {},
            uiState =
            UiState.Success(
                weeklyCompleted = 24,
                weeklyPending = 10,
                weeklyProgress = 0.65f,
                weeklyPendingProgress = 0.35f,
                weeklyBarValues = listOf(7, 2, 3, 2, 5, 1, 4),
                yearlyProgress = 0.5f,
                selectedDate = LocalDate.now(),
                yearlyPendingProgress = 0f,
                yearlyCompleted = 42,
                yearlyTotal = 100,
                weeklyPendingBarValues = listOf(1, 1, 3, 1, 5, 2, 4),
            ),
        )
    }
}

@Preview("Night", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ActivityScreenPreview_Dark() {
    TDTheme {
        ActivitySuccessContent(
            onAction = {},
            uiState =
            UiState.Success(
                weeklyCompleted = 24,
                weeklyPending = 10,
                weeklyProgress = 0.65f,
                weeklyPendingProgress = 0.35f,
                weeklyBarValues = listOf(7, 2, 3, 2, 2, 1, 4),
                yearlyProgress = 0.5f,
                selectedDate = LocalDate.now(),
                yearlyPendingProgress = 0f,
                yearlyCompleted = 42,
                yearlyTotal = 100,
                weeklyPendingBarValues = listOf(1, 1, 3, 1, 5, 2, 4),
            ),
        )
    }
}
