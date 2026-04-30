package com.todoapp.mobile.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class GroupMember(
    val userId: Long,
    val displayName: String,
    val email: String,
    val avatarUrl: String?,
    val role: String,
    val joinedAt: Long,
    val pendingTaskCount: Int = 0,
)
