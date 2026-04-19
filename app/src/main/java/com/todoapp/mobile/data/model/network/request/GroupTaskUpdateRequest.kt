package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.Serializable

/**
 * Partial update for a group task. Field semantics:
 *  - omitted (null) -> no change
 *  - non-null value -> set to that value
 *  - assigneeId + clearAssignee=true -> explicit unassign (omit assigneeId)
 */
@Serializable
data class GroupTaskUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val dueDate: Long? = null,
    val isCompleted: Boolean? = null,
    val priority: String? = null,
    val assigneeId: Long? = null,
    val clearAssignee: Boolean = false,
)
