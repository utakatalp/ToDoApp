package com.todoapp.mobile.data.source.remote.datasource.group

import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupMemberData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.AddMemberRequest
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.model.network.request.UpdateGroupRequest

interface GroupManagementRemoteDataSource {
    suspend fun createGroup(request: CreateGroupRequest): Result<GroupData>

    suspend fun getGroups(): Result<GroupSummaryDataList>

    suspend fun updateGroup(request: UpdateGroupRequest): Result<GroupData>

    suspend fun deleteGroup(id: Long): Result<Unit>

    suspend fun addMemberToGroup(request: AddMemberRequest): Result<GroupMemberData>

    suspend fun getGroupDetails(id: Long): Result<GroupData>

    suspend fun removeMember(userId: Long, groupId: Long): Result<Boolean>
}
