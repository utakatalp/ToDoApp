package com.todoapp.mobile.data.source.local.datasource.group

import com.todoapp.mobile.data.model.entity.group.GroupEntity
import kotlinx.coroutines.flow.Flow

interface GroupsLocalDataSource {
    suspend fun upsert(group: GroupEntity)

    suspend fun insert(group: GroupEntity): Long

    suspend fun insertAll(groups: List<GroupEntity>)

    suspend fun getById(id: Long): GroupEntity?

    fun observeById(id: Long): Flow<GroupEntity?>

    suspend fun getByRemoteId(remoteId: Long): GroupEntity?

    fun observeByRemoteId(remoteId: Long): Flow<GroupEntity?>

    fun observeAll(): Flow<List<GroupEntity>>

    suspend fun getAll(): List<GroupEntity>

    suspend fun delete(group: GroupEntity)

    suspend fun deleteById(id: Long)

    suspend fun deleteByRemoteId(remoteId: Long)

    suspend fun deleteAll(ids: List<Long>)

    suspend fun getOrderIndex(): Int

    suspend fun updateOrderIndex(groupId: Long, orderIndex: Int)

    suspend fun clear()
}
