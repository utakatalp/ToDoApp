package com.todoapp.mobile.ui.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.common.move
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiState
import com.todoapp.uikit.components.TDStatisticCard
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDUndoSnackbar
import com.todoapp.uikit.components.TDWeeklyDatePicker
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.delay
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier,
    uiState: UiState.Success,
    onAction: (UiAction) -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()

    var localTasks by remember { mutableStateOf(uiState.tasks) }
    var dragOriginalIndex by remember { mutableIntStateOf(-1) }
    var dragFinalIndex by remember { mutableIntStateOf(-1) }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        if (dragOriginalIndex == -1) dragOriginalIndex = from.index
        dragFinalIndex = to.index
        val list = localTasks.toMutableList()
        list.move(from.index, to.index)
        localTasks = list
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    LaunchedEffect(uiState.tasks) {
        if (!reorderableLazyListState.isAnyItemDragging) {
            localTasks = uiState.tasks
        }
    }
    var showHint by remember { mutableStateOf(false) }
    var currentHintRes by remember { mutableIntStateOf(com.todoapp.mobile.R.string.hint_swipe_left_delete) }
    val hints = listOf(
        com.todoapp.mobile.R.string.hint_swipe_left_delete,
        com.todoapp.mobile.R.string.hint_swipe_right_secret,
        com.todoapp.mobile.R.string.hint_long_press_reorder,
        com.todoapp.mobile.R.string.hint_add_task,
        com.todoapp.mobile.R.string.hint_secret_on_create,
        com.todoapp.mobile.R.string.hint_calendar_range,
        com.todoapp.mobile.R.string.hint_activity_stats,
        com.todoapp.mobile.R.string.hint_task_detail,
        com.todoapp.mobile.R.string.hint_daily_plan,
        com.todoapp.mobile.R.string.hint_groups,
        com.todoapp.mobile.R.string.hint_theme,
        com.todoapp.mobile.R.string.hint_pomodoro,
        com.todoapp.mobile.R.string.hint_weekly_picker,
        com.todoapp.mobile.R.string.hint_weekly_stats,
        com.todoapp.mobile.R.string.hint_group_reorder,
    )
    val pullState = rememberPullToRefreshState()
    LaunchedEffect(showHint) {
        if (showHint) {
            delay(4000L)
            showHint = false
        }
    }

    PullToRefreshBox(
        isRefreshing = false,
        onRefresh = {
            currentHintRes = hints.random()
            showHint = true
        },
        state = pullState,
        indicator = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (pullState.distanceFraction > 0f) {
                    Row(
                        modifier = Modifier
                            .background(
                                color = TDTheme.colors.pendingGray.copy(
                                    alpha = (pullState.distanceFraction * 1.5f).coerceIn(0.4f, 1f)
                                ),
                                shape = RoundedCornerShape(50.dp),
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_info),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        TDText(
                            text = stringResource(
                                if (pullState.distanceFraction >= 1f) {
                                    com.todoapp.mobile.R.string.hint_release_indicator
                                } else {
                                    com.todoapp.mobile.R.string.hint_pull_indicator
                                }
                            ),
                            style = TDTheme.typography.subheading4,
                            color = Color.White,
                        )
                    }
                }
            }
        },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            HomeHintCard(showHint = showHint, hintRes = currentHintRes)
            TDWeeklyDatePicker(
                modifier = Modifier,
                displayedMonth = uiState.displayedMonth,
                selectedDate = uiState.selectedDate,
                onDateSelect = { onAction(UiAction.OnDateSelect(it)) },
                onPreviousMonth = { onAction(UiAction.OnPreviousMonth) },
                onNextMonth = { onAction(UiAction.OnNextMonth) },
            )
            Spacer(Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TDStatisticCard(
                    text = stringResource(com.todoapp.mobile.R.string.task_complete),
                    taskAmount = uiState.completedTaskCountThisWeek,
                    modifier = Modifier.weight(1f),
                    isCompleted = true,
                    onClick = { onAction(UiAction.OnCompletedStatCardTap) },
                )
                Spacer(modifier = Modifier.size(20.dp))
                TDStatisticCard(
                    text = stringResource(com.todoapp.mobile.R.string.task_pending),
                    taskAmount = uiState.pendingTaskCountThisWeek,
                    modifier = Modifier.weight(1f),
                    isCompleted = false,
                    onClick = { onAction(UiAction.OnPendingStatCardTap) },
                )
            }
            Spacer(Modifier.height(32.dp))
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.tasks_today),
                color = TDTheme.colors.onBackground,
                style = TDTheme.typography.heading3,
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .heightIn(min = 300.dp),
            ) {
                HomeTaskList(
                    tasks = localTasks.filter { it.id != uiState.pendingDeleteTask?.id },
                    lazyListState = lazyListState,
                    reorderableLazyListState = reorderableLazyListState,
                    hapticFeedback = hapticFeedback,
                    onTaskCheck = { onAction(UiAction.OnTaskCheck(it)) },
                    onTaskClick = { onAction(UiAction.OnTaskClick(it)) },
                    onTaskLongPress = { onAction(UiAction.OnTaskLongPress(it)) },
                    onToggleTaskSecret = { onAction(UiAction.OnToggleTaskSecret(it)) },
                    onMoveTask = { from, to -> onAction(UiAction.OnMoveTask(from, to)) },
                    onReorderFinished = {
                        val orig = dragOriginalIndex
                        val final = dragFinalIndex
                        dragOriginalIndex = -1
                        dragFinalIndex = -1
                        if (orig != -1 && final != -1 && orig != final) {
                            onAction(UiAction.OnMoveTask(orig, final))
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
                HomeFabMenu(
                    onAddTask = { onAction(UiAction.OnShowBottomSheet) },
                    onPomodoro = { onAction(UiAction.OnPomodoroTap) },
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                ) {
                    this@Column.AnimatedVisibility(
                        visible = uiState.pendingDeleteTask != null,
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut(),
                    ) {
                        TDUndoSnackbar(
                            message = stringResource(com.todoapp.mobile.R.string.task_deleted),
                            onUndo = { onAction(UiAction.OnUndoDelete) },
                        )
                    }
                }
            }
        }
        if (uiState.isDeleteDialogOpen) {
            AlertDialog(
                onDismissRequest = { onAction(UiAction.OnDeleteDialogDismiss) },
                title = { Text(stringResource(com.todoapp.mobile.R.string.delete_task_title)) },
                titleContentColor = TDTheme.colors.onBackground,
                containerColor = TDTheme.colors.background,
                textContentColor = TDTheme.colors.gray,
                text = { Text(stringResource(com.todoapp.mobile.R.string.delete_task_message)) },
                confirmButton = {
                    TextButton(onClick = { onAction(UiAction.OnDeleteDialogConfirm) }) {
                        Text(
                            text = stringResource(com.todoapp.mobile.R.string.delete),
                            color = TDTheme.colors.crossRed,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onAction(UiAction.OnDeleteDialogDismiss) }) {
                        Text(
                            text = stringResource(com.todoapp.mobile.R.string.cancel),
                            color = TDTheme.colors.gray,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun HomeHintCard(showHint: Boolean, hintRes: Int) {
    AnimatedVisibility(
        visible = showHint,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(TDTheme.colors.infoCardBgColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = null,
                tint = TDTheme.colors.pendingGray,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                TDText(
                    text = stringResource(com.todoapp.mobile.R.string.hint_tip_label),
                    style = TDTheme.typography.subheading4,
                    color = TDTheme.colors.pendingGray,
                )
                Spacer(modifier = Modifier.height(4.dp))
                TDText(
                    text = stringResource(hintRes),
                    style = TDTheme.typography.subheading4,
                    color = TDTheme.colors.onSurface,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    TDTheme {
        HomeContent(
            uiState = HomePreviewData.successState(
                tasks = HomePreviewData.sampleTasks,
                completedTaskCountThisWeek = 5,
                pendingTaskCountThisWeek = 8,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview_no_task() {
    TDTheme {
        HomeContent(
            uiState = HomePreviewData.successState(
                tasks = emptyList(),
                completedTaskCountThisWeek = 0,
                pendingTaskCountThisWeek = 0,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview_no_task_dark() {
    TDTheme {
        HomeContent(
            uiState = HomePreviewData.successState(
                tasks = emptyList(),
                completedTaskCountThisWeek = 0,
                pendingTaskCountThisWeek = 0,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeContentPreview_Dark() {
    TDTheme(darkTheme = true) {
        HomeContent(
            uiState = HomePreviewData.successState(
                tasks = HomePreviewData.sampleTasks,
                completedTaskCountThisWeek = 5,
                pendingTaskCountThisWeek = 8,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
