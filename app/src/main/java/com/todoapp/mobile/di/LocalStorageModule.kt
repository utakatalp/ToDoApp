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
import com.todoapp.mobile.data.repository.DailyPlanPreferencesImpl
import com.todoapp.mobile.data.repository.DataStoreHelper
import com.todoapp.mobile.data.repository.FCMTokenPreferencesImpl
import com.todoapp.mobile.data.repository.SecretPreferencesImpl
import com.todoapp.mobile.data.source.local.AppDatabase
import com.todoapp.mobile.data.source.local.GroupActivityDao
import com.todoapp.mobile.data.source.local.GroupMemberDao
import com.todoapp.mobile.data.source.local.GroupTaskDao
import com.todoapp.mobile.data.source.local.PomodoroDao
import com.todoapp.mobile.data.source.local.TaskDao
import com.todoapp.mobile.data.source.local.datasource.GroupDao
import com.todoapp.mobile.domain.repository.DailyPlanPreferences
import com.todoapp.mobile.domain.repository.FCMTokenPreferences
import com.todoapp.mobile.domain.repository.SecretPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalStorageModule {
    private const val DB_NAME = "todo_db"
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")
    private const val PREFS_NAME = "todo_prefs"

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room
        .databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME,
        ).build()

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences = try {
        createEncryptedSharedPreferences(context, PREFS_NAME, buildMasterKey(context))
    } catch (e: Exception) {
        // Keystore master key got out of sync with Tink keyset (common after
        // device-level keystore rotation, biometric re-enroll, or the OS
        // killing our process while AFK). Nuke both sides and rebuild.
        deleteSharedPreferencesFile(context, PREFS_NAME)
        deleteMasterKeyEntry()
        createEncryptedSharedPreferences(context, PREFS_NAME, buildMasterKey(context))
    }

    private fun buildMasterKey(context: Context): MasterKey = MasterKey
        .Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private fun deleteMasterKeyEntry() {
        runCatching {
            val ks = java.security.KeyStore.getInstance("AndroidKeyStore")
            ks.load(null)
            if (ks.containsAlias(MasterKey.DEFAULT_MASTER_KEY_ALIAS)) {
                ks.deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            }
        }
    }

    private fun createEncryptedSharedPreferences(
        context: Context,
        fileName: String,
        masterKey: MasterKey,
    ): SharedPreferences = EncryptedSharedPreferences.create(
        context,
        fileName,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private fun deleteSharedPreferencesFile(
        context: Context,
        fileName: String,
    ) {
        try {
            val sharedPrefsFile = File(context.applicationInfo.dataDir, "shared_prefs/$fileName.xml")
            if (sharedPrefsFile.exists()) {
                sharedPrefsFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<androidx.datastore.preferences.core.Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemUTC()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideGroupDao(database: AppDatabase): GroupDao = database.groupDao()

    @Provides
    @Singleton
    fun provideGroupTaskDao(database: AppDatabase): GroupTaskDao = database.groupTaskDao()

    @Provides
    @Singleton
    fun provideGroupMemberDao(database: AppDatabase): GroupMemberDao = database.groupMemberDao()

    @Provides
    @Singleton
    fun provideGroupActivityDao(database: AppDatabase): GroupActivityDao = database.groupActivityDao()

    @Provides
    @Singleton
    fun providePomodoro(database: AppDatabase): PomodoroDao = database.pomodoroDao()

    @Provides
    @Singleton
    fun provideGoogleSignInManager(): GoogleSignInManager = GoogleSignInManager

    @Provides
    @Singleton
    fun provideAuthTokensManager(dataStoreHelper: DataStoreHelper): AuthTokenManager = AuthTokenManager(dataStoreHelper)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalStorageModuleForBindings {
    @Binds
    @Singleton
    abstract fun bindSecretModePreferences(secretPreferencesImpl: SecretPreferencesImpl): SecretPreferences

    @Binds
    @Singleton
    abstract fun bindDailyPlanPreferences(dailyPlanPreferencesImpl: DailyPlanPreferencesImpl): DailyPlanPreferences

    @Binds
    @Singleton
    abstract fun bindFcmTokenPreferences(impl: FCMTokenPreferencesImpl): FCMTokenPreferences
}
