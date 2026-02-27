package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun createGroup(request: CreateGroupRequest): Result<GroupData>

    suspend fun getGroups(): Result<GroupSummaryDataList>

    suspend fun deleteGroup(id: Long): Result<Unit>

    suspend fun deleteAllLocalGroups(): Result<Unit>

    fun observeAllGroups(): Flow<List<Group>>

    suspend fun reorderGroups(fromIndex: Int, toIndex: Int): Result<Unit>
}
