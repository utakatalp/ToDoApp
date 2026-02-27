package com.todoapp.mobile.ui.groups

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class GroupsPreviewProvider : PreviewParameterProvider<GroupsContract.UiState> {
    override val values: Sequence<GroupsContract.UiState> = sequenceOf(
        GroupsContract.UiState.Empty(isUserAuthenticated = true),
        GroupsContract.UiState.Success(
            groups = mockGroups,
            isUserAuthenticated = true
        ),
    )
}

val mockGroups = listOf(
    GroupsContract.GroupUiItem(
        id = 1L,
        name = "The Smith Family",
        role = "ADMIN",
        description = "Daily chores, grocery lists, and vacation planning for 2024. Keep up the good work!",
        memberCount = 5,
        pendingTaskCount = 9,
        createdAt = "Jan 12, 2023"
    ),
    GroupsContract.GroupUiItem(
        id = 2L,
        name = "Extended Cousins",
        role = "MEMBER",
        description = "Planning the annual reunion, potluck coordination, and secret santa gifts.",
        memberCount = 14,
        pendingTaskCount = 5,
        createdAt = "Mar 05, 2023"
    ),
    GroupsContract.GroupUiItem(
        id = 3L,
        name = "Work Project",
        role = "ADMIN",
        description = "Sprint tasks, code reviews, and deployment checklist for Q1.",
        memberCount = 8,
        pendingTaskCount = 4,
        createdAt = "Feb 21, 2023"
    ),
)
