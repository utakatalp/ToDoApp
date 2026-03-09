package com.todoapp.mobile.data.source.local.datasource.group

import com.todoapp.mobile.data.model.entity.group.GroupSummaryEntity
import kotlinx.coroutines.flow.Flow

interface GroupSummaryLocalDataSource {
    fun observeGroupSummaries(): Flow<List<GroupSummaryEntity>>
    suspend fun getGroupSummaryById(id: Long): GroupSummaryEntity?
    suspend fun insert(groupSummary: GroupSummaryEntity)
    suspend fun insertAll(groupSummaries: List<GroupSummaryEntity>)
    suspend fun delete(id: Long)
    suspend fun update(groupSummary: GroupSummaryEntity)
    fun observeGroupUserCount(id: Long): Flow<Int>
    suspend fun clear()
}
