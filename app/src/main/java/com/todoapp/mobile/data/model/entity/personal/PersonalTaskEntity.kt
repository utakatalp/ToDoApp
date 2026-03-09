package com.todoapp.mobile.data.model.entity.personal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todoapp.mobile.data.model.entity.SyncStatus

@Entity(tableName = "personal_tasks")
data class PersonalTaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "remote_id") val remoteId: Long? = null,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "time_start") val timeStart: Int,
    @ColumnInfo(name = "time_end") val timeEnd: Int,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
    @ColumnInfo(name = "is_secret") val isSecret: Boolean = false,
    @ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = 0L,
    @ColumnInfo(name = "order_index", defaultValue = "0") val orderIndex: Int = 0,
    @ColumnInfo(
        name = "sync_status",
        defaultValue = "PENDING_CREATE"
    )
    val syncStatus: SyncStatus = SyncStatus.PENDING_CREATE,
)
