package com.todoapp.mobile.di

import com.todoapp.mobile.data.notification.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationServiceModule {
    @Provides
    @Singleton
    fun provideNotificationService(): NotificationService {
        return NotificationService()
    }
}
