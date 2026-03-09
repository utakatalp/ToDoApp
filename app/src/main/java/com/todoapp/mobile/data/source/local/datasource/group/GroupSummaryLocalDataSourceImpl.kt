package com.todoapp.mobile.data.source.local.datasource.group

import com.todoapp.mobile.data.model.entity.group.GroupSummaryEntity
import com.todoapp.mobile.data.source.local.dao.group.GroupSummaryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupSummaryLocalDataSourceImpl @Inject constructor(
    private val groupSummaryDao: GroupSummaryDao,
) : GroupSummaryLocalDataSource {
    override fun observeGroupSummaries(): Flow<List<GroupSummaryEntity>> {
        return groupSummaryDao.observeGroupSummaries()
    }

    override suspend fun getGroupSummaryById(id: Long): GroupSummaryEntity? {
        return groupSummaryDao.getGroupSummaryById(id)
    }

    override suspend fun insert(groupSummary: GroupSummaryEntity) {
        return groupSummaryDao.insert(groupSummary)
    }

    override suspend fun insertAll(groupSummaries: List<GroupSummaryEntity>) {
        return groupSummaryDao.insertAll(groupSummaries)
    }

    override suspend fun delete(id: Long) {
        groupSummaryDao.deleteById(id)
    }

    override suspend fun update(groupSummary: GroupSummaryEntity) {
        groupSummaryDao.update(groupSummary)
    }

    override fun observeGroupUserCount(id: Long): Flow<Int> {
        return groupSummaryDao.observeUserCount(id)
    }

    override suspend fun clear() {
        groupSummaryDao.clear()
    }
}
