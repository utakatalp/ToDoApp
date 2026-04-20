package com.todoapp.mobile.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.todoapp.mobile.data.model.entity.GroupTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupTaskDao {

    @Query("SELECT * FROM group_tasks WHERE local_group_id = :localGroupId")
    fun getTasksByGroupId(localGroupId: Long): Flow<List<GroupTaskEntity>>

    @Query("SELECT * FROM group_tasks WHERE remote_id = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Long): GroupTaskEntity?

    @Query("SELECT remote_id FROM group_tasks WHERE remote_id IS NOT NULL")
    suspend fun getAllRemoteIds(): List<Long>

    @Query(
        "SELECT * FROM group_tasks WHERE " +
            "(title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')",
    )
    fun searchTasks(query: String): Flow<List<GroupTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: GroupTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<GroupTaskEntity>)

    @Update
    suspend fun update(task: GroupTaskEntity)

    @Delete
    suspend fun delete(task: GroupTaskEntity)

    @Query("DELETE FROM group_tasks WHERE local_group_id = :localGroupId")
    suspend fun deleteByGroupId(localGroupId: Long)

    @Query("DELETE FROM group_tasks WHERE remote_id = :remoteId")
    suspend fun deleteByRemoteId(remoteId: Long)

    @Query("UPDATE group_tasks SET is_completed = :isCompleted WHERE remote_id = :remoteId")
    suspend fun updateCompletion(remoteId: Long, isCompleted: Boolean)

    @Query(
        "UPDATE group_tasks SET title = :title, description = :description, " +
            "due_date = :dueDate, priority = :priority, " +
            "assignee_user_id = :assigneeUserId, assignee_display_name = :assigneeDisplayName, " +
            "assignee_avatar_url = :assigneeAvatarUrl WHERE remote_id = :remoteId",
    )
    suspend fun updateTask(
        remoteId: Long,
        title: String,
        description: String?,
        dueDate: Long?,
        priority: String?,
        assigneeUserId: Long?,
        assigneeDisplayName: String?,
        assigneeAvatarUrl: String?,
    )
}
