package com.todoapp.mobile.data.source.local.dao.personal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.personal.PersonalTaskEntity
import com.todoapp.mobile.data.source.local.DayCount
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalTaskDao {

    @Query("SELECT * FROM personal_tasks")
    fun getAllTasks(): Flow<List<PersonalTaskEntity>>

    @Query("SELECT * FROM personal_tasks WHERE date = :date ORDER BY order_index ASC")
    fun getTasksByDate(date: Long): Flow<List<PersonalTaskEntity>>

    @Query("UPDATE personal_tasks SET is_completed = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean)

    @Query("SELECT * FROM personal_tasks WHERE date BETWEEN :startDate AND :endDate")
    fun loadTasksBetweenRange(
        startDate: Long,
        endDate: Long,
    ): Flow<List<PersonalTaskEntity>>

    @Query(
        """
        SELECT COUNT(*) AS count
        FROM personal_tasks
        WHERE date BETWEEN :startDate AND :endDate
          AND is_completed = 1
        """
    )
    fun observeCompletedCounts(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) AS count
        FROM personal_tasks
        WHERE date BETWEEN :startDate AND :endDate
          AND is_completed = 0
        """
    )
    fun observeNonCompletedCounts(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    @Query(
        """
        SELECT date, COUNT(*) AS count
        FROM personal_tasks
        WHERE date BETWEEN :startDate AND :endDate
          AND is_completed = 1
        GROUP BY date
        ORDER BY date ASC
        """
    )
    fun observeCompletedCountsByDay(
        startDate: Long,
        endDate: Long,
    ): Flow<List<DayCount>>

    @Query(
        """SELECT COUNT(*) FROM personal_tasks 
            WHERE date BETWEEN :startDate AND :endDate 
            AND is_completed = :isCompleted"""
    )
    fun getTaskCountInRange(
        startDate: Long,
        endDate: Long,
        isCompleted: Boolean,
    ): Flow<Int>

    @Query("SELECT * FROM personal_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): PersonalTaskEntity?

    // --- Sync helpers ---

    // Find local task by remote id
    @Query("SELECT * FROM personal_tasks WHERE remote_id = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Long): PersonalTaskEntity?

    // Pending creates (no remote_id yet, marked for create)
    @Query("SELECT * FROM personal_tasks WHERE sync_status = 'PENDING_CREATE'")
    suspend fun getPendingCreates(): List<PersonalTaskEntity>

    // Pending updates
    @Query("SELECT * FROM personal_tasks WHERE sync_status = 'PENDING_UPDATE'")
    suspend fun getPendingUpdates(): List<PersonalTaskEntity>

    // Pending deletes
    @Query("SELECT * FROM personal_tasks WHERE sync_status = 'PENDING_DELETE'")
    suspend fun getPendingDeletes(): List<PersonalTaskEntity>

    // Update sync status only
    @Query("UPDATE personal_tasks SET sync_status = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: SyncStatus)

    // Mark task as synced and set remote id after successful create
    @Query(
        """
        UPDATE personal_tasks
        SET remote_id = :remoteId,
            sync_status = 'SYNCED'
        WHERE id = :id
        """
    )
    suspend fun markCreatedSynced(id: Long, remoteId: Long)

    // Mark update synced (no remote id change)
    @Query(
        """
        UPDATE personal_tasks
        SET sync_status = 'SYNCED'
        WHERE id = :id
        """
    )
    suspend fun markUpdatedSynced(id: Long)

    // Physically delete rows that were successfully deleted on server
    @Query("DELETE FROM personal_tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE personal_tasks SET order_index = :orderIndex WHERE id = :id")
    suspend fun updateOrderIndex(id: Long, orderIndex: Int)

    @Query("SELECT COUNT(*) FROM personal_tasks")
    fun observeTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM personal_tasks")
    suspend fun getTaskCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: PersonalTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<PersonalTaskEntity>)

    @Update
    suspend fun update(task: PersonalTaskEntity)

    @Delete
    suspend fun delete(task: PersonalTaskEntity)

    @Delete
    suspend fun deleteAll(tasks: List<PersonalTaskEntity>)
}
