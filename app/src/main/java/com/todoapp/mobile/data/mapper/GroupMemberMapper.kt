package com.todoapp.mobile.data.mapper

import com.todoapp.mobile.data.model.entity.GroupMemberEntity
import com.todoapp.mobile.data.model.network.data.GroupMemberData
import com.todoapp.mobile.domain.model.GroupMember

fun GroupMemberEntity.toDomain(): GroupMember = GroupMember(
    userId = userId,
    displayName = displayName,
    email = email,
    avatarUrl = avatarUrl,
    role = role,
    joinedAt = joinedAt,
    pendingTaskCount = pendingTaskCount,
)

fun GroupMemberData.toEntity(localGroupId: Long): GroupMemberEntity = GroupMemberEntity(
    userId = userId,
    localGroupId = localGroupId,
    displayName = displayName,
    email = email,
    avatarUrl = avatarUrl,
    role = role,
    joinedAt = joinedAt,
)
