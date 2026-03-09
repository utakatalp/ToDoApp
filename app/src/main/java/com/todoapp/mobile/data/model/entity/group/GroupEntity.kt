package com.todoapp.mobile.data.model.entity.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todoapp.mobile.data.model.entity.SyncStatus

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "remote_id") val remoteId: Long?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(
        name = "sync_status",
        defaultValue = "SYNCED"
    ) val syncStatus: SyncStatus,
    @ColumnInfo(name = "order_index", defaultValue = "0") val orderIndex: Int,
)
