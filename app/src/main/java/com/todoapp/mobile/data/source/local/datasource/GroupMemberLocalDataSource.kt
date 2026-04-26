package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.GroupMemberEntity
import kotlinx.coroutines.flow.Flow

interface GroupMemberLocalDataSource {
    fun observeByGroupId(localGroupId: Long): Flow<List<GroupMemberEntity>>

    suspend fun getByGroupIdOnce(localGroupId: Long): List<GroupMemberEntity>

    suspend fun replaceAll(
        localGroupId: Long,
        members: List<GroupMemberEntity>,
    )

    suspend fun deleteAll()
}
