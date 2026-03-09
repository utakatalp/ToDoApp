package com.todoapp.mobile.di

import com.todoapp.mobile.data.engine.PomodoroEngineImpl
import com.todoapp.mobile.data.repository.AuthRepositoryImpl
import com.todoapp.mobile.data.repository.DailyPlanPreferencesImpl
import com.todoapp.mobile.data.repository.PomodoroRepositoryImpl
import com.todoapp.mobile.data.repository.SecretPreferencesImpl
import com.todoapp.mobile.data.repository.SessionPreferencesImpl
import com.todoapp.mobile.data.repository.TaskSyncRepositoryImpl
import com.todoapp.mobile.data.repository.ThemeRepositoryImpl
import com.todoapp.mobile.data.repository.UserRepositoryImpl
import com.todoapp.mobile.data.repository.group.GroupManagementRepositoryImpl
import com.todoapp.mobile.data.repository.group.GroupTaskRepositoryImpl
import com.todoapp.mobile.data.repository.personal.PersonalTaskRepositoryImpl
import com.todoapp.mobile.data.source.local.datasource.TaskLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.TaskLocalDataSourceImpl
import com.todoapp.mobile.data.source.local.datasource.group.GroupSummaryLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.group.GroupSummaryLocalDataSourceImpl
import com.todoapp.mobile.data.source.local.datasource.group.GroupTaskLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.group.GroupTaskLocalDataSourceImpl
import com.todoapp.mobile.data.source.local.datasource.group.GroupsLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.group.GroupsLocalDataSourceImpl
import com.todoapp.mobile.data.source.local.datasource.personal.PersonalTaskLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.personal.PersonalTaskLocalDataSourceImpl
import com.todoapp.mobile.data.source.local.datasource.user.UserLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.user.UserLocalDataSourceImpl
import com.todoapp.mobile.data.source.local.datasource.usergroup.UserGroupLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.usergroup.UserGroupLocalDataSourceImpl
import com.todoapp.mobile.data.source.remote.datasource.GroupRemoteDataSource
import com.todoapp.mobile.data.source.remote.datasource.GroupRemoteDataSourceImpl
import com.todoapp.mobile.data.source.remote.datasource.TaskRemoteDataSource
import com.todoapp.mobile.data.source.remote.datasource.TaskRemoteDataSourceImpl
import com.todoapp.mobile.data.source.remote.datasource.group.GroupManagementRemoteDataSource
import com.todoapp.mobile.data.source.remote.datasource.group.GroupManagementRemoteDataSourceImpl
import com.todoapp.mobile.data.source.remote.datasource.group.GroupSummaryRemoteDataSource
import com.todoapp.mobile.data.source.remote.datasource.group.GroupSummaryRemoteDataSourceImpl
import com.todoapp.mobile.data.source.remote.datasource.group.GroupTaskRemoteDataSource
import com.todoapp.mobile.data.source.remote.datasource.group.GroupTaskRemoteDataSourceImpl
import com.todoapp.mobile.data.source.remote.datasource.personal.PersonalTaskRemoteDataSource
import com.todoapp.mobile.data.source.remote.datasource.personal.PersonalTaskRemoteDataSourceImpl
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.repository.AuthRepository
import com.todoapp.mobile.domain.repository.DailyPlanPreferences
import com.todoapp.mobile.domain.repository.PomodoroRepository
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.repository.SessionPreferences
import com.todoapp.mobile.domain.repository.TaskSyncRepository
import com.todoapp.mobile.domain.repository.ThemeRepository
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.domain.repository.group.GroupManagementRepository
import com.todoapp.mobile.domain.repository.group.GroupTaskRepository
import com.todoapp.mobile.domain.repository.personal.PersonalTaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRemoteDataSource(impl: TaskRemoteDataSourceImpl): TaskRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTaskLocalDataSource(impl: TaskLocalDataSourceImpl): TaskLocalDataSource

    @Binds
    @Singleton
    abstract fun bindSessionPreferences(impl: SessionPreferencesImpl): SessionPreferences

    @Binds
    @Singleton
    abstract fun bindThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindPomodoroEngine(impl: PomodoroEngineImpl): PomodoroEngine

    @Binds
    @Singleton
    abstract fun bindTaskSyncRepository(impl: TaskSyncRepositoryImpl): TaskSyncRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSecretModePreferences(secretPreferencesImpl: SecretPreferencesImpl): SecretPreferences

    @Binds
    @Singleton
    abstract fun bindFamilyGroupDataSource(groupRemoteDataSourceImpl: GroupRemoteDataSourceImpl): GroupRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPomodoroRepository(pomodoroRepositoryImpl: PomodoroRepositoryImpl): PomodoroRepository

    @Binds
    @Singleton
    abstract fun bindDailyPlanPreferences(dailyPlanPreferencesImpl: DailyPlanPreferencesImpl): DailyPlanPreferences

    @Binds
    @Singleton
    abstract fun bindPersonalTaskRepository(
        personalTaskRepositoryImpl: PersonalTaskRepositoryImpl
    ): PersonalTaskRepository

    @Binds
    @Singleton
    abstract fun bindGroupTaskRepository(groupTaskRepositoryImpl: GroupTaskRepositoryImpl): GroupTaskRepository

    @Binds
    @Singleton
    abstract fun bindPersonalTaskLocalDataSource(
        personalTaskLocalDataSourceImpl: PersonalTaskLocalDataSourceImpl
    ): PersonalTaskLocalDataSource

    @Binds
    @Singleton
    abstract fun bindGroupTaskLocalDataSource(
        groupTaskLocalDataSourceImpl: GroupTaskLocalDataSourceImpl
    ): GroupTaskLocalDataSource

    @Binds
    @Singleton
    abstract fun bindPersonalTaskRemoteDataSource(
        personalTaskRemoteDataSourceImpl: PersonalTaskRemoteDataSourceImpl
    ): PersonalTaskRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindGroupTaskRemoteDataSource(
        groupTaskRemoteDataSourceImpl: GroupTaskRemoteDataSourceImpl
    ): GroupTaskRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserLocalDataSource(userLocalDataSourceImpl: UserLocalDataSourceImpl): UserLocalDataSource

    @Binds
    @Singleton
    abstract fun bindUserGroupLocalDataSource(
        userGroupLocalDataSourceImpl: UserGroupLocalDataSourceImpl
    ): UserGroupLocalDataSource

    @Binds
    @Singleton
    abstract fun bindGroupsLocalDataSource(groupsLocalDataSourceImpl: GroupsLocalDataSourceImpl): GroupsLocalDataSource

    @Binds
    @Singleton
    abstract fun bindGroupSummaryLocalDataSource(
        groupSummaryLocalDataSourceImpl: GroupSummaryLocalDataSourceImpl
    ): GroupSummaryLocalDataSource

    @Binds
    @Singleton
    abstract fun bindGroupSummaryRemoteDataSource(
        groupSummaryRemoteDataSourceImpl: GroupSummaryRemoteDataSourceImpl
    ): GroupSummaryRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindGroupManagementRemoteDataSource(
        groupManagementRemoteDataSourceImpl: GroupManagementRemoteDataSourceImpl
    ): GroupManagementRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindGroupManagementRepository(
        groupManagementRepositoryImpl: GroupManagementRepositoryImpl
    ): GroupManagementRepository
}
