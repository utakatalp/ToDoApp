package com.todoapp.mobile.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.todoapp.mobile.data.repository.SecretPreferencesImpl
import com.todoapp.mobile.data.repository.TaskRepositoryImpl
import com.todoapp.mobile.data.source.local.AppDatabase
import com.todoapp.mobile.data.source.local.TaskDao
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {
    private const val DB_NAME = "todo_db"
    private const val PREFS_NAME = "todo_prefs"

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
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    @Provides
    @Singleton
    fun provideSecretModePreferences(
        sharedPreferences: SharedPreferences
    ): SecretPreferences = SecretPreferencesImpl(sharedPreferences)

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemUTC()

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository = TaskRepositoryImpl(taskDao)
}
