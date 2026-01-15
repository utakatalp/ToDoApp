package com.todoapp.mobile.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.todoapp.mobile.data.model.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
