package com.todoapp.mobile.data.mapper

import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.group.GroupEntity
import com.todoapp.mobile.data.model.entity.group.GroupSummaryEntity
import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupSummaryData
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest

fun GroupData.toEntity(orderIndex: Int): GroupEntity {
    return GroupEntity(
        remoteId = id,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        syncStatus = SyncStatus.SYNCED,
        orderIndex = orderIndex,
    )
}

fun CreateGroupRequest.toEntity(orderIndex: Int, syncStatus: SyncStatus = SyncStatus.PENDING_CREATE): GroupEntity {
    return GroupEntity(
        remoteId = null,
        name = name,
        description = description,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        syncStatus = syncStatus,
        orderIndex = orderIndex,
    )
}

fun GroupSummaryEntity.toData(name: String, description: String, role: String): GroupSummaryData {
    return GroupSummaryData(
        id = remoteId!!,
        name = name,
        description = description,
        role = role,
        memberCount = memberCount,
        pendingTaskCount = pendingTaskCount,
        createdAt = createdAt
    )
}
