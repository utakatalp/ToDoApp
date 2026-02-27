package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserData,
)

@Serializable
data class UserData(
    val id: Long,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
    val emailVerified: Boolean,
    val providers: List<String>,
    val createdAt: String,
)

@Serializable
data class RefreshTokenData(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

@Serializable
data class GroupSummaryDataList(
    @SerialName("familyGroups") val groups: List<GroupSummaryData>,
    @SerialName("count") val count: Int
)

@Serializable
data class GroupSummaryData(

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String,

    @SerialName("role")
    val role: String,

    @SerialName("memberCount")
    val memberCount: Int,

    @SerialName("pendingTaskCount")
    val pendingTaskCount: Int,

    @SerialName("createdAt")
    val createdAt: Long
)

@Serializable
data class GroupData(

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String,

    @SerialName("createdAt")
    val createdAt: Long,

    @SerialName("updatedAt")
    val updatedAt: Long,

    @SerialName("members")
    val members: List<GroupMemberData>
)

@Serializable
data class GroupMemberData(

    @SerialName("userId")
    val userId: Long,

    @SerialName("displayName")
    val displayName: String,

    @SerialName("email")
    val email: String,

    @SerialName("avatarUrl")
    val avatarUrl: String?,

    @SerialName("role")
    val role: String,

    @SerialName("joinedAt")
    val joinedAt: Long
)
