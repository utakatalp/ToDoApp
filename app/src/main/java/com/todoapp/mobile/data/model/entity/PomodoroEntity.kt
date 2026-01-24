package com.todoapp.mobile.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro")
data class PomodoroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "session_count") val sessionCount: Int,
    @ColumnInfo(name = "focus_time") val focusTime: Int,
    @ColumnInfo(name = "short_break") val shortBreak: Int,
    @ColumnInfo(name = "long_break") val longBreak: Int,
    @ColumnInfo(name = "section_count") val sectionCount: Int
)
