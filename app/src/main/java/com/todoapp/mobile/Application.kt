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
import com.todoapp.mobile.data.notification.NotificationService
import com.todoapp.mobile.data.notification.PomodoroNotificationChannels
import com.todoapp.mobile.domain.alarm.RescheduleAllAlarmsUseCase
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.security.SecretModeEndEvent
import com.todoapp.mobile.domain.usecase.security.OnSecretModeEventUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class Application :
    Application(),
    DefaultLifecycleObserver,
    Configuration.Provider,
    coil.ImageLoaderFactory {
    @Inject
    lateinit var secretPreferences: SecretPreferences

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var okHttpClient: okhttp3.OkHttpClient

    @Inject
    lateinit var pomodoroEngine: PomodoroEngine

    @Inject
    lateinit var rescheduleAllAlarmsUseCase: RescheduleAllAlarmsUseCase

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()

    override fun onCreate() {
        super<Application>.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        createNotificationChannel()
        PomodoroNotificationChannels.ensurePomodoroChannel(this)
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            runCatching { rescheduleAllAlarmsUseCase() }
                .onFailure { Timber.tag("RescheduleAllAlarms").w(it, "reschedule on app start failed") }
        }
    }

    // Coil picks this up automatically as the app-wide ImageLoader; wires the shared OkHttpClient
    // (with the AuthInterceptor) so authenticated endpoints like /users/{id}/avatar and
    // /tasks/{id}/photos/{photoId} send the Bearer token.
    override fun newImageLoader(): coil.ImageLoader = coil.ImageLoader
        .Builder(this)
        .okHttpClient { okHttpClient }
        .crossfade(true)
        .build()

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        owner.lifecycleScope.launch {
            OnSecretModeEventUseCase(secretPreferences).invoke(SecretModeEndEvent.APP_CLOSED)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        pomodoroEngine.shutdown()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    NotificationService.CHANNEL_ID,
                    "Tasks",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    enableVibration(true)
                    enableLights(true)
                    setShowBadge(true)
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                }
            channel.description = "Used for the notifications"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
