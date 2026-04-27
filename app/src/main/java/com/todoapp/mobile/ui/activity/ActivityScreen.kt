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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.uikit.R
import com.todoapp.mobile.domain.model.TaskCategory
import com.todoapp.mobile.ui.activity.ActivityContract.BestDay
import com.todoapp.mobile.ui.activity.ActivityContract.CategoryStat
import com.todoapp.mobile.ui.activity.ActivityContract.TrendDirection
import com.todoapp.mobile.ui.activity.ActivityContract.UiAction
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import com.todoapp.mobile.ui.activity.ActivityContract.WeekTrend
import com.todoapp.mobile.ui.home.AddTaskSheet
import com.todoapp.mobile.ui.home.HomeFabMenu
import com.todoapp.mobile.ui.home.TaskFormUiAction
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDGeneralProgressBar
import com.todoapp.uikit.components.TDLoadingBar
import com.todoapp.uikit.components.TDScreenWithSheet
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDWeekNavigator
import com.todoapp.uikit.components.TDWeeklyBarChart
import com.todoapp.uikit.theme.TDTheme
import java.time.DayOfWeek
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

                IncludeRecurringRow(
                    checked = uiState.includeRecurring,
                    onCheckedChange = { onAction(UiAction.OnToggleIncludeRecurring(it)) },
                )

                Spacer(modifier = Modifier.height(8.dp))

                ActivityCard {
                    WeekSummaryHeader(
                        weeklyCompleted = uiState.weeklyCompleted,
                        weeklyPending = uiState.weeklyPending,
                        weekTrend = uiState.weekTrend,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TDWeeklyBarChart(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(
                            id = com.todoapp.mobile.R.string.activity_screen_bar_chart_component_title_text,
                        ),
                        values = uiState.weeklyBarValues,
                        pendingValues = uiState.weeklyPendingBarValues,
                        height = 160.dp,
                        autoScaleHeightToMaxY = false,
                        onExpandClick = { showFullScreen = true },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ActivityCard {
                    StreakAndBestDay(
                        streakDays = uiState.streakDays,
                        bestDay = uiState.bestDay,
                    )
                }

                if (uiState.categoryBreakdown.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    ActivityCard {
                        CategoryBreakdownSection(stats = uiState.categoryBreakdown)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ActivityCard {
                    YearlyProgressSection(
                        yearlyCompleted = uiState.yearlyCompleted,
                        yearlyTotal = uiState.yearlyTotal,
                        yearlyProgress = uiState.yearlyProgress,
                    )
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
            HomeFabMenu(
                onAddTask = { onAction(UiAction.OnShowBottomSheet) },
                onPomodoro = { onAction(UiAction.OnPomodoroTap) },
            )
        }
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun ActivityLoadingPreview() {
    TDTheme {
        ActivityLoadingContent()
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun ActivityErrorPreview() {
    TDTheme {
        ActivityErrorContent(
            message = "Something went wrong",
            onAction = {},
        )
    }
}

@Composable
private fun IncludeRecurringRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TDText(
            modifier = Modifier.weight(1f),
            text = stringResource(com.todoapp.mobile.R.string.activity_include_recurring),
            style = TDTheme.typography.subheading1,
            color = TDTheme.colors.onBackground,
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TDTheme.colors.white,
                checkedTrackColor = TDTheme.colors.pendingGray,
            ),
        )
    }
}

@Composable
private fun ActivityCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = TDTheme.colors.lightPending,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
        content = content,
    )
}

@Composable
private fun YearlyProgressSection(
    yearlyCompleted: Int,
    yearlyTotal: Int,
    yearlyProgress: Float,
) {
    TDText(
        text = stringResource(com.todoapp.mobile.R.string.activity_screen_progress_text),
        style = TDTheme.typography.heading5,
        color = TDTheme.colors.onBackground,
    )
    Spacer(modifier = Modifier.height(8.dp))
    TDGeneralProgressBar(
        progress = yearlyProgress,
        completedCount = yearlyCompleted,
        totalCount = yearlyTotal,
    )
}

@Composable
private fun WeekSummaryHeader(
    weeklyCompleted: Int,
    weeklyPending: Int,
    weekTrend: WeekTrend?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TDText(
            text = stringResource(
                com.todoapp.mobile.R.string.activity_week_summary,
                weeklyCompleted,
                weeklyPending,
            ),
            style = TDTheme.typography.heading5,
            color = TDTheme.colors.onBackground,
        )
        if (weekTrend != null) {
            Spacer(modifier = Modifier.height(2.dp))
            val (textRes, color) = when (weekTrend.direction) {
                TrendDirection.UP -> com.todoapp.mobile.R.string.activity_trend_up to TDTheme.colors.darkGreen
                TrendDirection.DOWN -> com.todoapp.mobile.R.string.activity_trend_down to TDTheme.colors.crossRed
                TrendDirection.FLAT -> com.todoapp.mobile.R.string.activity_trend_flat to TDTheme.colors.gray
            }
            val text = if (weekTrend.direction == TrendDirection.FLAT) {
                stringResource(textRes)
            } else {
                stringResource(textRes, weekTrend.percentDelta)
            }
            TDText(
                text = text,
                style = TDTheme.typography.subheading1,
                color = color,
            )
        }
    }
}

@Composable
private fun StreakAndBestDay(
    streakDays: Int,
    bestDay: BestDay?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        TDText(
            text = if (streakDays > 0) {
                stringResource(com.todoapp.mobile.R.string.activity_streak_label, streakDays)
            } else {
                stringResource(com.todoapp.mobile.R.string.activity_streak_none)
            },
            style = TDTheme.typography.heading4,
            color = if (streakDays > 0) TDTheme.colors.orange else TDTheme.colors.gray,
        )
        if (bestDay != null) {
            TDText(
                text = stringResource(
                    com.todoapp.mobile.R.string.activity_best_day,
                    stringResource(dayOfWeekRes(bestDay.day)),
                    bestDay.count,
                ),
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.onBackground,
            )
        }
    }
}

