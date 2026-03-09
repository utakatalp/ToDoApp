package com.todoapp.mobile.data.model.entity.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todoapp.mobile.data.model.entity.SyncStatus

@Entity(tableName = "group_summaries")
data class GroupSummaryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "remote_id")
    val remoteId: Long?,

    @ColumnInfo(name = "member_count")
    val memberCount: Int,

    @ColumnInfo(name = "pending_task_count")
    val pendingTaskCount: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus,

    @ColumnInfo(name = "order_index")
    val orderIndex: Int,
)
