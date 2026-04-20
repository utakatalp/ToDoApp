package com.todoapp.mobile.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.common.maskDescription
import com.todoapp.mobile.common.maskTitle
import com.todoapp.mobile.domain.model.Task
import com.todoapp.uikit.components.TDTaskCardWithCheckbox
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTaskList(
    tasks: List<Task>,
    lazyListState: LazyListState,
    reorderableLazyListState: ReorderableLazyListState,
    hapticFeedback: HapticFeedback,
    onTaskCheck: (Task) -> Unit,
    onTaskClick: (Task) -> Unit,
    onTaskLongPress: (Task) -> Unit,
    onToggleTaskSecret: (Task) -> Unit,
    onMoveTask: (Int, Int) -> Unit,
    onReorderFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        if (tasks.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = painterResource(
                        if (TDTheme.isDark) {
                            com.todoapp.mobile.R.drawable.ic_home_idle_img_dark
                        } else {
                            com.todoapp.mobile.R.drawable.ic_home_idle_img_light
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.sizeIn(maxHeight = 200.dp),
                )
                Spacer(Modifier.height(12.dp))
                TDText(
                    text = stringResource(com.todoapp.mobile.R.string.no_tasks_today),
                    style = TDTheme.typography.heading3,
                    color = TDTheme.colors.onBackground,
                )
                Spacer(Modifier.height(8.dp))
                TDText(
                    text = stringResource(com.todoapp.mobile.R.string.no_tasks_today_description),
                    modifier = Modifier.padding(horizontal = 48.dp),
                    style = TDTheme.typography.heading6,
                    color = TDTheme.colors.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            val isAnyDragging = reorderableLazyListState.isAnyItemDragging
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(
                    items = tasks,
                    key = { _, task -> task.id }
                ) { index, task ->
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = task.id,
                    ) { isDragging ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val dismissState = rememberSwipeToDismissBoxState()

                        LaunchedEffect(dismissState.currentValue) {
                            when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    onTaskLongPress(task)
                                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    onToggleTaskSecret(task)
                                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                }
                                SwipeToDismissBoxValue.Settled -> {}
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                HomeSwipeDismissBackground(direction = dismissState.dismissDirection)
                            },
                        ) {
                            Card(
                                onClick = { onTaskClick(task) },
                                modifier = Modifier.semantics {
                                    customActions = listOf(
                                        CustomAccessibilityAction(
                                            label = "Move Up",
                                            action = {
                                                if (index > 0) {
                                                    onMoveTask(index, index - 1)
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                        ),
                                        CustomAccessibilityAction(
                                            label = "Move Down",
                                            action = {
                                                if (index < tasks.lastIndex) {
                                                    onMoveTask(index, index + 1)
                                                    true
                                                } else {
                                                    false
                                                }
                                            }
                                        ),
                                    )
                                },
                                interactionSource = interactionSource,
                            ) {
                                val firstPhoto = task.photoUrls.firstOrNull()
                                if (firstPhoto != null) {
                                    androidx.compose.foundation.layout.Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                            .background(com.todoapp.uikit.theme.TDTheme.colors.lightPending),
                                    ) {
                                        SecretOrNormalPhotoBanner(
                                            url = run {
                                                val base = com.todoapp.mobile.BuildConfig.BASE_URL.trimEnd('/')
                                                "$base/${firstPhoto.trimStart('/')}"
                                            },
                                            isSecret = task.isSecret,
                                        )
                                        TDTaskCardWithCheckbox(
                                            taskText = if (task.isSecret) task.title.maskTitle() else task.title,
                                            taskDescription = if (task.isSecret) task.description?.maskDescription() else task.description,
                                            isChecked = task.isCompleted,
                                            onCheckBoxClick = { onTaskCheck(task) },
                                            isDragging = isDragging,
                                            isAnyDragging = isAnyDragging,
                                            modifier = Modifier.longPressDraggableHandle(
                                                onDragStarted = {
                                                    hapticFeedback.performHapticFeedback(
                                                        HapticFeedbackType.GestureThresholdActivate
                                                    )
                                                },
                                                onDragStopped = {
                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                                    onReorderFinished()
                                                },
                                            ),
                                        )
                                    }
                                } else {
                                    TDTaskCardWithCheckbox(
                                        taskText = if (task.isSecret) task.title.maskTitle() else task.title,
                                        taskDescription = if (task.isSecret) task.description?.maskDescription() else task.description,
                                        isChecked = task.isCompleted,
                                        onCheckBoxClick = { onTaskCheck(task) },
                                        isDragging = isDragging,
                                        isAnyDragging = isAnyDragging,
                                        modifier = Modifier.longPressDraggableHandle(
                                            onDragStarted = {
                                                hapticFeedback.performHapticFeedback(
                                                    HapticFeedbackType.GestureThresholdActivate
                                                )
                                            },
                                            onDragStopped = {
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                                onReorderFinished()
                                            },
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeSwipeDismissBackground(direction: SwipeToDismissBoxValue) {
    val color by animateColorAsState(
        targetValue = when (direction) {
            SwipeToDismissBoxValue.EndToStart -> TDTheme.colors.crossRed
            SwipeToDismissBoxValue.StartToEnd -> TDTheme.colors.pendingGray
            else -> Color.Transparent
        },
        label = "swipe_bg_color",
    )
    val alignment = when (direction) {
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.CenterStart
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = alignment,
    ) {
        when (direction) {
            SwipeToDismissBoxValue.EndToStart -> Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = null,
                tint = Color.White,
            )
            SwipeToDismissBoxValue.StartToEnd -> Icon(
                painter = painterResource(com.todoapp.mobile.R.drawable.ic_secret_mode),
                contentDescription = null,
                tint = Color.White,
            )
            else -> {}
        }
    }
}
