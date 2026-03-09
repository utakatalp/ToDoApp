package com.todoapp.mobile.data.source.remote.datasource.group

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupMemberData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.AddMemberRequest
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.model.network.request.UpdateGroupRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import javax.inject.Inject

class GroupManagementRemoteDataSourceImpl @Inject constructor(
    private val todoApi: ToDoApi
) : GroupManagementRemoteDataSource {

    override suspend fun createGroup(request: CreateGroupRequest): Result<GroupData> {
        return handleRequest { todoApi.createGroup(request) }
    }

    override suspend fun getGroups(): Result<GroupSummaryDataList> {
        return handleRequest { todoApi.getGroups() }
    }

    override suspend fun updateGroup(
        request: UpdateGroupRequest
    ): Result<GroupData> {
        return handleRequest { todoApi.updateGroup(request) }
    }

    override suspend fun deleteGroup(id: Long): Result<Unit> {
        return handleRequest { todoApi.deleteGroup(id) }
    }

    override suspend fun addMemberToGroup(request: AddMemberRequest): Result<GroupMemberData> {
        return handleRequest { todoApi.addMember(request) }
    }

    override suspend fun getGroupDetails(id: Long): Result<GroupData> {
        return handleRequest { todoApi.getGroupDetails(id) }
    }

    override suspend fun removeMember(userId: Long, groupId: Long): Result<Boolean> {
        return handleRequest { todoApi.removeMember(userId, groupId) }
    }
}
