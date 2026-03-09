package com.todoapp.mobile.data.model.entity.usergroup

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "user_group_memberships",
    primaryKeys = ["user_id", "group_id"],
)
data class UserGroupEntity(
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "group_id") val groupId: Long,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "joined_at") val joinedAt: Long,
)
