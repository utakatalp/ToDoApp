package com.todoapp.mobile.domain.repository

interface SecretPreferences {
    fun setSelectedOption(value: String)
    fun getSelectedOption(): String?
    fun isSecretModeEnabled(): Boolean
    fun setSecretModeEnabledUntil()
    fun getSecretModeEnabledUntil(): Long
    fun clearSecretMode()
}
