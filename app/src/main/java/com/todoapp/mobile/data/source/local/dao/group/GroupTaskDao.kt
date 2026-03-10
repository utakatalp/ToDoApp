package com.todoapp.mobile.data.source.local.dao.group

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.todoapp.mobile.data.model.entity.group.GroupTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupTaskDao {

    @Insert
    suspend fun insert(task: GroupTaskEntity)

    @Query("SELECT * FROM group_tasks WHERE group_id = :groupId ORDER BY order_index ASC")
    fun getTasks(groupId: Long): Flow<List<GroupTaskEntity>>

    @Query("SELECT COUNT(*) FROM group_tasks WHERE group_id = :groupId")
    suspend fun getTaskCount(groupId: Long): Int

    @Query("SELECT * FROM group_tasks WHERE remote_id = :remoteId")
    suspend fun getByRemoteId(remoteId: Long): GroupTaskEntity?

    @Query("SELECT * FROM group_tasks WHERE group_id = :groupId AND assigned_to_user_id = :userId")
    fun filterTasks(groupId: Long, userId: Long): Flow<List<GroupTaskEntity>>
}
