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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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

    val reorderableLazyListState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            // `from.index` / `to.index` are *absolute* LazyColumn item positions and include
            // every item produced by `headerContent` — they are NOT indices into `localTasks`.
            // Resolve via key (task.id) instead. Without this translation the move() call
            // crashes with IndexOutOfBoundsException as soon as a header is present.
            val fromIndex = localTasks.indexOfFirst { it.id == from.key }
            val toIndex = localTasks.indexOfFirst { it.id == to.key }
            if (fromIndex < 0 || toIndex < 0) return@rememberReorderableLazyListState
            if (dragOriginalIndex == -1) dragOriginalIndex = fromIndex
            dragFinalIndex = toIndex
            val list = localTasks.toMutableList()
            list.move(fromIndex, toIndex)
            localTasks = list
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }

    // Only re-sync localTasks from upstream when no drag is in flight. Adopt the new list when
    // the IDs differ (real insert/delete from server) OR when same IDs but content differs
    // (completion flip etc.). The naive overwrite during a gesture's transition window
    // crashes the reorderable library because its internal key map references items the
    // overwrite removed.
    LaunchedEffect(uiState.tasks) {
        if (reorderableLazyListState.isAnyItemDragging) return@LaunchedEffect
        val sameIds = uiState.tasks.size == localTasks.size &&
            uiState.tasks.zip(localTasks).all { (a, b) -> a.id == b.id }
        if (!sameIds || uiState.tasks != localTasks) {
            localTasks = uiState.tasks
        }
    }
    var showHint by remember { mutableStateOf(false) }
    var currentHintRes by remember { mutableIntStateOf(com.todoapp.mobile.R.string.hint_swipe_left_delete) }
    val hints =
        listOf(
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
            com.todoapp.mobile.R.string.hint_task_photos,
            com.todoapp.mobile.R.string.hint_group_task_flip,
            com.todoapp.mobile.R.string.hint_group_task_assign_swipe,
            com.todoapp.mobile.R.string.hint_group_task_priority,
            com.todoapp.mobile.R.string.hint_profile_avatar,
            com.todoapp.mobile.R.string.hint_group_avatar,
            com.todoapp.mobile.R.string.hint_pomodoro_banner,
            com.todoapp.mobile.R.string.hint_transfer_ownership,
            com.todoapp.mobile.R.string.hint_search_across_groups,
            com.todoapp.mobile.R.string.hint_group_task_edit,
            com.todoapp.mobile.R.string.hint_undo_delete,
            com.todoapp.mobile.R.string.hint_language_switch,
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
                modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (pullState.distanceFraction > 0f) {
                    Row(
                        modifier =
                        Modifier
                            .background(
                                color =
                                TDTheme.colors.pendingGray.copy(
                                    alpha = (pullState.distanceFraction * 1.5f).coerceIn(0.4f, 1f),
                                ),
                                shape = RoundedCornerShape(50.dp),
                            ).padding(horizontal = 16.dp, vertical = 8.dp),
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
                            text =
                            stringResource(
                                if (pullState.distanceFraction >= 1f) {
                                    com.todoapp.mobile.R.string.hint_release_indicator
                                } else {
                                    com.todoapp.mobile.R.string.hint_pull_indicator
                                },
                            ),
                            style = TDTheme.typography.subheading4,
                            color = Color.White,
                        )
                    }
                }
            }
        },
    ) {
        Box(modifier = modifier.fillMaxSize()) {
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
                headerContent = {
                    if (uiState.pendingPermissions.isNotEmpty()) {
                        item {
                            HomePermissionPrompts(
                                permissions = uiState.pendingPermissions,
                                onAction = onAction,
                            )
                        }
                    }
                    item { HomeHintCard(showHint = showHint, hintRes = currentHintRes) }
                    item {
                        HomeFilterRow(
                            selected = uiState.selectedFilter,
                            onSelected = { onAction(UiAction.OnFilterChange(it)) },
                        )
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                    if (uiState.selectedFilter == HomeContract.HomeFilter.TODAY) {
                        item {
                            TDWeeklyDatePicker(
                                modifier = Modifier,
                                displayedMonth = uiState.displayedMonth,
                                selectedDate = uiState.selectedDate,
                                onDateSelect = { onAction(UiAction.OnDateSelect(it)) },
                                onPreviousMonth = { onAction(UiAction.OnPreviousMonth) },
                                onNextMonth = { onAction(UiAction.OnNextMonth) },
                            )
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                        item {
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
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                        item {
                            TDText(
                                text = stringResource(com.todoapp.mobile.R.string.tasks_today),
                                color = TDTheme.colors.onBackground,
                                style = TDTheme.typography.heading3,
                            )
                        }
                    } else {
                        item {
                            TDText(
                                text = stringResource(sectionHeaderRes(uiState.selectedFilter)),
                                color = TDTheme.colors.onBackground,
                                style = TDTheme.typography.heading3,
                            )
                        }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                },
            )
            HomeFabMenu(
                onAddTask = { onAction(UiAction.OnShowBottomSheet) },
                onPomodoro = { onAction(UiAction.OnPomodoroTap) },
            )
            Box(
                modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            ) {
                AnimatedVisibility(
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

private fun sectionHeaderRes(filter: HomeContract.HomeFilter): Int = when (filter) {
    HomeContract.HomeFilter.TODAY -> com.todoapp.mobile.R.string.tasks_today
    HomeContract.HomeFilter.DAILY -> com.todoapp.mobile.R.string.section_daily_tasks
    HomeContract.HomeFilter.WEEKLY -> com.todoapp.mobile.R.string.section_weekly_tasks
    HomeContract.HomeFilter.MONTHLY -> com.todoapp.mobile.R.string.section_monthly_tasks
    HomeContract.HomeFilter.YEARLY -> com.todoapp.mobile.R.string.section_yearly_tasks
}

@Composable
private fun HomeHintCard(
    showHint: Boolean,
    hintRes: Int,
) {
    AnimatedVisibility(
        visible = showHint,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Row(
            modifier =
            Modifier
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
                modifier =
                Modifier
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
            uiState =
            HomePreviewData.successState(
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
            uiState =
            HomePreviewData.successState(
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
            uiState =
            HomePreviewData.successState(
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
            uiState =
            HomePreviewData.successState(
                tasks = HomePreviewData.sampleTasks,
                completedTaskCountThisWeek = 5,
                pendingTaskCountThisWeek = 8,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun HomeContentSheetOpenPreview() {
    TDTheme {
        HomeContent(
            uiState =
            HomePreviewData.successState(
                tasks = HomePreviewData.sampleTasks,
                completedTaskCountThisWeek = 5,
                pendingTaskCountThisWeek = 8,
                isSheetOpen = true,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun HomeContentDeleteDialogPreview() {
    TDTheme {
        HomeContent(
            uiState =
            HomePreviewData.successState(
                tasks = HomePreviewData.sampleTasks,
                completedTaskCountThisWeek = 5,
                pendingTaskCountThisWeek = 8,
                isDeleteDialogOpen = true,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun HomeContentSecretModeOffPreview() {
    TDTheme {
        HomeContent(
            uiState =
            HomePreviewData.successState(
                tasks = HomePreviewData.sampleTasks,
                completedTaskCountThisWeek = 5,
                pendingTaskCountThisWeek = 8,
                isSecretModeEnabled = false,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

@com.todoapp.uikit.previews.TDPreview
@Composable
private fun HomeContentValidationErrorPreview() {
    TDTheme {
        HomeContent(
            uiState =
            HomePreviewData.successState(
                tasks = HomePreviewData.sampleTasks,
                completedTaskCountThisWeek = 5,
                pendingTaskCountThisWeek = 8,
                isSheetOpen = true,
                titleErrorRes = com.todoapp.mobile.R.string.error_task_title_required,
            ),
            onAction = {},
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}
