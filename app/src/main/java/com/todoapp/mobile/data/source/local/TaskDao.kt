package com.todoapp.mobile.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.todoapp.mobile.data.model.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

data class DayCount(
    val date: Long,
    val count: Int,
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date")
    fun getTasksByDate(date: Long): Flow<List<TaskEntity>>

    @Insert
    suspend fun insert(task: TaskEntity)

    @Query("UPDATE tasks SET is_completed = :isCompleted WHERE id = :id")
    fun updateTask(id: Long, isCompleted: Boolean)

    @Query("SELECT * FROM tasks WHERE date BETWEEN :startDate AND :endDate")
    fun loadTasksBetweenRange(
        startDate: Long,
        endDate: Long,
    ): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT date AS date, COUNT(*) AS count
        FROM tasks
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

    @Query("SELECT COUNT(*) FROM tasks WHERE date BETWEEN :startDate AND :endDate AND is_completed = 1")
    fun getCompletedTaskAmountInAWeek(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE date BETWEEN :startDate AND :endDate AND is_completed = 1")
    fun getCompletedTaskAmountYearToDate(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE date BETWEEN :startDate AND :endDate AND is_completed = 0")
    fun getPendingTaskAmountInAWeek(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE date BETWEEN :startDate AND :endDate AND is_completed = 0")
    fun getPendingTaskAmountYearToDate(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    @Delete
    suspend fun delete(task: TaskEntity)
}
