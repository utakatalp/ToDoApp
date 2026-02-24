package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.GroupEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupLocalDataSourceImpl @Inject constructor(
    private val groupDao: GroupDao,
) : GroupLocalDataSource {
    override fun observeAll(): Flow<List<GroupEntity>> {
        return groupDao.getAllGroups()
    }

    override suspend fun insert(group: GroupEntity) {
        groupDao.insert(group)
    }

    override suspend fun delete(group: GroupEntity) {
        groupDao.delete(group)
    }

    override suspend fun deleteAll(group: GroupEntity) {
        groupDao.deleteAll()
    }

    override suspend fun update(group: GroupEntity) {
        groupDao.update(group)
    }

    override suspend fun getGroupById(id: Long): GroupEntity? = groupDao.getGroupById(id)

    override suspend fun getGroupByName(name: String): GroupEntity = groupDao.getGroupByName(name)

    override suspend fun updateOrderIndex(id: Long, orderIndex: Int) {
        groupDao.updateOrderIndex(id = id, orderIndex = orderIndex)
    }

    override suspend fun updateOrderIndices(updates: List<Pair<Long, Int>>) {
        for ((id, orderIndex) in updates) {
            groupDao.updateOrderIndex(id = id, orderIndex = orderIndex)
        }
    }

    override fun getAllGroupsOrdered(): Flow<List<GroupEntity>> {
        return groupDao.getAllGroupsOrdered()
    }
}
