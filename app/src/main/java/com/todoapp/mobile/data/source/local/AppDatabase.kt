package com.todoapp.mobile.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.todoapp.mobile.data.model.entity.PomodoroEntity
import com.todoapp.mobile.data.model.entity.TaskEntity

@Database(entities = [TaskEntity::class, PomodoroEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun pomodoroDao(): PomodoroDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pomodoro (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                session_count INTEGER NOT NULL,
                focus_time INTEGER NOT NULL,
                short_break INTEGER NOT NULL,
                long_break INTEGER NOT NULL,
                section_count INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}
