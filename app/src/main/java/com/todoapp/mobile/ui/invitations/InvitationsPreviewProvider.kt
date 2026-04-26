package com.todoapp.mobile.ui.invitations

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.todoapp.mobile.domain.model.Invitation

class InvitationsPreviewProvider : PreviewParameterProvider<InvitationsContract.UiState> {
    override val values: Sequence<InvitationsContract.UiState>
        get() {
            val now = System.currentTimeMillis()
            val sample =
                listOf(
                    Invitation(
                        id = 1,
                        groupId = 100,
                        groupName = "Smith Family",
                        groupAvatarUrl = null,
                        inviterUserId = 10,
                        inviterName = "Berat Baran",
                        inviterAvatarUrl = null,
                        inviteeEmail = "you@example.com",
                        createdAt = now - 60_000,
                    ),
                    Invitation(
                        id = 2,
                        groupId = 200,
                        groupName = "Roommates",
                        groupAvatarUrl = null,
                        inviterUserId = 20,
                        inviterName = "Ayse Y.",
                        inviterAvatarUrl = null,
                        inviteeEmail = "you@example.com",
                        createdAt = now - 3_600_000,
                    ),
                    Invitation(
                        id = 3,
                        groupId = 300,
                        groupName = "Work Squad",
                        groupAvatarUrl = null,
                        inviterUserId = 30,
                        inviterName = "Mehmet K.",
                        inviterAvatarUrl = null,
                        inviteeEmail = "you@example.com",
                        createdAt = now - 86_400_000 * 2,
                    ),
                )

            return sequenceOf(
                InvitationsContract.UiState.Loading,
                InvitationsContract.UiState.Error("Could not load invitations"),
                InvitationsContract.UiState.Success(items = emptyList()),
                InvitationsContract.UiState.Success(items = sample),
                InvitationsContract.UiState.Success(items = sample, isRefreshing = true),
                InvitationsContract.UiState.Success(items = sample, processingIds = setOf(1L)),
                InvitationsContract.UiState.Success(
                    items = sample,
                    pendingAction = InvitationsContract.PendingAction.Accept(sample.first()),
                ),
                InvitationsContract.UiState.Success(
                    items = sample,
                    pendingAction = InvitationsContract.PendingAction.Decline(sample[1]),
                ),
            )
        }
}