@Composable
private fun CategoryBreakdownSection(stats: List<CategoryStat>) {
    val maxCount = stats.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TDText(
            text = stringResource(com.todoapp.mobile.R.string.activity_category_breakdown_title),
            style = TDTheme.typography.heading5,
            color = TDTheme.colors.onBackground,
        )
        stats.forEach { stat ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                TDText(
                    modifier = Modifier.width(96.dp),
                    text = stat.customLabel ?: stringResource(categoryLabelRes(stat.category)),
                    style = TDTheme.typography.subheading2,
                    color = TDTheme.colors.onBackground,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .background(
                            color = TDTheme.colors.lightPending,
                            shape = RoundedCornerShape(4.dp),
                        ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(stat.count.toFloat() / maxCount)
                            .height(8.dp)
                            .background(
                                color = TDTheme.colors.purple,
                                shape = RoundedCornerShape(4.dp),
                            ),
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                TDText(
                    text = stat.count.toString(),
                    style = TDTheme.typography.subheading1,
                    color = TDTheme.colors.onBackground,
                )
            }
        }
    }
}

private fun dayOfWeekRes(day: DayOfWeek): Int = when (day) {
    DayOfWeek.MONDAY -> com.todoapp.mobile.R.string.day_monday
    DayOfWeek.TUESDAY -> com.todoapp.mobile.R.string.day_tuesday
    DayOfWeek.WEDNESDAY -> com.todoapp.mobile.R.string.day_wednesday
    DayOfWeek.THURSDAY -> com.todoapp.mobile.R.string.day_thursday
    DayOfWeek.FRIDAY -> com.todoapp.mobile.R.string.day_friday
    DayOfWeek.SATURDAY -> com.todoapp.mobile.R.string.day_saturday
    DayOfWeek.SUNDAY -> com.todoapp.mobile.R.string.day_sunday
}

private fun categoryLabelRes(category: TaskCategory): Int = when (category) {
    TaskCategory.PERSONAL -> com.todoapp.mobile.R.string.category_personal
    TaskCategory.SHOPPING -> com.todoapp.mobile.R.string.category_shopping
    TaskCategory.MEDICINE -> com.todoapp.mobile.R.string.category_medicine
    TaskCategory.HEALTH -> com.todoapp.mobile.R.string.category_health
    TaskCategory.WORK -> com.todoapp.mobile.R.string.category_work
    TaskCategory.STUDY -> com.todoapp.mobile.R.string.category_study
    TaskCategory.BIRTHDAY -> com.todoapp.mobile.R.string.category_birthday
    TaskCategory.OTHER -> com.todoapp.mobile.R.string.category_other
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun ActivityScreenSuccessPreview() {
    TDTheme {
        ActivitySuccessContent(
            onAction = {},
            uiState = UiState.Success(
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

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun ActivityScreenSuccessRichPreview() {
    TDTheme {
        ActivitySuccessContent(
            onAction = {},
            uiState = UiState.Success(
                weeklyCompleted = 18,
                weeklyPending = 6,
                weeklyProgress = 0.75f,
                weeklyPendingProgress = 0.25f,
                weeklyBarValues = listOf(3, 2, 4, 2, 5, 1, 1),
                yearlyProgress = 0.42f,
                selectedDate = LocalDate.now(),
                yearlyPendingProgress = 0f,
                yearlyCompleted = 178,
                yearlyTotal = 420,
                weeklyPendingBarValues = listOf(1, 0, 1, 1, 2, 1, 0),
                includeRecurring = true,
                weekTrend = WeekTrend(TrendDirection.UP, 20),
                streakDays = 5,
                bestDay = BestDay(DayOfWeek.TUESDAY, 8),
                categoryBreakdown = listOf(
                    CategoryStat(TaskCategory.WORK, null, 8),
                    CategoryStat(TaskCategory.HEALTH, null, 5),
                    CategoryStat(TaskCategory.STUDY, null, 3),
                ),
            ),
        )
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun ActivityScreenEmptyWeekPreview() {
    TDTheme {
        ActivitySuccessContent(
            onAction = {},
            uiState = UiState.Success(
                weeklyCompleted = 0,
                weeklyPending = 0,
                weeklyProgress = 0f,
                weeklyPendingProgress = 0f,
                weeklyBarValues = listOf(0, 0, 0, 0, 0, 0, 0),
                yearlyProgress = 0f,
                selectedDate = LocalDate.now(),
                yearlyPendingProgress = 0f,
                yearlyCompleted = 0,
                yearlyTotal = 0,
                weeklyPendingBarValues = listOf(0, 0, 0, 0, 0, 0, 0),
            ),
        )
    }
}
