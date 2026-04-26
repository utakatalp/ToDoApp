package com.todoapp.mobile.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "task_daily_completions",
    primaryKeys = ["task_id", "date"],
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["date"])],
)
data class TaskDailyCompletionEntity(
    @ColumnInfo(name = "task_id") val taskId: Long,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "completed_at") val completedAt: Long,
)
