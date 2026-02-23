package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

interface GroupLocalDataSource {
    fun observeAll(): Flow<List<GroupEntity>>

    suspend fun insert(group: GroupEntity)

    suspend fun delete(group: GroupEntity)

    suspend fun update(group: GroupEntity)

    suspend fun getGroupById(id: Long): GroupEntity?

    suspend fun getGroupByName(name: String): GroupEntity

    suspend fun updateOrderIndex(id: Long, orderIndex: Int)

    suspend fun updateOrderIndices(updates: List<Pair<Long, Int>>)

    fun getAllGroupsOrdered(): Flow<List<GroupEntity>>
}
