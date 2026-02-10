package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.Serializable

@Serializable
data class TaskRequest(
    val title: String,
    val description: String?,
    val date: Long,
    val timeStart: Long,
    val timeEnd: Long,
    val isCompleted: Boolean,
    val isSecret: Boolean,
)
