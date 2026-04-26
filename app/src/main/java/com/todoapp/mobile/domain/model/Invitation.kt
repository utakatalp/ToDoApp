package com.todoapp.mobile.domain.model

data class Invitation(
    val id: Long,
    val groupId: Long,
    val groupName: String,
    val groupAvatarUrl: String?,
    val inviterUserId: Long,
    val inviterName: String,
    val inviterAvatarUrl: String?,
    val inviteeEmail: String,
    val createdAt: Long,
)
