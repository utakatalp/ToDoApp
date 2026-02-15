package com.todoapp.mobile.data.source.remote.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.todoapp.mobile.domain.repository.FCMTokenPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TDFireBaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenPreferences: FCMTokenPreferences

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        fcmTokenPreferences.setPendingToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}
