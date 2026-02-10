package com.todoapp.mobile.di

import com.todoapp.mobile.data.engine.PomodoroEngineImpl
import com.todoapp.mobile.data.repository.SessionPreferencesImpl
import com.todoapp.mobile.data.repository.ThemeRepositoryImpl
import com.todoapp.mobile.data.repository.UserRepositoryImpl
import com.todoapp.mobile.data.source.local.datasource.TaskLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.TaskLocalDataSourceImpl
import com.todoapp.mobile.data.source.remote.datasource.TaskRemoteDataSource
import com.todoapp.mobile.data.source.remote.datasource.TaskRemoteDataSourceImpl
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.repository.SessionPreferences
import com.todoapp.mobile.domain.repository.ThemeRepository
import com.todoapp.mobile.domain.repository.UserRepository
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
}
