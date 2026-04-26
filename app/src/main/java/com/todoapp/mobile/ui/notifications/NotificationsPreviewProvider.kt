package com.todoapp.mobile.ui.notifications

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.todoapp.mobile.domain.model.Notification
import com.todoapp.mobile.domain.model.NotificationType

class NotificationsPreviewProvider : PreviewParameterProvider<NotificationsContract.UiState> {
    override val values: Sequence<NotificationsContract.UiState>
        get() {
            val now = System.currentTimeMillis()
            val sample =
                listOf(
                    Notification(
                        id = 1,
                        type = NotificationType.INVITATION_RECEIVED,
                        title = "New invitation",
                        body = "Berat invited you to Smith Family",
                        payload =
                        mapOf(
                            "groupName" to "Smith Family",
                            "groupDescription" to "Daily chores and groceries",
                            "memberCount" to "5",
                        ),
                        isRead = false,
                        createdAt = now - 30_000,
                    ),
                    Notification(
                        id = 2,
                        type = NotificationType.TASK_ASSIGNED,
                        title = "New task",
                        body = "Buy groceries assigned to you",
                        payload = mapOf("taskTitle" to "Buy groceries"),
                        isRead = false,
                        createdAt = now - 600_000,
                    ),
                    Notification(
                        id = 3,
                        type = NotificationType.TASK_COMPLETED,
                        title = "Task completed",
                        body = "Ayse completed Submit weekly report",
                        payload = mapOf("taskTitle" to "Submit weekly report"),
                        isRead = true,
                        createdAt = now - 86_400_000,
                    ),
                    Notification(
                        id = 4,
                        type = NotificationType.TASK_DUE_SOON,
                        title = "Due soon",
                        body = "Pay electricity bill is due in 1 hour",
                        payload = mapOf("taskTitle" to "Pay electricity bill"),
                        isRead = true,
                        createdAt = now - 86_400_000 * 3,
                    ),
                )

            return sequenceOf(
                NotificationsContract.UiState.Loading,
                NotificationsContract.UiState.Error("Could not load notifications"),
                NotificationsContract.UiState.Success(items = emptyList()),
                NotificationsContract.UiState.Success(items = sample),
                NotificationsContract.UiState.Success(items = sample, isRefreshing = true),
                NotificationsContract.UiState.Success(items = sample.map { it.copy(isRead = true) }),
            )
        }
}
