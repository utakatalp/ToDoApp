package com.todoapp.mobile.data.repository.group

import android.util.Log
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.data.mapper.toData
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.group.GroupEntity
import com.todoapp.mobile.data.model.entity.group.GroupSummaryEntity
import com.todoapp.mobile.data.model.entity.user.UserEntity
import com.todoapp.mobile.data.model.entity.usergroup.UserGroupEntity
import com.todoapp.mobile.data.model.network.data.GroupMemberData
import com.todoapp.mobile.data.model.network.data.GroupSummaryData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.model.network.request.UpdateGroupRequest
import com.todoapp.mobile.data.repository.DataStoreHelper
import com.todoapp.mobile.data.source.local.datasource.group.GroupSummaryLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.group.GroupsLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.user.UserLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.usergroup.UserGroupLocalDataSource
import com.todoapp.mobile.data.source.remote.datasource.GroupRemoteDataSource
import com.todoapp.mobile.data.source.remote.datasource.group.GroupSummaryRemoteDataSource
import com.todoapp.mobile.domain.observer.ConnectivityObserver
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.domain.repository.group.GroupManagementRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class GroupManagementRepositoryImpl @Inject constructor(
    private val groupRemoteDataSource: GroupRemoteDataSource,
    private val groupLocalDataSource: GroupsLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val userGroupLocalDataSource: UserGroupLocalDataSource,
    private val connectivityObserver: ConnectivityObserver,
    private val userRepository: UserRepository,
    private val groupSummaryLocalDataSource: GroupSummaryLocalDataSource,
    private val groupSummaryRemoteDataSource: GroupSummaryRemoteDataSource,
    private val dataSourceHelper: DataStoreHelper,
) : GroupManagementRepository {
    private companion object {
        private const val TAG = "GroupManagementRepository"
    }

    override suspend fun createGroup(request: CreateGroupRequest): Result<Unit> {
        val orderIndex = groupLocalDataSource.getOrderIndex()
        val result = groupRemoteDataSource.createGroup(request)
        return result.fold(
            onSuccess = {
                groupLocalDataSource.insert(it.toEntity(orderIndex))
                Result.success(Unit)
            },
            onFailure = {
                groupLocalDataSource.insert(
                    request.toEntity(
                        syncStatus = SyncStatus.PENDING_CREATE,
                        orderIndex = orderIndex
                    )
                )
                Result.failure(DomainException.fromThrowable(it))
            }
        )
    }

    override suspend fun updateGroupSummaries(): Result<Unit> {
        Log.d(TAG, "updateGroupSummaries() - START")
        if (!connectivityObserver.isConnected.value) return Result.failure(DomainException.NoInternet())

        return runCatching {
            val remoteGroups = groupSummaryRemoteDataSource.getGroups().fold(
                onSuccess = { it },
                onFailure = { throw DomainException.fromThrowable(it) },
            )
            val localGroups = groupSummaryLocalDataSource.observeGroupSummaries().first()

            val currentUser = userRepository.getUserInfo().fold(
                onSuccess = { it },
                onFailure = { null },
            ) ?: throw DomainException.Server("User not authenticated")
            val diff = calculateGroupSummaryDiff(
                remoteGroups = remoteGroups,
                localGroups = localGroups,
                currentUserId = currentUser.id,
            )
            // Helpful for diagnosing duplicates (e.g., same userId+groupId inserted twice)
            val remoteIds = remoteGroups.familyGroups.map { it.id }
            val remoteDistinct = remoteIds.distinct().size
            if (remoteDistinct != remoteIds.size) {
                Log.w(
                    TAG,
                    "Remote list contains duplicate group ids! total=${remoteIds.size}, distinct=$remoteDistinct"
                )
            }

            applyGroupSummaryDiff(
                diff = diff,
                localGroups = localGroups,
                currentUserId = currentUser.id,
            )
            Unit
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t ->
                Result.failure(DomainException.fromThrowable(t))
            },
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeGroupSummaries(): Flow<List<GroupSummaryData>> = flow {
        val currentUserId = dataSourceHelper.observeUser().first()?.id ?: return@flow
        Log.d(TAG, "observeGroupSummaries() - currentUserId=$currentUserId")
        emitAll(
            groupSummaryLocalDataSource.observeGroupSummaries().mapLatest { summaries ->
                summaries.mapNotNull { summaryEntity ->
                    val remoteId = summaryEntity.remoteId ?: return@mapNotNull null

                    val groupEntity = groupLocalDataSource.getByRemoteId(remoteId) ?: return@mapNotNull null

                    val role = userGroupLocalDataSource
                        .observeMembership(currentUserId, remoteId)
                        .first()
                        ?.role
                        ?: return@mapNotNull null

                    summaryEntity.toData(
                        name = groupEntity.name,
                        description = groupEntity.description.orEmpty(),
                        role = role,
                    )
                }
            }
        )
    }

    override fun observeMemberCount(groupId: Long): Flow<Int> {
        return groupSummaryLocalDataSource.observeGroupUserCount(groupId)
    }

    override suspend fun updateGroup(request: UpdateGroupRequest): Result<Unit> {
        val currentItem = groupLocalDataSource.getById(request.id)
            ?: return Result.failure(DomainException.Database("Group not found"))

        if (!connectivityObserver.isConnected.value) {
            groupLocalDataSource.upsert(
                currentItem.copy(
                    name = request.name,
                    description = request.description,
                    syncStatus = SyncStatus.PENDING_UPDATE
                )
            )
            return Result.failure(DomainException.NoInternet())
        }

        val result = groupRemoteDataSource.updateGroup(request)

        return result.fold(
            onSuccess = {
                groupLocalDataSource.upsert(it.toEntity(currentItem.orderIndex))
                Result.success(Unit)
            },
            onFailure = { throwable ->
                groupLocalDataSource.upsert(
                    currentItem.copy(
                        name = request.name,
                        description = request.description,
                        syncStatus = SyncStatus.PENDING_UPDATE
                    )
                )
                Result.failure(DomainException.fromThrowable(throwable))
            }
        )
    }

    override suspend fun deleteGroup(groupId: Long): Result<Unit> {
        val currentItem =
            groupLocalDataSource.getById(groupId) ?: return Result.failure(DomainException.Database("Group not found"))

        if (!connectivityObserver.isConnected.value) {
            groupLocalDataSource.upsert(currentItem.copy(syncStatus = SyncStatus.PENDING_DELETE))
            return Result.failure(DomainException.NoInternet())
        }

        val result = groupRemoteDataSource.deleteGroup(groupId)

        return result.fold(
            onSuccess = {
                groupLocalDataSource.deleteById(groupId)
                Result.success(Unit)
            },
            onFailure = {
                groupLocalDataSource.upsert(currentItem.copy(syncStatus = SyncStatus.PENDING_DELETE))
                Result.failure(DomainException.fromThrowable(it))
            }
        )
    }

    override suspend fun getGroup(groupId: Long): Result<GroupEntity> {
        val result = groupLocalDataSource.observeById(groupId).first()
            ?: return Result.failure(DomainException.Database("Group not found"))
        return Result.success(result)
    }

    override fun observeGroup(groupId: Long): Flow<GroupEntity?> {
        return groupLocalDataSource.observeById(groupId)
    }

    override suspend fun updateMember(userEntity: UserEntity): Result<Boolean> {
        userLocalDataSource.upsert(userEntity)
        return Result.success(true)
    }

    override suspend fun deleteMember(userEntity: UserEntity): Result<Boolean> {
        userLocalDataSource.delete(userEntity)
        return Result.success(true)
    }

    override suspend fun syncGroupDetails(groupId: Long): Result<Unit> {
        val remoteGroup = fetchRemoteGroupOrNull(groupId)
            ?: return Result.failure(DomainException.Server("Group not found"))

        val remoteMembers = remoteGroup.members
        val localMemberIds = getLocalMemberIds(groupId)
        val localMembersById = getLocalMembersById(localMemberIds)

        val diff = calculateMemberDiff(
            remoteMembers = remoteMembers,
            localMemberIds = localMemberIds,
            localMembersById = localMembersById,
        )

        applyMemberDiff(groupId = groupId, diff = diff)

        return Result.success(Unit)
    }

    override suspend fun clearGroups(): Result<Unit> {
        groupLocalDataSource.clear()
        userLocalDataSource.clear()
        userGroupLocalDataSource.clear()
        groupSummaryLocalDataSource.clear()
        return Result.success(Unit)
    }

    override suspend fun updateTaskCompletion(
        taskId: Long,
        isCompleted: Boolean
    ): Result<Unit> {
        groupRemoteDataSource.updateTaskCompletion(taskId, isCompleted)

        return Result.success(Unit)
    }

    private suspend fun fetchRemoteGroupOrNull(groupId: Long) =
        groupRemoteDataSource.getGroupByRemoteId(groupId).fold(
            onSuccess = { it },
            onFailure = { null },
        )

    private suspend fun getLocalMemberIds(groupId: Long): Set<Long> {
        return userGroupLocalDataSource
            .observeMembersOfGroup(groupId)
            .first()
            .map { it.userId }
            .toSet()
    }

    private suspend fun getLocalMembersById(localMemberIds: Set<Long>): Map<Long, UserEntity> {
        // NOTE: This keeps the original behavior (Flow.first per user) while isolating the responsibility.
        val result = mutableMapOf<Long, UserEntity>()
        localMemberIds.forEach { id ->
            val member = userLocalDataSource.observeById(id).first()
            if (member != null) {
                result[id] = member
            }
        }
        return result
    }

    private fun calculateMemberDiff(
        remoteMembers: List<GroupMemberData>,
        localMemberIds: Set<Long>,
        localMembersById: Map<Long, UserEntity>,
    ): Diff<UserEntityWithRole> {
        val remoteById = remoteMembers.associateBy { it.userId }
        val remoteIds = remoteById.keys

        val addedIds = remoteIds - localMemberIds
        val removedIds = localMemberIds - remoteIds
        val commonIds = remoteIds intersect localMemberIds

        val added = addedIds.mapNotNull { id ->
            val remote = remoteById[id] ?: return@mapNotNull null
            val role = remote.role
            val joinedAt = remote.joinedAt
            UserEntityWithRole(remote.toEntity(), role, joinedAt)
        }

        val changed = commonIds.mapNotNull { id ->
            val remote = remoteById[id] ?: return@mapNotNull null
            val remoteUser = remote.toEntity()
            val localUser = localMembersById[id]
            if (localUser != null && remoteUser != localUser) {
                val role = remote.role
                val joinedAt = remote.joinedAt
                UserEntityWithRole(remoteUser, role, joinedAt)
            } else {
                null
            }
        }
        Log.d("GroupManagementRepositoryImpl", "calculateMemberDiff: $added")
        Log.d("GroupManagementRepositoryImpl", "calculateMemberDiff: $changed")
        Log.d("GroupManagementRepositoryImpl", "calculateMemberDiff: $removedIds")

        return Diff(
            added = added,
            removed = removedIds.toList(),
            changed = changed,
        )
    }

    private suspend fun applyMemberDiff(groupId: Long, diff: Diff<UserEntityWithRole>) {
        diff.changed.forEach { item ->
            userLocalDataSource.upsert(item.userEntity)
            userGroupLocalDataSource.upsert(
                UserGroupEntity(
                    userId = item.userEntity.userId,
                    groupId = groupId,
                    role = item.role,
                    joinedAt = item.joinedAt ?: System.currentTimeMillis(),
                )
            )
        }

        diff.removed.forEach { userId ->
            userLocalDataSource.deleteById(userId)
            userGroupLocalDataSource.deleteByIds(userId, groupId)
        }

        diff.added.forEach { item ->
            userLocalDataSource.upsert(item.userEntity)
            userGroupLocalDataSource.upsert(
                UserGroupEntity(
                    userId = item.userEntity.userId,
                    groupId = groupId,
                    role = item.role,
                    joinedAt = item.joinedAt ?: System.currentTimeMillis(),
                )
            )
        }
    }

    private suspend fun calculateGroupSummaryDiff(
        remoteGroups: GroupSummaryDataList,
        localGroups: List<GroupSummaryEntity>,
        currentUserId: Long,
    ): Diff<GroupSummaryData> {
        val remoteById = remoteGroups.familyGroups.associateBy { it.id }
        val localByRemoteId = localGroups.toLocalByRemoteId()

        val localIds = localByRemoteId.keys
        val remoteIds = remoteById.keys

        val addedIds = remoteIds - localIds
        val removedIds = localIds - remoteIds
        val commonIds = localIds intersect remoteIds

        val added = addedIds.mapNotNull { remoteById[it] }

        val changed = commonIds.mapNotNull { id ->
            val remote = remoteById[id] ?: return@mapNotNull null
            val localComparable = buildLocalComparableSummary(
                groupRemoteId = id,
                localByRemoteId = localByRemoteId,
                currentUserId = currentUserId,
            ) ?: return@mapNotNull null

            if (localComparable != remote) remote else null
        }

        return Diff(
            added = added,
            removed = removedIds.toList(),
            changed = changed,
        )
    }

    private fun List<GroupSummaryEntity>.toLocalByRemoteId(): Map<Long, GroupSummaryEntity> {
        return this.mapNotNull { entity ->
            val remoteId = entity.remoteId
            if (remoteId != null) remoteId to entity else null
        }.toMap()
    }

    private suspend fun buildLocalComparableSummary(
        groupRemoteId: Long,
        localByRemoteId: Map<Long, GroupSummaryEntity>,
        currentUserId: Long,
    ): GroupSummaryData? {
        val localSummaryEntity = localByRemoteId[groupRemoteId] ?: return null
        val groupEntity = groupLocalDataSource.getByRemoteId(groupRemoteId) ?: return null

        val userRole = userGroupLocalDataSource
            .observeMembership(currentUserId, groupRemoteId)
            .first()
            ?.role
            ?: return null

        return localSummaryEntity.toData(
            name = groupEntity.name,
            description = groupEntity.description.orEmpty(),
            role = userRole,
        )
    }

    private suspend fun applyGroupSummaryDiff(
        diff: Diff<GroupSummaryData>,
        localGroups: List<GroupSummaryEntity>,
        currentUserId: Long,
    ) {
        val localByRemoteId = localGroups.toLocalByRemoteId()

        applyChangedGroupSummaries(
            changed = diff.changed,
            localByRemoteId = localByRemoteId,
            currentUserId = currentUserId,
        )

        applyAddedGroupSummaries(
            added = diff.added,
            currentUserId = currentUserId,
        )

        applyRemovedGroupSummaries(
            removedIds = diff.removed,
        )
    }

    private suspend fun applyChangedGroupSummaries(
        changed: List<GroupSummaryData>,
        localByRemoteId: Map<Long, GroupSummaryEntity>,
        currentUserId: Long,
    ) {
        changed.forEach { remote ->
            val id = remote.id
            val localSummaryEntity = localByRemoteId[id] ?: return@forEach

            groupSummaryLocalDataSource.update(
                GroupSummaryEntity(
                    id = localSummaryEntity.id,
                    remoteId = remote.id,
                    memberCount = remote.memberCount,
                    pendingTaskCount = remote.pendingTaskCount,
                    createdAt = remote.createdAt,
                    syncStatus = SyncStatus.SYNCED,
                    orderIndex = localSummaryEntity.orderIndex,
                )
            )

            val groupEntity = groupLocalDataSource.getByRemoteId(id)
            if (groupEntity != null) {
                groupLocalDataSource.upsert(
                    groupEntity.copy(
                        name = remote.name,
                        description = remote.description,
                    )
                )
            }

            Log.d(TAG, "applyChangedGroupSummaries() - upserting membership userId=$currentUserId groupId=${remote.id}")
            userGroupLocalDataSource.upsert(
                UserGroupEntity(
                    userId = currentUserId,
                    groupId = remote.id,
                    role = remote.role,
                    joinedAt = remote.createdAt,
                )
            )
        }
    }

    private suspend fun applyAddedGroupSummaries(
        added: List<GroupSummaryData>,
        currentUserId: Long,
    ) {
        if (added.isEmpty()) return

        Log.d(TAG, "applyAddedGroupSummaries() - START added=${added.size}, currentUserId=$currentUserId")
        val addedMap = added.associateBy { it.id }
        val entities = added.map {
            GroupEntity(
                remoteId = it.id,
                name = it.name,
                description = it.description,
                createdAt = it.createdAt,
                updatedAt = it.createdAt,
                syncStatus = SyncStatus.SYNCED,
                orderIndex = groupLocalDataSource.getOrderIndex(),
            )
        }

        Log.d(TAG, "applyAddedGroupSummaries() - inserting GroupEntity count=${entities.size}")
        groupLocalDataSource.insertAll(entities)
        Log.d(TAG, "applyAddedGroupSummaries() - GroupEntity insertAll DONE")

        val memberships = added.map {
            UserGroupEntity(
                userId = currentUserId,
                groupId = it.id,
                role = it.role,
                joinedAt = it.createdAt,
            )
        }.associateBy { it.groupId }

        // detect duplicates that would violate (user_id, group_id) PK
        Log.d(TAG, "applyAddedGroupSummaries() - inserting UserGroupEntity memberships=$memberships")
        try {
            groupSummaryLocalDataSource.insertAll(
                memberships.map {
                    GroupSummaryEntity(
                        remoteId = it.value.groupId,
                        memberCount = addedMap[it.key]!!.memberCount,
                        pendingTaskCount = addedMap[it.key]!!.pendingTaskCount,
                        createdAt = addedMap[it.key]!!.createdAt,
                        syncStatus = SyncStatus.SYNCED,
                        orderIndex = groupLocalDataSource.getOrderIndex(),
                    )
                }
            )
            userGroupLocalDataSource.insertAll(memberships.values.toList())
            Log.d(TAG, "applyAddedGroupSummaries() - UserGroupEntity insertAll DONE")
        } catch (t: Throwable) {
            Log.e(TAG, "applyAddedGroupSummaries() - UserGroupEntity insertAll CRASH", t)
            throw t
        }

        Log.d(TAG, "applyAddedGroupSummaries() - END")
    }

    private suspend fun applyRemovedGroupSummaries(
        removedIds: List<Long>,
    ) {
        if (removedIds.isEmpty()) return
        groupLocalDataSource.deleteAll(removedIds)
    }

    private data class Diff<T>(
        val added: List<T>,
        val removed: List<Long>, // id of the removed item
        val changed: List<T>,
    )

    data class UserEntityWithRole(
        val userEntity: UserEntity,
        val role: String,
        val joinedAt: Long? = null,
    )
}
