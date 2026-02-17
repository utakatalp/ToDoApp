package com.todoapp.mobile

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Configuration
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.todoapp.mobile.data.notification.NotificationService
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.security.SecretModeEndEvent
import com.todoapp.mobile.domain.usecase.security.OnSecretModeEventUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), DefaultLifecycleObserver, Configuration.Provider {

    @Inject
    lateinit var secretPreferences: SecretPreferences

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super<Application>.onCreate()

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        createNotificationChannel()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        owner.lifecycleScope.launch {
            OnSecretModeEventUseCase(secretPreferences).invoke(SecretModeEndEvent.APP_CLOSED)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationService.CHANNEL_ID,
                "Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                setShowBadge(true)
            }
            channel.description = "Used for the notifications"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
