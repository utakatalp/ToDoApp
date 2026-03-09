package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.model.network.request.UpdateGroupRequest

interface GroupRemoteDataSource {
    suspend fun createGroup(request: CreateGroupRequest): Result<GroupData>

    suspend fun updateGroup(request: UpdateGroupRequest): Result<GroupData>

    suspend fun getGroups(): Result<GroupSummaryDataList>

    suspend fun deleteGroup(id: Long): Result<Unit>

    suspend fun getGroupByRemoteId(remoteId: Long): Result<GroupData>
}
