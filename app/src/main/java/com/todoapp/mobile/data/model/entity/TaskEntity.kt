package com.todoapp.mobile.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "time_start") val timeStart: Long,
    @ColumnInfo(name = "time_end") val timeEnd: Long,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
    @ColumnInfo(name = "is_secret") val isSecret: Boolean = false,
    @ColumnInfo(name = "remote_id") val remoteId: Long? = null,

    // --- Remote user fields (flattened from TaskUserData) ---
    @ColumnInfo(name = "created_by_user_id") val createdByUserId: Long? = null,
    @ColumnInfo(name = "created_by_display_name") val createdByDisplayName: String? = null,

    @ColumnInfo(name = "assigned_to_user_id") val assignedToUserId: Long? = null,
    @ColumnInfo(name = "assigned_to_display_name") val assignedToDisplayName: String? = null,

    @ColumnInfo(name = "completed_by_user_id") val completedByUserId: Long? = null,
    @ColumnInfo(name = "completed_by_display_name") val completedByDisplayName: String? = null,

    // --- Remote timestamps ---
    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = 0L,

    @ColumnInfo(
        name = "sync_status",
        defaultValue = "PENDING_CREATE"
    )
    val syncStatus: SyncStatus = SyncStatus.PENDING_CREATE,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "order_index", defaultValue = "0") val orderIndex: Int = 0,
    @ColumnInfo(name = "family_group_id") val familyGroupId: Long? = null,

)

enum class SyncStatus {
    PENDING_CREATE,
    PENDING_UPDATE,
    PENDING_DELETE,
    SYNCED,
}
