package com.todoapp.mobile.di

import com.todoapp.mobile.data.repository.ThemeRepositoryImpl
import com.todoapp.mobile.data.repository.UserRepositoryImpl
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
    abstract fun bindThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
