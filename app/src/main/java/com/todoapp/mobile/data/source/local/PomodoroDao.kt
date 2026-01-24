package com.todoapp.mobile.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.todoapp.mobile.data.model.entity.PomodoroEntity

@Dao
interface PomodoroDao {
    @Update
    suspend fun updatePomodoro(pomodoro: PomodoroEntity)

    @Query("SELECT * FROM pomodoro")
    suspend fun getPomodoro(): PomodoroEntity?

    @Insert
    suspend fun insertPomodoro(pomodoro: PomodoroEntity)
}
