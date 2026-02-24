package com.todoapp.mobile.data.source.local.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.todoapp.mobile.data.model.entity.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {

    @Query("SELECT * FROM `groups`")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Insert
    suspend fun insert(group: GroupEntity)

    @Update
    suspend fun update(group: GroupEntity)

    @Delete
    suspend fun delete(group: GroupEntity)

    @Query("DELETE FROM `groups`")
    suspend fun deleteAll()

    @Query("SELECT * FROM `groups` WHERE id = :id LIMIT 1")
    suspend fun getGroupById(id: Long): GroupEntity?

    @Query("SELECT * FROM `groups` WHERE name = :name LIMIT 1")
    suspend fun getGroupByName(name: String): GroupEntity

    @Query("UPDATE `groups` SET order_index = :orderIndex WHERE id = :id")
    suspend fun updateOrderIndex(id: Long, orderIndex: Int)

    @Query("SELECT * FROM `groups` ORDER BY order_index ASC")
    fun getAllGroupsOrdered(): Flow<List<GroupEntity>>
}
