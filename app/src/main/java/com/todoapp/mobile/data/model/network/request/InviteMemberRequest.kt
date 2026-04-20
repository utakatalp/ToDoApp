package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InviteMemberRequest(
    @SerialName("groupId") val groupId: Long,
    @SerialName("email") val email: String,
)
