package com.todoapp.mobile.data.source.local.datasource.usergroup

import com.todoapp.mobile.data.model.entity.usergroup.UserGroupEntity
import kotlinx.coroutines.flow.Flow

interface UserGroupLocalDataSource {

    suspend fun upsert(membership: UserGroupEntity)

    suspend fun insertAll(memberships: List<UserGroupEntity>)
    suspend fun getGroupIdsOfUser(userId: Long): List<Long>

    fun observeMembership(userId: Long, groupId: Long): Flow<UserGroupEntity?>

    fun observeMembersOfGroup(groupId: Long): Flow<List<UserGroupEntity>>

    fun observeGroupsOfUser(userId: Long): Flow<List<UserGroupEntity>>

    suspend fun delete(membership: UserGroupEntity)

    suspend fun deleteByIds(userId: Long, groupId: Long)

    suspend fun deleteAllByGroupId(groupId: Long)

    suspend fun updateRole(userId: Long, groupId: Long, role: String)

    suspend fun clear()
}
