package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.Serializable

@Serializable
data class InvitationData(
    val id: Long,
    val groupId: Long,
    val groupName: String,
    val groupAvatarUrl: String? = null,
    val inviterUserId: Long,
    val inviterName: String,
    val inviterAvatarUrl: String? = null,
    val inviteeEmail: String,
    val status: String,
    val createdAt: Long,
    val respondedAt: Long? = null,
)

@Serializable
data class InvitationListData(
    val items: List<InvitationData>,
    val count: Int,
)
