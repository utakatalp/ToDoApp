package com.todoapp.mobile.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.ui.groups.GroupsContract.UiAction
import com.todoapp.mobile.ui.groups.GroupsContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDCreateGroupButton
import com.todoapp.uikit.components.TDFamilyGroupCard
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun GroupScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    when (uiState) {
        UiState.Empty -> GroupEmptyContent(onCreateNewGroup = { onAction(UiAction.OnCreateNewGroupTap) })
        is UiState.Error -> {}
        UiState.Loading -> {}
        is UiState.Success -> GroupsContent(uiState, onAction)
    }
}

@Composable
private fun GroupsContent(
    uiState: UiState.Success,
    onAction: (UiAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TDText(
                    text = stringResource(com.todoapp.mobile.R.string.my_groups),
                    style = TDTheme.typography.heading1,
                    color = TDTheme.colors.onBackground,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(uiState.groups, key = { it.id }) { group ->
                TDFamilyGroupCard(
                    name = group.name,
                    role = group.role,
                    description = group.description,
                    memberCount = group.memberCount,
                    pendingTaskCount = group.pendingTaskCount,
                    createdDate = group.createdAt,
                    membersIcon = R.drawable.ic_members,
                    tasksIcon = R.drawable.ic_tasks_done,
                    onViewDetailsClick = { onAction(UiAction.OnGroupTap(group.id)) },
                    onDeleteClick = { onAction(UiAction.OnDeleteGroupTap(group.id)) }
                )
            }
        }

        TDCreateGroupButton(
            onClick = { onAction(UiAction.OnCreateNewGroupTap) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

@Composable
private fun GroupEmptyContent(
    onCreateNewGroup: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = TDTheme.colors.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painterResource(R.drawable.ic_avatar_new_group),
            contentDescription = stringResource(com.todoapp.mobile.R.string.new_group),
            modifier = Modifier.size(192.dp),
            tint = TDTheme.colors.primary.copy(0.81f)
        )
        Spacer(Modifier.height(32.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.you_don_t_have_any),
                style = TDTheme.typography.heading2,
                color = TDTheme.colors.onBackground
            )
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.groups_yet),
                style = TDTheme.typography.heading2,
                color = TDTheme.colors.onBackground
            )
        }
        Spacer(Modifier.height(12.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.create_a_group_to_start_collaborating_on),
                style = TDTheme.typography.subheading3,
                color = TDTheme.colors.lightGray
            )
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.tasks_with_your_family),
                style = TDTheme.typography.subheading3,
                color = TDTheme.colors.lightGray
            )
        }
        Spacer(Modifier.height(32.dp))
        TDButton(
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
            text = stringResource(com.todoapp.mobile.R.string.create_new_group),
            fullWidth = true
        ) { onCreateNewGroup() }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun GroupsContentPreview(
    @PreviewParameter(GroupsPreviewProvider::class) uiState: UiState,
) {
    TDTheme {
        GroupScreen(
            uiState = uiState,
            onAction = {}
        )
    }
}
