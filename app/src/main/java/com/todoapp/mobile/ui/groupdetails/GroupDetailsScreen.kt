package com.todoapp.mobile.ui.groupdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.domain.model.Task
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDGroupTaskCard
import com.todoapp.uikit.components.TDIconWithText
import com.todoapp.uikit.components.TDLoadingBar
import com.todoapp.uikit.components.TDSegmentedSwitch
import com.todoapp.uikit.components.TDSquareCard
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun GroupDetailsScreen(
    uiState: GroupDetailsContract.UiState,
    onAction: (GroupDetailsContract.UiAction) -> Unit,
) {
    when (uiState) {
        is GroupDetailsContract.UiState.Empty -> GroupDetailsEmptyContent(uiState, onAction)
        is GroupDetailsContract.UiState.Error -> TODO()
        GroupDetailsContract.UiState.Loading -> TDLoadingBar()
        is GroupDetailsContract.UiState.Success -> GroupDetailsContent(uiState, onAction)
    }
}

@Composable
private fun GroupDetailsContent(
    uiState: GroupDetailsContract.UiState.Success,
    onAction: (GroupDetailsContract.UiAction) -> Unit
) {
    val lazyListState = rememberLazyListState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TDText(
            text = "Organizing our chaotic but lovely life. Chores, grocery lists, and family events go here! ❤",
            color = TDTheme.colors.lightGray,
            modifier = Modifier.fillMaxWidth(),
            // overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TDSquareCard(
                number = uiState.memberCount.toString(),
                text = "MEMBERS",
                modifier = Modifier.weight(1f),
            )
            TDSquareCard(
                numberColor = TDTheme.colors.green,
                number = uiState.completedTaskCount.toString(),
                text = "COMPLETED",
                modifier = Modifier.weight(1f),
            )
            TDSquareCard(
                numberColor = TDTheme.colors.purple,
                number = uiState.pendingTaskCount.toString(),
                text = "PENDING",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TDSegmentedSwitch(
                leftText = "All",
                rightText = "Assigned to Me",
                checked = uiState.checked,
                onCheckedChange = {
                    if (uiState.checked) {
                        onAction(GroupDetailsContract.UiAction.OnAllTap)
                    } else {
                        onAction(GroupDetailsContract.UiAction.OnAssignedToMeTap)
                    }
                }
            )
            TDIconWithText(icon = R.drawable.ic_filter, text = "Filter", modifier = Modifier)
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = uiState.groupTasks,
                key = { _, task -> task.id }
            ) { index, task ->
                TDGroupTaskCard(
                    taskTitle = task.title,
                    taskDescription = task.description,
                    isCompleted = task.isCompleted,
                    assignedTo = task.assignedToDisplayName,
                    date = task.date.toString(),
                    timeStart = task.timeStart.toString(),
                    timeEnd = task.timeEnd.toString(),
                    onCheckboxClick = { onAction(GroupDetailsContract.UiAction.OnTaskCheckboxTap(task)) }
                )
            }
        }
    }
}

@Composable
private fun GroupDetailsEmptyContent(
    uiState: GroupDetailsContract.UiState.Empty,
    onAction: (GroupDetailsContract.UiAction) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.groupBgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.ic_no_task),
            contentDescription = "No Task"
        )
        Spacer(Modifier.height(16.dp))
        TDText(text = "No tasks yet.", style = TDTheme.typography.heading1)
        TDText(
            text = "Create your first task to get started.",
            color = TDTheme.colors.lightGray,
            style = TDTheme.typography.subheading3
        )
        Spacer(Modifier.weight(4f))
        TDButton(
            text = "New Task",
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .align(Alignment.End)
        ) {
            onAction(GroupDetailsContract.UiAction.OnAddTaskTap(uiState.groupId))
        }
    }
}

@Preview(
    name = "GroupDetails - Empty",
    showBackground = true,
)
@Composable
private fun GroupDetailsEmptyPreview() {
    TDTheme {
        GroupDetailsScreen(
            uiState = GroupDetailsContract.UiState.Empty(groupId = 1L),
            onAction = {}
        )
    }
}

@Preview(
    name = "GroupDetails - Loading",
    showBackground = true,
)
@Composable
private fun GroupDetailsLoadingPreview() {
    TDTheme {
        GroupDetailsScreen(
            uiState = GroupDetailsContract.UiState.Loading,
            onAction = {}
        )
    }
}

@Preview(
    name = "GroupDetails - Success",
    showBackground = true,
)
@Composable
private fun GroupDetailsSuccessPreview() {
    TDTheme {
        GroupDetailsScreen(
            uiState = GroupDetailsContract.UiState.Success(
                memberCount = 4,
                pendingTaskCount = 2,
                completedTaskCount = 3,
                groupTasks = listOf(
                    Task.Group(
                        id = 1,
                        title = "Prepare presentation",
                        description = "Prepare slides for Monday meeting",
                        date = LocalDate.of(2026, 2, 18),
                        timeStart = LocalTime.of(10, 0),
                        timeEnd = LocalTime.of(12, 0),
                        isCompleted = false,
                        isSecret = false,
                        orderIndex = 0,
                        groupId = 1,
                        assignedToUserId = 101,
                        assignedToDisplayName = "Natalia",
                        createdByUserId = 201,
                        createdByDisplayName = "Alp",
                        completedByUserId = null,
                        completedByDisplayName = null,
                    ),
                    Task.Group(
                        id = 2,
                        title = "Review PR",
                        description = "asdasd",
                        date = LocalDate.of(2026, 2, 19),
                        timeStart = LocalTime.of(14, 0),
                        timeEnd = LocalTime.of(15, 0),
                        isCompleted = true,
                        isSecret = false,
                        orderIndex = 1,
                        groupId = 1,
                        assignedToUserId = 201,
                        assignedToDisplayName = "Alp",
                        createdByUserId = 101,
                        createdByDisplayName = "Natalia",
                        completedByUserId = 201,
                        completedByDisplayName = "Alp",
                    )
                )
            ),
            onAction = {}
        )
    }
}
