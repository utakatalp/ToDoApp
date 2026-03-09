package com.todoapp.mobile.data.source.local.datasource.usergroup

import com.todoapp.mobile.data.model.entity.usergroup.UserGroupEntity
import com.todoapp.mobile.data.source.local.dao.usergroup.UserGroupDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserGroupLocalDataSourceImpl @Inject constructor(
    private val dao: UserGroupDao
) : UserGroupLocalDataSource {

    override suspend fun upsert(membership: UserGroupEntity) {
        dao.upsert(membership)
    }

    override suspend fun insertAll(memberships: List<UserGroupEntity>) {
        dao.insertAll(memberships)
    }

    override suspend fun getGroupIdsOfUser(userId: Long): List<Long> {
        return dao.getGroupIdsOfUser(userId)
    }

    override fun observeMembership(
        userId: Long,
        groupId: Long
    ): Flow<UserGroupEntity?> {
        return dao.observeMembership(userId, groupId)
    }

    override fun observeMembersOfGroup(groupId: Long): Flow<List<UserGroupEntity>> {
        return dao.observeMembersOfGroup(groupId)
    }

    override fun observeGroupsOfUser(userId: Long): Flow<List<UserGroupEntity>> {
        return dao.observeGroupsOfUser(userId)
    }

    override suspend fun delete(membership: UserGroupEntity) {
        dao.delete(membership)
    }

    override suspend fun deleteByIds(userId: Long, groupId: Long) {
        dao.deleteByIds(userId, groupId)
    }

    override suspend fun deleteAllByGroupId(groupId: Long) {
        dao.deleteAllByGroupId(groupId)
    }

    override suspend fun updateRole(userId: Long, groupId: Long, role: String) {
        dao.updateRole(userId, groupId, role)
    }

    override suspend fun clear() {
        dao.clear()
    }
}
