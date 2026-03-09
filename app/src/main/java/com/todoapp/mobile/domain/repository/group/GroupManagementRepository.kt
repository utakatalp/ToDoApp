package com.todoapp.mobile.domain.repository.group

import com.todoapp.mobile.data.model.entity.group.GroupEntity
import com.todoapp.mobile.data.model.entity.user.UserEntity
import com.todoapp.mobile.data.model.network.data.GroupSummaryData
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.model.network.request.UpdateGroupRequest
import kotlinx.coroutines.flow.Flow

interface GroupManagementRepository {

    suspend fun createGroup(request: CreateGroupRequest): Result<Unit>
    suspend fun updateGroupSummaries(): Result<Unit>
    fun observeGroupSummaries(): Flow<List<GroupSummaryData>>
    fun observeMemberCount(groupId: Long): Flow<Int>
    suspend fun updateGroup(request: UpdateGroupRequest): Result<Unit>
    suspend fun deleteGroup(groupId: Long): Result<Unit>
    suspend fun getGroup(groupId: Long): Result<GroupEntity>
    fun observeGroup(groupId: Long): Flow<GroupEntity?>
    suspend fun updateMember(userEntity: UserEntity): Result<Boolean>
    suspend fun deleteMember(userEntity: UserEntity): Result<Boolean>
    suspend fun syncGroupDetails(groupId: Long): Result<Unit>
    suspend fun clearGroups(): Result<Unit>
}
