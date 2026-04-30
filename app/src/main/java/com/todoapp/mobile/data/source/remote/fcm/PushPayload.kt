package com.todoapp.mobile.data.source.remote.fcm

sealed class PushPayload {
    abstract val type: String
    abstract val title: String?
    abstract val body: String?

    data class TaskAssigned(
        override val title: String?,
        override val body: String?,
        val groupId: Long,
        val taskId: Long,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "task_assigned"
        }
    }

    data class TaskCompleted(
        override val title: String?,
        override val body: String?,
        val groupId: Long,
        val taskId: Long,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "task_completed"
        }
    }

    data class TaskDueSoon(
        override val title: String?,
        override val body: String?,
        val groupId: Long,
        val taskId: Long,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "task_due_soon"
        }
    }

    data class InvitationReceived(
        override val title: String?,
        override val body: String?,
        val invitationId: Long?,
        val groupId: Long?,
        val groupName: String?,
        val inviterName: String?,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "invitation_received"
        }
    }

    data class InvitationAccepted(
        override val title: String?,
        override val body: String?,
        val groupId: Long?,
        val acceptorName: String?,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "invitation_accepted"
        }
    }

    data class InvitationDeclined(
        override val title: String?,
        override val body: String?,
        val groupId: Long?,
        val declinerName: String?,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "invitation_declined"
        }
    }

    data class GroupInvite(
        override val title: String?,
        override val body: String?,
        val groupId: Long,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "group_invite"
        }
    }

    data class GroupTaskChanged(
        override val title: String?,
        override val body: String?,
        val groupId: Long,
        val taskId: Long?,
        val silent: Boolean,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "group_task_changed"
        }
    }

    data class TaskListChanged(
        override val title: String?,
        override val body: String?,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "task_list_changed"
        }
    }

    data class GroupOwnershipTransferred(
        override val title: String?,
        override val body: String?,
        val groupId: Long,
        val groupName: String?,
    ) : PushPayload() {
        override val type: String = TYPE
        companion object {
            const val TYPE = "group_ownership_transferred"
        }
    }

    data class Unknown(
        override val type: String,
        override val title: String?,
        override val body: String?,
    ) : PushPayload()
}

object PushPayloadParser {
    @Suppress("LongMethod", "ReturnCount", "CyclomaticComplexMethod")
    fun parse(data: Map<String, String>): PushPayload? {
        val type = data["type"] ?: return null
        val title = data["title"]
        val body = data["body"]
        val groupId = data["groupId"]?.toLongOrNull()
        val taskId = data["taskId"]?.toLongOrNull()
        return when (type) {
            PushPayload.TaskAssigned.TYPE ->
                if (groupId != null && taskId != null) {
                    PushPayload.TaskAssigned(title, body, groupId, taskId)
                } else {
                    null
                }
            PushPayload.TaskCompleted.TYPE ->
                if (groupId != null && taskId != null) {
                    PushPayload.TaskCompleted(title, body, groupId, taskId)
                } else {
                    null
                }
            PushPayload.TaskDueSoon.TYPE ->
                if (groupId != null && taskId != null) {
                    PushPayload.TaskDueSoon(title, body, groupId, taskId)
                } else {
                    null
                }
            PushPayload.InvitationReceived.TYPE ->
                PushPayload.InvitationReceived(
                    title = title,
                    body = body,
                    invitationId = data["invitationId"]?.toLongOrNull(),
                    groupId = groupId,
                    groupName = data["groupName"],
                    inviterName = data["inviterName"],
                )
            PushPayload.InvitationAccepted.TYPE ->
                PushPayload.InvitationAccepted(
                    title = title,
                    body = body,
                    groupId = groupId,
                    acceptorName = data["acceptorName"],
                )
            PushPayload.InvitationDeclined.TYPE ->
                PushPayload.InvitationDeclined(
                    title = title,
                    body = body,
                    groupId = groupId,
                    declinerName = data["declinerName"],
                )
            PushPayload.GroupInvite.TYPE ->
                if (groupId != null) PushPayload.GroupInvite(title, body, groupId) else null
            PushPayload.GroupTaskChanged.TYPE ->
                if (groupId != null) {
                    PushPayload.GroupTaskChanged(
                        title = title,
                        body = body,
                        groupId = groupId,
                        taskId = taskId,
                        silent = data["silent"]?.toBooleanStrictOrNull() == true,
                    )
                } else {
                    null
                }
            PushPayload.TaskListChanged.TYPE -> PushPayload.TaskListChanged(title, body)
            PushPayload.GroupOwnershipTransferred.TYPE ->
                if (groupId != null) {
                    PushPayload.GroupOwnershipTransferred(
                        title = title,
                        body = body,
                        groupId = groupId,
                        groupName = data["groupName"],
                    )
                } else {
                    null
                }
            else -> PushPayload.Unknown(type, title, body)
        }
    }
}
