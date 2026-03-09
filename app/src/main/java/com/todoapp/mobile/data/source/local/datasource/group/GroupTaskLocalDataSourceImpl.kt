package com.todoapp.mobile.data.source.local.datasource.group

import com.todoapp.mobile.data.model.entity.group.GroupTaskEntity
import com.todoapp.mobile.data.source.local.dao.group.GroupTaskDao
import com.todoapp.mobile.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class GroupTaskLocalDataSourceImpl @Inject constructor(
    private val groupTaskDao: GroupTaskDao
) : GroupTaskLocalDataSource {

    override suspend fun insert(taskEntity: GroupTaskEntity) {
        groupTaskDao.insert(taskEntity)
    }

    override fun observeTasks(groupId: Long): Flow<List<Task.Group>> {
        return groupTaskDao.getTasks(groupId).map { entities ->
            entities.map {
                Task.Group(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    date = LocalDate.ofEpochDay(it.date),
                    timeStart = LocalTime.ofSecondOfDay(it.timeStart.toLong()),
                    timeEnd = LocalTime.ofSecondOfDay(it.timeEnd.toLong()),
                    isCompleted = it.isCompleted,
                    isSecret = it.isSecret,
                    groupId = it.groupId,
                    orderIndex = it.orderIndex,
                    createdByUserId = it.createdByUserId,
                    createdByDisplayName = it.createdByDisplayName,
                    assignedToUserId = it.assignedToUserId!!,
                    assignedToDisplayName = it.assignedToDisplayName!!,
                    completedByUserId = it.completedByUserId,
                    completedByDisplayName = it.completedByDisplayName,
                )
            }
        }
    }

    override suspend fun getTaskCount(groupId: Long): Int {
        return groupTaskDao.getTaskCount(groupId)
    }

    override suspend fun getByRemoteId(remoteId: Long): GroupTaskEntity? {
        return groupTaskDao.getByRemoteId(remoteId)
    }
}
