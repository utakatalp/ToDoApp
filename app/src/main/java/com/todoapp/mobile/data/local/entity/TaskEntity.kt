package com.todoapp.mobile.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "time_start") val timeStart: Long,
    @ColumnInfo(name = "time_end") val timeEnd: Long,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
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

    @Query("SELECT COUNT(*) FROM tasks WHERE date BETWEEN :startDate AND :endDate AND is_completed = 1")
    fun getCompletedTaskAmountInAWeek(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE date BETWEEN :startDate AND :endDate AND is_completed = 0")
    fun getPendingTaskAmountInAWeek(
        startDate: Long,
        endDate: Long,
    ): Flow<Int>

    @Delete
    suspend fun delete(task: TaskEntity)
}
