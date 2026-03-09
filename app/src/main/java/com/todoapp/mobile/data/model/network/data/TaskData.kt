package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.Serializable

@Serializable
data class TaskData(
    val id: Long,
    val title: String,
    val description: String?,
    val date: Long,
    val timeStart: Int,
    val timeEnd: Int,
    val isCompleted: Boolean,
    val isSecret: Boolean,
    val createdBy: TaskUserData?,
    val assignedTo: TaskUserData?,
    val completedBy: TaskUserData?,
    val createdAt: Long,
)
data class PersonalTaskData(
    val id: Long,
    val title: String,
    val desc: String?,
    val date: Long,
    val timeStart: Int,
    val timeEnd: Int,
    val isCompleted: Boolean,
    val isSecret: Boolean,
    val createdAt: Long,
)
data class GroupTaskData(
    val id: Long,
    val title: String,
    val desc: String?,
    val date: Long,
    val timeStart: Int,
    val timeEnd: Int,
    val isCompleted: Boolean,
    val isSecret: Boolean,
    val createdBy: TaskUserData,
    val assignedTo: TaskUserData,
    val completedBy: TaskUserData?,
    val createdAt: Long,
)

fun TaskData.toPersonalTaskData(): PersonalTaskData {
    return PersonalTaskData(
        id = id,
        title = title,
        desc = description,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isCompleted = isCompleted,
        isSecret = isSecret,
        createdAt = createdAt
    )
}

fun TaskData.toGroupTaskData(): GroupTaskData {
    return GroupTaskData(
        id = id,
        title = title,
        desc = description,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isCompleted = isCompleted,
        isSecret = isSecret,
        createdAt = createdAt,
        createdBy = createdBy!!,
        assignedTo = assignedTo!!,
        completedBy = completedBy,
    )
}

@Serializable
data class TaskUserData(
    val userId: Long,
    val displayName: String
)

@Serializable
data class TaskListData(
    val tasks: List<TaskData>,
    val count: Int
)
