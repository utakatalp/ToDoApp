package com.todoapp.mobile.data.source.local.dao.usergroup

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.todoapp.mobile.data.model.entity.usergroup.UserGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserGroupDao {

    @Query("DELETE FROM user_group_memberships")
    suspend fun clear()

    @Insert
    suspend fun insertAll(memberships: List<UserGroupEntity>)

    @Upsert
    suspend fun upsert(membership: UserGroupEntity)

    @Query(
        """
        SELECT *
        FROM user_group_memberships
        WHERE user_id = :userId AND group_id = :groupId
        """,
    )
    fun observeMembership(userId: Long, groupId: Long): Flow<UserGroupEntity?>

    @Query(
        """
        SELECT group_id
        FROM user_group_memberships
        WHERE user_id = :userId
        """,
    )
    suspend fun getGroupIdsOfUser(userId: Long): List<Long>

    @Query(
        """
        SELECT *
        FROM user_group_memberships
        WHERE group_id = :groupId
        ORDER BY joined_at ASC
        """,
    )
    fun observeMembersOfGroup(groupId: Long): Flow<List<UserGroupEntity>>

    @Query(
        """
        SELECT *
        FROM user_group_memberships
        WHERE user_id = :userId
        ORDER BY joined_at ASC
        """,
    )
    fun observeGroupsOfUser(userId: Long): Flow<List<UserGroupEntity>>

    @Delete
    suspend fun delete(membership: UserGroupEntity)

    @Query(
        """
        DELETE FROM user_group_memberships
        WHERE user_id = :userId AND group_id = :groupId
        """,
    )
    suspend fun deleteByIds(userId: Long, groupId: Long)

    @Query(
        """
        DELETE FROM user_group_memberships
        WHERE group_id = :groupId
        """,
    )
    suspend fun deleteAllByGroupId(groupId: Long)

    @Query(
        """
        UPDATE user_group_memberships
        SET role = :role
        WHERE user_id = :userId AND group_id = :groupId
        """,
    )
    suspend fun updateRole(userId: Long, groupId: Long, role: String)
}
