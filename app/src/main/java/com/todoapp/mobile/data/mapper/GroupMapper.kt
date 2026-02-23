package com.todoapp.mobile.data.mapper

import com.todoapp.mobile.data.model.entity.GroupEntity
import com.todoapp.mobile.domain.model.Group

fun GroupEntity.toDomain(): Group =
    Group(
        id = id,
        name = name,
        description = description,
        remoteId = remoteId,
        createdAt = createdAt,
        orderIndex = orderIndex,
        role = role,
        memberCount = memberCount,
        pendingTaskCount = pendingTaskCount,
    )

fun Group.toEntity(): GroupEntity =
    GroupEntity(
        id = id,
        name = name,
        description = description,
        remoteId = remoteId,
        createdAt = createdAt,
        orderIndex = orderIndex,
        role = role,
        memberCount = memberCount,
        pendingTaskCount = pendingTaskCount,
    )
