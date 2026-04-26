package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.GroupMemberEntity
import com.todoapp.mobile.data.source.local.GroupMemberDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupMemberLocalDataSourceImpl
@Inject
constructor(
    private val groupMemberDao: GroupMemberDao,
) : GroupMemberLocalDataSource {
    override fun observeByGroupId(
        localGroupId: Long,
    ): Flow<List<GroupMemberEntity>> = groupMemberDao.getMembersByGroupId(
        localGroupId,
    )

    override suspend fun getByGroupIdOnce(localGroupId: Long): List<GroupMemberEntity> = groupMemberDao.getMembersByGroupIdOnce(
        localGroupId
    )

    override suspend fun replaceAll(
        localGroupId: Long,
        members: List<GroupMemberEntity>,
    ) {
        groupMemberDao.deleteByGroupId(localGroupId)
        groupMemberDao.insertAll(members)
    }

    override suspend fun deleteAll() = groupMemberDao.deleteAll()
}
