package com.todoapp.mobile.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.security.SecretModeReopenOption
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val secretModePreferences: SecretPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(selectedSecretMode = SecretModeReopenOption.fromStorage(secretModePreferences.getSelectedOption()))
    )
    val uiState = _uiState.asStateFlow()
    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnSelectedSecretModeChange -> _uiState.update { it.copy(selectedSecretMode = uiAction.label) }
            is UiAction.OnSettingsSave -> updateOption()
        }
    }

    init {
        viewModelScope.launch {
            getSecretModeEnabledTimeAmountFlow().collect { value ->
                _uiState.update {
                    it.copy(remainedSecretModeTime = value)
                }
            }
        }
    }

    private fun updateOption() {
        secretModePreferences.setSelectedOption(uiState.value.selectedSecretMode.name)
    }

    private fun getSecretModeEnabledTimeAmountFlow(): Flow<String> = flow {
        while (true) {
            val value = if (secretModePreferences.isSecretModeEnabled()) {
                val untilTimeMillis = secretModePreferences.getSecretModeEnabledUntil()

                if (untilTimeMillis == -1L) {
                    "Secret mode will be open until the app is closed."
                } else {
                    val remainingMillis = untilTimeMillis - System.currentTimeMillis()

                    if (remainingMillis >= 0L) {
                        val formattedTime = formatMillisToMinuteSecond(remainingMillis)
                        "Secret mode will be open for $formattedTime ."
                    } else {
                        "Secret mode is closed."
                    }
                }
            } else {
                "Secret mode is closed."
            }
            emit(value)
            delay(INTERVAL)
        }
    }
    private fun formatMillisToMinuteSecond(millis: Long): String {
        if (millis <= ZERO_MILLIS) return DEFAULT_TIME_FORMAT

        val totalSeconds = millis / MILLIS_IN_SECOND
        val minutes = totalSeconds / SECONDS_IN_MINUTE
        val seconds = totalSeconds % SECONDS_IN_MINUTE

        return String.format(TIME_FORMAT_PATTERN, minutes, seconds)
    }
    private companion object {
        private const val INTERVAL = 1_000L
        private const val ZERO_MILLIS = 0L
        private const val MILLIS_IN_SECOND = 1_000L
        private const val SECONDS_IN_MINUTE = 60L
        private const val DEFAULT_TIME_FORMAT = "00:00"
        private const val TIME_FORMAT_PATTERN = "%02d:%02d"
    }
}
