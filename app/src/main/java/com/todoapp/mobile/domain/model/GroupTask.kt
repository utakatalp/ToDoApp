package com.todoapp.mobile.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class GroupTask(
    val id: Long,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val priority: String?,
    val dueDate: Long?,
    val assignee: GroupMember?,
    val photoUrls: List<String> = emptyList(),
    val groupId: Long? = null,
    val locationName: String? = null,
    val locationAddress: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
)
