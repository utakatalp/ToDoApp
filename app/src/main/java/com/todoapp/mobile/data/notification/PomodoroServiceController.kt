package com.todoapp.mobile.data.notification

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroServiceController
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {
    fun start() {
        if (!hasNotificationPermission()) {
            Timber.tag(TAG).d("POST_NOTIFICATIONS not granted; skipping live notification.")
            return
        }
        val intent = Intent(context, PomodoroForegroundService::class.java).apply {
            action = PomodoroForegroundService.ACTION_START
        }
        runCatching {
            ContextCompat.startForegroundService(context, intent)
        }.onFailure { Timber.tag(TAG).w(it, "startForegroundService failed") }
    }

    fun stop() {
        val intent = Intent(context, PomodoroForegroundService::class.java).apply {
            action = PomodoroForegroundService.ACTION_STOP
        }
        runCatching { context.startService(intent) }
            .onFailure { Timber.tag(TAG).w(it, "stopService start signal failed") }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private companion object {
        const val TAG: String = "PomodoroFgController"
    }
}
