package com.todoapp.mobile.data.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.todoapp.mobile.data.model.entity.PomodoroEntity
import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.TaskEntity

@Database(
    version = 4,
    entities = [TaskEntity::class, PomodoroEntity::class],
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = AppDatabase.Migration1To2Spec::class
        ),
        AutoMigration(
            from = 2,
            to = 3,
        ),
        AutoMigration(
            from = 3,
            to = 4,
            spec = AppDatabase.Migration3To4Spec::class
        )
    ]
)
@TypeConverters(SyncStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    class Migration1To2Spec : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE tasks ADD COLUMN is_secret INTEGER NOT NULL DEFAULT 0")
        }
    }

    class Migration3To4Spec : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
            // db.execSQL("ALTER TABLE tasks ADD COLUMN order_info INTEGER NOT NULL DEFAULT 0")
            db.execSQL("UPDATE tasks SET order_index = id ")
        }
    }

    abstract fun taskDao(): TaskDao
    abstract fun pomodoroDao(): PomodoroDao
}

class SyncStatusConverter {

    @TypeConverter
    fun fromSyncStatus(status: SyncStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toSyncStatus(value: String?): SyncStatus? {
        return value?.let { SyncStatus.valueOf(it) }
    }
}
