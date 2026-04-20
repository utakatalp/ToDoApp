package com.todoapp.mobile.domain.model

data class GroupActivity(
    val id: Long,
    val type: String,
    val actorName: String,
    val actorAvatarUrl: String?,
    val description: String,
    val timestamp: Long,
    val taskTitle: String?,
)
