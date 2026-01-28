package com.todoapp.mobile.data.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.todoapp.mobile.data.model.entity.PomodoroEntity
import com.todoapp.mobile.data.model.entity.TaskEntity

@Database(
    version = 2,
    entities = [TaskEntity::class, PomodoroEntity::class],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
        )
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun pomodoroDao(): PomodoroDao
}
