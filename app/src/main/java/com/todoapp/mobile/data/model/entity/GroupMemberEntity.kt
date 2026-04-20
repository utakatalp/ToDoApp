package com.todoapp.mobile.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "group_members",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["local_group_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("local_group_id")],
)
data class GroupMemberEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "local_group_id") val localGroupId: Long,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String? = null,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "joined_at") val joinedAt: Long,
    @ColumnInfo(name = "pending_task_count", defaultValue = "0") val pendingTaskCount: Int = 0,
)
