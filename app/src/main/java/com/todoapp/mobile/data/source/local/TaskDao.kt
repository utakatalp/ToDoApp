package com.todoapp.mobile.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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
        SELECT date, COUNT(*) AS count
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

    @Query("""SELECT COUNT(*) FROM tasks WHERE date BETWEEN :startDate AND :endDate AND is_completed = :isCompleted""")
    fun getTaskCountInRange(
        startDate: Long,
        endDate: Long,
        isCompleted: Boolean,
    ): Flow<Int>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}
