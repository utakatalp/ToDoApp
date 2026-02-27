package com.todoapp.mobile.domain.repository

interface FCMTokenPreferences {

    fun getPendingToken(): String?

    fun getDeviceId(): String?

    fun getDeviceName(): String?

    fun setPendingToken(token: String)

    fun clearPendingToken()

    fun getLastSentToken(): String?

    fun setLastSentToken(token: String)

    fun clearAll()
}
