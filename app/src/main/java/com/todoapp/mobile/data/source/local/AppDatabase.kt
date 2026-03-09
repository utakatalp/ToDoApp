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
import com.todoapp.mobile.data.model.entity.group.GroupEntity
import com.todoapp.mobile.data.model.entity.group.GroupSummaryEntity
import com.todoapp.mobile.data.model.entity.group.GroupTaskEntity
import com.todoapp.mobile.data.model.entity.personal.PersonalTaskEntity
import com.todoapp.mobile.data.model.entity.user.UserEntity
import com.todoapp.mobile.data.model.entity.usergroup.UserGroupEntity
import com.todoapp.mobile.data.source.local.dao.group.GroupDao
import com.todoapp.mobile.data.source.local.dao.group.GroupSummaryDao
import com.todoapp.mobile.data.source.local.dao.group.GroupTaskDao
import com.todoapp.mobile.data.source.local.dao.personal.PersonalTaskDao
import com.todoapp.mobile.data.source.local.dao.user.UserDao
import com.todoapp.mobile.data.source.local.dao.usergroup.UserGroupDao

@Database(
    version = 9,
    entities = [
        TaskEntity::class,
        PomodoroEntity::class,
        PersonalTaskEntity::class,
        GroupTaskEntity::class,
        UserEntity::class,
        GroupEntity::class,
        UserGroupEntity::class,
        GroupSummaryEntity::class,
    ],
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
        AutoMigration(
            from = 5,
            to = 6,
            spec = AppDatabase.Migration5To6Spec::class
        ),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9)

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
            db.execSQL("UPDATE tasks SET order_index = id ")
        }
    }

    class Migration4To5Spec : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE tasks ADD COLUMN family_group_id INTEGER")
        }
    }

    class Migration5To6Spec : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                INSERT INTO personal_tasks (
                    remote_id,
                    title,
                    description,
                    date,
                    time_start,
                    time_end,
                    is_completed,
                    is_secret,
                    created_at,
                    order_index,
                    sync_status
                )
                SELECT
                    remote_id,
                    title,
                    description,
                    date,
                    CAST(time_start AS INTEGER) AS time_start,
                    CAST(time_end AS INTEGER)   AS time_end,
                    is_completed,
                    is_secret,
                    created_at,
                    order_index,
                    sync_status
                FROM tasks
                WHERE family_group_id IS NULL
                """
            )

            // Remove moved rows from the old table to avoid duplicates.
            db.execSQL("DELETE FROM tasks WHERE family_group_id IS NULL")
        }
    }

    abstract fun taskDao(): TaskDao
    abstract fun pomodoroDao(): PomodoroDao

    abstract fun personalTaskDao(): PersonalTaskDao
    abstract fun groupTaskDao(): GroupTaskDao

    abstract fun groupDao(): GroupDao
    abstract fun userDao(): UserDao
    abstract fun userGroupDao(): UserGroupDao

    abstract fun groupSummaryDao(): GroupSummaryDao
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
