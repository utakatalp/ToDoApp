package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.Serializable

@Serializable
data class TaskRequest(
    val title: String,
    val description: String?,
    val date: Long,
    val timeStart: Int,
    val timeEnd: Int,
    val isCompleted: Boolean,
    val isSecret: Boolean,
    val groupId: Long? = null,
    val assignedToUserId: Long? = null,
)

@Serializable
data class TaskUpdateRequest(
    val id: Long,
    val title: String,
    val description: String?,
    val date: Long,
    val timeStart: Int,
    val timeEnd: Int,
    val isCompleted: Boolean,
    val isSecret: Boolean,
    val assignedToUserId: Long? = null,
)
