package com.todoapp.mobile.domain.model

enum class NotificationType {
    INVITATION_RECEIVED,
    INVITATION_ACCEPTED,
    INVITATION_DECLINED,
    TASK_ASSIGNED,
    TASK_COMPLETED,
    TASK_DUE_SOON,
    UNKNOWN,
    ;

    companion object {
        fun fromString(value: String?): NotificationType = entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
    }
}

@androidx.compose.runtime.Immutable
data class Notification(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val body: String,
    val payload: Map<String, String>,
    val isRead: Boolean,
    val createdAt: Long,
)
