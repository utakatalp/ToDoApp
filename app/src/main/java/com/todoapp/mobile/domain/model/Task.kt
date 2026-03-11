package com.todoapp.mobile.domain.model

import java.time.LocalDate
import java.time.LocalTime

sealed class Task(
    open val id: Long,
    open val remoteId: Long?,
    open val title: String,
    open val description: String?,
    open val date: LocalDate,
    open val timeStart: LocalTime,
    open val timeEnd: LocalTime,
    open val isCompleted: Boolean,
    open val isSecret: Boolean,
    open val orderIndex: Int,
) {
    data class Personal(
        override val id: Long,
        override val remoteId: Long?,
        override val title: String,
        override val description: String?,
        override val date: LocalDate,
        override val timeStart: LocalTime,
        override val timeEnd: LocalTime,
        override val isCompleted: Boolean,
        override val isSecret: Boolean,
        override val orderIndex: Int,
    ) : Task(
        id = id,
        remoteId = remoteId,
        title = title,
        description = description,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isCompleted = isCompleted,
        isSecret = isSecret,
        orderIndex = orderIndex
    )

    data class Group(
        override val id: Long,
        override val remoteId: Long?,
        override val title: String,
        override val description: String?,
        override val date: LocalDate,
        override val timeStart: LocalTime,
        override val timeEnd: LocalTime,
        override val isCompleted: Boolean,
        override val isSecret: Boolean,
        override val orderIndex: Int,
        val groupId: Long,
        val assignedToUserId: Long,
        val assignedToDisplayName: String,
        val createdByUserId: Long,
        val createdByDisplayName: String,
        val completedByUserId: Long?,
        val completedByDisplayName: String?,
    ) : Task(
        id = id,
        remoteId = remoteId,
        title = title,
        description = description,
        date = date,
        timeStart = timeStart,
        timeEnd = timeEnd,
        isCompleted = isCompleted,
        isSecret = isSecret,
        orderIndex = orderIndex
    )
}
