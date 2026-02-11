package com.todoapp.mobile.data.source.remote.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class TDFireBaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TEST", "TOKEN: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM_TEST", "MESSAGE: ${message.data}")
        Log.d("FCM_TEST", "MESSAGE: ${message.notification?.body}")
        Log.d("FCM_TEST", "NOTIF_TITLE: ${message.notification?.title}")
    }
}
