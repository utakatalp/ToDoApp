package com.todoapp.mobile.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.todoapp.mobile.data.auth.AuthTokenManager
import com.todoapp.mobile.data.auth.GoogleSignInManager
import com.todoapp.mobile.data.repository.DataStoreHelper
import com.todoapp.mobile.data.repository.PomodoroRepositoryImpl
import com.todoapp.mobile.data.repository.SecretPreferencesImpl
import com.todoapp.mobile.data.repository.TaskRepositoryImpl
import com.todoapp.mobile.data.source.local.AppDatabase
import com.todoapp.mobile.data.source.local.PomodoroDao
import com.todoapp.mobile.data.source.local.TaskDao
import com.todoapp.mobile.domain.repository.PomodoroRepository
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.repository.TaskRepository
import dagger.Binds
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
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private const val PREFS_NAME = "todo_prefs"

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
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
    fun provideClock(): Clock = Clock.systemUTC()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Module
    @InstallIn(SingletonComponent::class)
    object DataStoreModule

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<androidx.datastore.preferences.core.Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun providePomodoro(database: AppDatabase): PomodoroDao = database.pomodoroDao()

    @Provides
    @Singleton
    fun provideGoogleSignInManager(
        @ApplicationContext context: Context,
    ): GoogleSignInManager = GoogleSignInManager(context)

    @Provides
    @Singleton
    fun provideAuthTokensManager(
        dataStoreHelper: DataStoreHelper
    ): AuthTokenManager = AuthTokenManager(dataStoreHelper)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalStorageModuleForBindings {
    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl,
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindSecretModePreferences(
        secretPreferencesImpl: SecretPreferencesImpl,
    ): SecretPreferences

    @Binds
    @Singleton
    abstract fun bindPomodoroRepository(
        pomodoroRepositoryImpl: PomodoroRepositoryImpl,
    ): PomodoroRepository
}
