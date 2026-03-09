package com.todoapp.mobile.di

import com.todoapp.mobile.data.observer.AndroidConnectivityObserver
import com.todoapp.mobile.domain.observer.ConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ObserverModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(impl: AndroidConnectivityObserver): ConnectivityObserver
}
