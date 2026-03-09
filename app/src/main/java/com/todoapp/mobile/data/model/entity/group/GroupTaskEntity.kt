package com.todoapp.mobile.data.model.entity.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todoapp.mobile.data.model.entity.SyncStatus

@Entity(tableName = "group_tasks")
data class GroupTaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "remote_id") val remoteId: Long? = null,
    @ColumnInfo(name = "group_id") val groupId: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "time_start") val timeStart: Int,
    @ColumnInfo(name = "time_end") val timeEnd: Int,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
    @ColumnInfo(name = "is_secret") val isSecret: Boolean = false,

    @ColumnInfo(name = "created_by_user_id") val createdByUserId: Long,
    @ColumnInfo(name = "created_by_display_name") val createdByDisplayName: String,

    @ColumnInfo(name = "assigned_to_user_id") val assignedToUserId: Long? = null,
    @ColumnInfo(name = "assigned_to_display_name") val assignedToDisplayName: String? = null,

    @ColumnInfo(name = "completed_by_user_id") val completedByUserId: Long? = null,
    @ColumnInfo(name = "completed_by_display_name") val completedByDisplayName: String? = null,

    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = 0L,

    @ColumnInfo(name = "order_index", defaultValue = "0") val orderIndex: Int = 0,
    @ColumnInfo(
        name = "sync_status",
        defaultValue = "PENDING_CREATE"
    )
    val syncStatus: SyncStatus = SyncStatus.PENDING_CREATE,
)
