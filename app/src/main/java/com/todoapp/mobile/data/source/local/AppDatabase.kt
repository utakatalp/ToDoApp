package com.todoapp.mobile.data.source.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.todoapp.mobile.data.model.entity.GroupEntity
import com.todoapp.mobile.data.model.entity.PomodoroEntity
import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.TaskEntity
import com.todoapp.mobile.data.source.local.datasource.GroupDao

@Database(
    version = 5,
    entities = [TaskEntity::class, PomodoroEntity::class, GroupEntity::class],
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
        ),
        AutoMigration(
            from = 4,
            to = 5,
            spec = AppDatabase.Migration4To5Spec::class
        ),
    ]
)
@TypeConverters(AppDatabase.SyncStatusConverter::class)
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

    @DeleteColumn(tableName = "groups", columnName = "image_url")
    @DeleteColumn(tableName = "groups", columnName = "color")
    class Migration4To5Spec : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            super.onPostMigrate(db)
            db.execSQL("UPDATE groups SET order_index = id")
        }
    }

    abstract fun taskDao(): TaskDao
    abstract fun groupDao(): GroupDao
    abstract fun pomodoroDao(): PomodoroDao

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
}
