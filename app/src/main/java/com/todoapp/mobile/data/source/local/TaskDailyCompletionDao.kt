package com.todoapp.mobile.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.todoapp.mobile.data.model.entity.TaskDailyCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDailyCompletionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TaskDailyCompletionEntity)

    @Query("DELETE FROM task_daily_completions WHERE task_id = :taskId AND date = :date")
    suspend fun delete(taskId: Long, date: Long)

    @Query("SELECT * FROM task_daily_completions WHERE date = :date")
    fun observeForDate(date: Long): Flow<List<TaskDailyCompletionEntity>>

    @Query("SELECT * FROM task_daily_completions WHERE date BETWEEN :start AND :end")
    fun observeRange(start: Long, end: Long): Flow<List<TaskDailyCompletionEntity>>

    @Query("DELETE FROM task_daily_completions")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<TaskDailyCompletionEntity>)
}
