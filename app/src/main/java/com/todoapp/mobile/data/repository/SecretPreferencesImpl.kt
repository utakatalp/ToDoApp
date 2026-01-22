package com.todoapp.mobile.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.security.SecretModeReopenOption
import java.time.Clock
import javax.inject.Inject

class SecretPreferencesImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val clock: Clock = Clock.systemUTC(),
) : SecretPreferences {
    override fun setSelectedOption(value: String) {
        sharedPreferences.edit { putString(KEY_SELECTED_OPTION, value) }
    }
    override fun getSelectedOption(): String? {
        return sharedPreferences.getString(KEY_SELECTED_OPTION, null)
    }

    override fun isSecretModeEnabled(): Boolean {
        if (sharedPreferences.contains(SECRET_MODE_UNTIL)) {
            val secretModeUntil = sharedPreferences.getLong(SECRET_MODE_UNTIL, 0L)
            if (secretModeUntil == UNTIL_APP_CLOSED) {
                return true
            }
            return clock.millis() < secretModeUntil
        } else {
            return false
        }
    }

    override fun getSecretModeEnabledUntil(): Long {
        if (sharedPreferences.contains(SECRET_MODE_UNTIL)) {
            return sharedPreferences.getLong(SECRET_MODE_UNTIL, 0L)
        }
        return 0
    }

    override fun setSecretModeEnabledUntil() {
        SecretModeReopenOption.fromStorage(getSelectedOption()).delay.let {
            if (it != null) {
                sharedPreferences.edit { putLong(SECRET_MODE_UNTIL, clock.millis() + it.toMillis()) }
            } else {
                sharedPreferences.edit { putLong(SECRET_MODE_UNTIL, UNTIL_APP_CLOSED) }
            }
        }
    }

    override fun clearSecretMode() {
        sharedPreferences.edit { remove(SECRET_MODE_UNTIL) }
    }

    companion object {
        private const val SECRET_MODE_UNTIL = "secret_mode_until"
        private const val KEY_SELECTED_OPTION = "secret_mode_selected_option"
        private const val UNTIL_APP_CLOSED = -1L
    }
}
