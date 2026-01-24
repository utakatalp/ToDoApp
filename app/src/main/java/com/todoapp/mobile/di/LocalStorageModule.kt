package com.todoapp.mobile.di

import android.content.Context
import androidx.room.Room
import com.todoapp.mobile.data.repository.PomodoroRepositoryImpl
import com.todoapp.mobile.data.repository.TaskRepositoryImpl
import com.todoapp.mobile.data.source.local.AppDatabase
import com.todoapp.mobile.data.source.local.MIGRATION_1_2
import com.todoapp.mobile.data.source.local.PomodoroDao
import com.todoapp.mobile.data.source.local.TaskDao
import com.todoapp.mobile.domain.repository.PomodoroRepository
import com.todoapp.mobile.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {
    private const val DB_NAME = "todo_db"

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun providePomodoroDao(database: AppDatabase): PomodoroDao = database.pomodoroDao()

    @Provides
    fun providePomodoroRepository(pomodoroDao: PomodoroDao): PomodoroRepository = PomodoroRepositoryImpl(pomodoroDao)

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository = TaskRepositoryImpl(taskDao)
}
