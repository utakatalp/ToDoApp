package com.todoapp.mobile.data.source.local.datasource.group

import com.todoapp.mobile.data.model.entity.group.GroupEntity
import com.todoapp.mobile.data.source.local.dao.group.GroupDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupsLocalDataSourceImpl @Inject constructor(
    private val dao: GroupDao
) : GroupsLocalDataSource {

    override suspend fun upsert(group: GroupEntity) {
        dao.upsert(group)
    }

    override suspend fun insert(group: GroupEntity): Long {
        return dao.insert(group)
    }

    override suspend fun insertAll(groups: List<GroupEntity>) {
        dao.insertAll(groups)
    }

    override suspend fun getById(id: Long): GroupEntity? {
        return dao.getById(id)
    }

    override fun observeById(id: Long): Flow<GroupEntity?> {
        return dao.observeById(id)
    }

    override suspend fun getByRemoteId(remoteId: Long): GroupEntity? {
        return dao.getByRemoteId(remoteId)
    }

    override fun observeByRemoteId(remoteId: Long): Flow<GroupEntity?> {
        return dao.observeByRemoteId(remoteId)
    }

    override fun observeAll(): Flow<List<GroupEntity>> {
        return dao.observeAll()
    }

    override suspend fun getAll(): List<GroupEntity> {
        return dao.getAll()
    }

    override suspend fun delete(group: GroupEntity) {
        dao.delete(group)
    }

    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun deleteByRemoteId(remoteId: Long) {
        dao.deleteByRemoteId(remoteId)
    }

    override suspend fun deleteAll(ids: List<Long>) {
        dao.deleteAll(ids)
    }

    override suspend fun getOrderIndex(): Int {
        return dao.getOrderIndex()
    }

    override suspend fun updateOrderIndex(groupId: Long, orderIndex: Int) {
        dao.updateOrderIndex(groupId, orderIndex)
    }

    override suspend fun clear() {
        dao.clear()
    }
}
