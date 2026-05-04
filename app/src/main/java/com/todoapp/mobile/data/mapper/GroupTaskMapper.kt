package com.todoapp.mobile.data.mapper

import com.todoapp.mobile.data.model.entity.GroupTaskEntity
import com.todoapp.mobile.data.model.network.data.GroupMemberData
import com.todoapp.mobile.data.model.network.data.GroupTaskData
import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.domain.model.GroupMember
import com.todoapp.mobile.domain.model.GroupTask
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun TaskData.toGroupTask(): GroupTask = GroupTask(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = priority,
    dueDate =
    LocalDate
        .ofEpochDay(date)
        .atTime(LocalTime.ofSecondOfDay(timeStart))
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli(),
    assignee =
    assignedTo?.let { user ->
        GroupMember(
            userId = user.userId,
            displayName = user.displayName,
            email = "",
            avatarUrl = null,
            role = "",
            joinedAt = 0L,
        )
    },
    photoUrls = photoUrls,
    locationName = locationName,
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLng = locationLng,
)

fun GroupTaskData.toDomain(): GroupTask = GroupTask(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = priority,
    dueDate = dueDate,
    assignee = assignee?.toDomain(),
    photoUrls = photoUrls,
    locationName = locationName,
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLng = locationLng,
)

fun GroupMemberData.toDomain(): GroupMember = GroupMember(
    userId = userId,
    displayName = displayName,
    email = email,
    avatarUrl = avatarUrl,
    role = role,
    joinedAt = joinedAt,
)

fun GroupTaskEntity.toDomain(): GroupTask = GroupTask(
    id = remoteId ?: id,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = priority,
    dueDate = dueDate,
    assignee =
    if (assigneeUserId != null && assigneeDisplayName != null) {
        GroupMember(
            userId = assigneeUserId,
            displayName = assigneeDisplayName,
            email = "",
            avatarUrl = assigneeAvatarUrl,
            role = "",
            joinedAt = 0L,
        )
    } else {
        null
    },
    photoUrls = photoUrls.split(',').filter { it.isNotBlank() },
    groupId = remoteGroupId,
    locationName = locationName,
    locationAddress = locationAddress,
    locationLat = locationLat,
    locationLng = locationLng,
)

fun GroupTask.toEntity(
    localGroupId: Long,
    remoteGroupId: Long,
): GroupTaskEntity = GroupTaskEntity(
    remoteId = id,
    localGroupId = localGroupId,
    remoteGroupId = remoteGroupId,
    title = title,
    description = description,
    isCompleted = isCompleted,
    priority = priority,
    dueDate = dueDate,
    assigneeUserId = assignee?.userId,
    assigneeDisplayName = assignee?.displayName,
    assigneeAvatarUrl = assignee?.avatarUrl,
    photoUrls = photoUrls.joinToString(","),
    locationLat = locationLat,
    locationLng = locationLng,
    locationName = locationName,
    locationAddress = locationAddress,
)
