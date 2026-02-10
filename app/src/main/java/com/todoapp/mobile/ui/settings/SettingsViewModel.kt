package com.todoapp.mobile.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.data.security.SecretModeEndCondition
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.repository.ThemeRepository
import com.todoapp.mobile.domain.security.SecretModeReopenOptions
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiEffect
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val secretModePreferences: SecretPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    @OptIn(ExperimentalCoroutinesApi::class)
    val secretModeMessageFlow: StateFlow<String> =
        secretModePreferences
            .observeCondition()
            .flatMapLatest { condition -> observeSecretModeMessage(condition) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = "Secret mode is closed."
            )

    init {
        observeTheme()
        viewModelScope.launch {
            val lastSelectedOptionId = secretModePreferences.getLastSelectedOptionId()
            val condition = secretModePreferences.getCondition()
            val isSecretModeActive = condition.isActive(System.currentTimeMillis())
            _uiState.update {
                it.copy(
                    selectedSecretMode = SecretModeReopenOptions.byId(lastSelectedOptionId),
                    isSecretModeActive = isSecretModeActive
                )
            }
        }

        viewModelScope.launch {
            secretModeMessageFlow.collect { value ->
                _uiState.update {
                    it.copy(remainedSecretModeTime = value)
                }
            }
        }
    }

    private fun observeTheme() {
        themeRepository.themeFlow
            .onEach { theme ->
                _uiState.update { it.copy(currentTheme = theme) }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnThemeChange -> viewModelScope.launch { themeRepository.saveTheme(action.theme) }
            is UiAction.OnSelectedSecretModeChange -> updateSelectedSecretMode(action)
            is UiAction.OnSettingsSave -> updateOption()
            is UiAction.OnDisableSecretModeTap -> disableSecretMode()
            is UiAction.OnNavigateToSecretModeSettings -> navigateToSecretModeSettings()
        }
    }

    private fun navigateToSecretModeSettings() {
        _navEffect.trySend(NavigationEffect.Navigate(Screen.SecretMode))
    }

    private fun updateSelectedSecretMode(uiAction: UiAction.OnSelectedSecretModeChange) {
        _uiState.update { it.copy(selectedSecretMode = uiAction.label) }
    }

    private fun disableSecretMode() {
        viewModelScope.launch {
            secretModePreferences.saveCondition(SecretModeEndCondition.Disabled)
        }
    }

    private fun updateOption() {
        val selectedOptionIdAtClick = uiState.value.selectedSecretMode.id
        viewModelScope.launch {
            secretModePreferences.setLastSelectedOptionId(selectedOptionIdAtClick)
        }
    }

    private fun observeSecretModeMessage(condition: SecretModeEndCondition): Flow<String> = flow {
        val now = System.currentTimeMillis()

        if (!condition.isActive(now)) {
            emit("Secret mode is closed.")
            return@flow
        }

        when (condition) {
            is SecretModeEndCondition.Disabled -> {
                emit("Secret mode is closed.")
            }

            is SecretModeEndCondition.UntilEvent -> {
                emit("Secret mode is open until the app closed.")
            }

            is SecretModeEndCondition.UntilTime -> {
                var remainingMillis = condition.epochMillis - now
                while (remainingMillis >= 0L) {
                    val formatted = formatMillisToMinuteSecond(remainingMillis)
                    emit("Secret mode will be open for $formatted.")
                    delay(INTERVAL)
                    remainingMillis -= INTERVAL
                }

                emit("Secret mode is closed.")
            }
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
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
