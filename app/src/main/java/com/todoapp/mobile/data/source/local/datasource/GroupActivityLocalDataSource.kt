package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.GroupActivityEntity
import kotlinx.coroutines.flow.Flow

interface GroupActivityLocalDataSource {
    fun observeByGroupId(localGroupId: Long): Flow<List<GroupActivityEntity>>
    suspend fun getByGroupIdOnce(localGroupId: Long): List<GroupActivityEntity>
    suspend fun replaceAll(localGroupId: Long, activities: List<GroupActivityEntity>)
}
