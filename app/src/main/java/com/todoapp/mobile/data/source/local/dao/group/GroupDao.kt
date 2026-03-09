package com.todoapp.mobile.data.source.local.dao.group

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.todoapp.mobile.data.model.entity.group.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Upsert
    suspend fun upsert(group: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: GroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<GroupEntity>)

    @Query("SELECT * FROM groups WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): GroupEntity?

    @Query("SELECT * FROM groups WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<GroupEntity?>

    @Query("SELECT * FROM groups WHERE remote_id = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Long): GroupEntity?

    @Query("SELECT * FROM groups WHERE remote_id = :remoteId LIMIT 1")
    fun observeByRemoteId(remoteId: Long): Flow<GroupEntity?>

    @Query("SELECT * FROM groups ORDER BY created_at DESC")
    fun observeAll(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups ORDER BY created_at DESC")
    suspend fun getAll(): List<GroupEntity>

    @Delete
    suspend fun delete(group: GroupEntity)

    @Query("DELETE FROM groups WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM groups WHERE remote_id = :remoteId")
    suspend fun deleteByRemoteId(remoteId: Long)

    @Query("DELETE FROM groups WHERE id IN (:ids)")
    suspend fun deleteAll(ids: List<Long>)

    @Query("SELECT MAX(order_index) FROM groups")
    suspend fun getOrderIndex(): Int

    @Query("UPDATE groups SET order_index = :orderIndex WHERE id = :groupId")
    suspend fun updateOrderIndex(groupId: Long, orderIndex: Int)

    @Query("DELETE FROM groups")
    suspend fun clear()
}
