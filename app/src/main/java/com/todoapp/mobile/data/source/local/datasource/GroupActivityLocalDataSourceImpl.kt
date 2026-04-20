package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.GroupActivityEntity
import com.todoapp.mobile.data.source.local.GroupActivityDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupActivityLocalDataSourceImpl @Inject constructor(
    private val groupActivityDao: GroupActivityDao,
) : GroupActivityLocalDataSource {

    override fun observeByGroupId(localGroupId: Long): Flow<List<GroupActivityEntity>> =
        groupActivityDao.getActivitiesByGroupId(localGroupId)

    override suspend fun getByGroupIdOnce(localGroupId: Long): List<GroupActivityEntity> =
        groupActivityDao.getActivitiesByGroupIdOnce(localGroupId)

    override suspend fun replaceAll(localGroupId: Long, activities: List<GroupActivityEntity>) {
        groupActivityDao.deleteByGroupId(localGroupId)
        groupActivityDao.insertAll(activities)
    }
}
