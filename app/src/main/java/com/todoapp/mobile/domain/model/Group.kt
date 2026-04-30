package com.todoapp.mobile.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Group(
    val id: Long = 0L,
    val name: String,
    val description: String,
    val remoteId: Long? = null,
    val createdAt: Long,
    val orderIndex: Int = 0,
    val role: String = "",
    val memberCount: Int = 0,
    val pendingTaskCount: Int = 0,
    val avatarUrl: String? = null,
)
