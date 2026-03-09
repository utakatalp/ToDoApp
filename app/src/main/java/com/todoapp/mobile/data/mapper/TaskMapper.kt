package com.todoapp.mobile.data.mapper

import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.group.GroupTaskEntity
import com.todoapp.mobile.data.model.entity.personal.PersonalTaskEntity
import com.todoapp.mobile.data.model.network.data.GroupTaskData
import com.todoapp.mobile.data.model.network.data.PersonalTaskData
import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.data.model.network.request.TaskUpdateRequest
import com.todoapp.mobile.domain.model.AlarmItem
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.Task.Group
import com.todoapp.mobile.domain.model.Task.Personal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun Task.Personal.toEntity(orderIndex: Int): PersonalTaskEntity {
    return PersonalTaskEntity(
        id = id,
        remoteId = null,
        title = title,
        description = description,
        date = date.toEpochDay(),
        timeStart = timeStart.toSecondOfDay(),
        timeEnd = timeEnd.toSecondOfDay(),
        isCompleted = isCompleted,
        isSecret = isSecret,
        createdAt = System.currentTimeMillis(),
        syncStatus = SyncStatus.PENDING_CREATE,
        orderIndex = orderIndex
    )
}

fun Task.Group.toEntity(orderIndex: Int): GroupTaskEntity {
    return GroupTaskEntity(
        id = id,
        remoteId = null,
        title = title,
        description = description,
        date = date.toEpochDay(),
        timeStart = timeStart.toSecondOfDay(),
        timeEnd = timeEnd.toSecondOfDay(),
        isCompleted = isCompleted,
        isSecret = isSecret,
        createdAt = System.currentTimeMillis(),
        syncStatus = SyncStatus.PENDING_CREATE,
        orderIndex = orderIndex,
        groupId = groupId,
        assignedToUserId = assignedToUserId,
        assignedToDisplayName = assignedToDisplayName,
        createdByUserId = createdByUserId,
        createdByDisplayName = createdByDisplayName,
        completedByUserId = completedByUserId,
        completedByDisplayName = completedByDisplayName,
    )
}

fun Task.toUpdateRequest(remoteId: Long, assignedToUserId: Long? = null): TaskUpdateRequest {
    return when (this) {
        is Personal -> TaskUpdateRequest(
            id = remoteId,
            title = title,
            description = description,
            date = date.toEpochDay(),
            timeStart = timeStart.toSecondOfDay(),
            timeEnd = timeEnd.toSecondOfDay(),
            isCompleted = isCompleted,
            isSecret = isSecret,
            assignedToUserId = null,
        )
        is Group -> TaskUpdateRequest(
            id = remoteId,
            title = title,
            description = description,
            date = date.toEpochDay(),
            timeStart = timeStart.toSecondOfDay(),
            timeEnd = timeEnd.toSecondOfDay(),
            isCompleted = isCompleted,
            isSecret = isSecret,
            assignedToUserId = assignedToUserId,
        )
    }
}

fun Task.toRequest(): TaskRequest {
    return when (this) {
        is Personal -> TaskRequest(
            title = title,
            description = description,
            date = date.toEpochDay(),
            timeStart = timeStart.toSecondOfDay(),
            timeEnd = timeEnd.toSecondOfDay(),
            isCompleted = isCompleted,
            isSecret = isSecret,
            groupId = null,
            assignedToUserId = null,
        )

        is Group -> TaskRequest(
            title = title,
            description = description,
            date = date.toEpochDay(),
            timeStart = timeStart.toSecondOfDay(),
            timeEnd = timeEnd.toSecondOfDay(),
            isCompleted = isCompleted,
            isSecret = isSecret,
            groupId = groupId,
            assignedToUserId = assignedToUserId,
        )
    }
}

fun PersonalTaskEntity.toAlarmItem(remindBeforeMinutes: Long = 0): AlarmItem {
    return AlarmItem(
        time = LocalDateTime.of(
            LocalDate.ofEpochDay(date),
            LocalTime.ofSecondOfDay(timeStart.toLong()).minusMinutes(remindBeforeMinutes)
        ),
        message = title,
        minutesBefore = remindBeforeMinutes,
    )
}

fun TaskRequest.toPersonalEntity(
    orderIndex: Int,
    remoteId: Long? = null,
    createdAt: Long = System.currentTimeMillis(),
    syncStatus: SyncStatus = SyncStatus.PENDING_CREATE,
): PersonalTaskEntity {
    return PersonalTaskEntity(
        remoteId = remoteId,
        title = title,
        description = description,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isCompleted = isCompleted,
        isSecret = isSecret,
        createdAt = createdAt,
        syncStatus = syncStatus,
        orderIndex = orderIndex,
    )
}

fun TaskRequest.toGroupEntity(
    orderIndex: Int,
    remoteId: Long? = null,
    createdAt: Long = System.currentTimeMillis(),
    syncStatus: SyncStatus = SyncStatus.PENDING_CREATE,
    assignedToDisplayName: String,
    createdByUserId: Long,
    createdByDisplayName: String,
    completedByUserId: Long? = null,
    completedByDisplayName: String? = null,
): GroupTaskEntity {
    val safeGroupId = requireNotNull(groupId) { "groupId must not be null for GroupTaskEntity" }
    val safeAssignedToUserId =
        requireNotNull(assignedToUserId) { "assignedToUserId must not be null for GroupTaskEntity" }

    return GroupTaskEntity(
        remoteId = remoteId,
        title = title,
        description = description,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isCompleted = isCompleted,
        isSecret = isSecret,
        createdAt = createdAt,
        syncStatus = syncStatus,
        orderIndex = orderIndex,
        groupId = safeGroupId,
        assignedToUserId = safeAssignedToUserId,
        assignedToDisplayName = assignedToDisplayName,
        createdByUserId = createdByUserId,
        createdByDisplayName = createdByDisplayName,
        completedByUserId = completedByUserId,
        completedByDisplayName = completedByDisplayName,
    )
}

fun PersonalTaskData.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED, orderIndex: Int): PersonalTaskEntity {
    return PersonalTaskEntity(
        remoteId = id,
        title = title,
        description = desc,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isCompleted = isCompleted,
        syncStatus = syncStatus,
        orderIndex = orderIndex,
    )
}
fun PersonalTaskEntity.toTaskPersonal(): Task.Personal {
    return Task.Personal(
        id = id,
        title = title,
        description = description,
        date = LocalDate.ofEpochDay(date),
        timeStart = LocalTime.ofSecondOfDay(timeStart.toLong()),
        timeEnd = LocalTime.ofSecondOfDay(timeEnd.toLong()),
        isCompleted = isCompleted,
        isSecret = isSecret,
        orderIndex = orderIndex,
    )
}
fun GroupTaskData.toEntity(
    syncStatus: SyncStatus = SyncStatus.SYNCED,
    orderIndex: Int,
    groupId: Long
): GroupTaskEntity {
    return GroupTaskEntity(
        remoteId = id,
        groupId = groupId,
        title = title,
        description = desc,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isCompleted = isCompleted,
        syncStatus = syncStatus,
        orderIndex = orderIndex,
        createdByUserId = createdBy.userId,
        createdByDisplayName = createdBy.displayName,
        assignedToUserId = assignedTo.userId,
        assignedToDisplayName = assignedTo.displayName,
        completedByUserId = completedBy?.userId,
        completedByDisplayName = completedBy?.displayName
    )
}
