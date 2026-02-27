package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import javax.inject.Inject

class GroupRemoteDataSourceImpl @Inject constructor(
    private val todoApi: ToDoApi
) : GroupRemoteDataSource {
    override suspend fun createGroup(request: CreateGroupRequest): Result<GroupData> {
        return handleRequest { todoApi.createGroup(request) }
    }

    override suspend fun getGroups(): Result<GroupSummaryDataList> {
        return handleRequest { todoApi.getGroups() }
    }

    override suspend fun deleteGroup(id: Long): Result<Unit> {
        return handleRequest { todoApi.deleteGroup(id) }
    }
}
