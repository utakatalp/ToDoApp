package com.todoapp.mobile.data.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.uikit.R
import com.todoapp.mobile.MainActivity

class NotificationService : Service() {

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    fun sendNotification(contentText: String, remindMinutesBefore: Int) {
        val activityIntent = Intent(this, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            activityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(
                "You have a task to do! In ${if (remindMinutesBefore == 0) "now" else remindMinutesBefore} minutes."
            )
            .setContentText(contentText)
            .setContentIntent(activityPendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(
            NOTIFICATION_ID,
            notification
        )
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val message = intent.getStringExtra(INTENT_EXTRA_MESSAGE)
        val time = intent.getLongExtra(INTENT_EXTRA_LONG, 0L)
        if (!message.isNullOrBlank()) {
            sendNotification(message, time.toInt())
        }
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "notification_channel"
        const val INTENT_EXTRA_MESSAGE = "extra_message"
        const val INTENT_EXTRA_LONG = "extra_time"
        private const val NOTIFICATION_ID = 1
    }
}
