package com.todoapp.mobile.ui.banner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.engine.PomodoroEvent
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BannerViewModel @Inject constructor(
    private val pomodoroEngine: PomodoroEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(BannerContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<BannerContract.UiEffect>() }
    val uiEffect: Flow<BannerContract.UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    fun onAction(action: BannerContract.UiAction) {
        when (action) {
            BannerContract.UiAction.OnBannerTap -> onBannerTap()
        }
    }

    init {
        observeEngineState()
        observeEngineEvents()
    }

    private fun onBannerTap() {
        _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro))
    }

    private fun observeEngineState() {
        viewModelScope.launch {
            pomodoroEngine.state.collect { state ->
                val (minutes, seconds) = toMinutesSeconds(state.remainingSeconds)
                _uiState.update {
                    it.copy(
                        minutes = minutes,
                        seconds = seconds,
                        isBannerActivated = state.isRunning,
                        isOverTime = state.isOvertime,
                        isVisible = state.isVisible,
                    )
                }
            }
        }
    }

    private fun observeEngineEvents() {
        viewModelScope.launch {
            pomodoroEngine.events.collect { event ->
                when (event) {
                    PomodoroEvent.SessionFinished -> _uiEffect.send(BannerContract.UiEffect.SessionFinished)
                    else -> { }
                }
            }
        }
    }

    private fun toMinutesSeconds(totalSeconds: Long): Pair<Int, Int> {
        val minutes = (totalSeconds / SECONDS_PER_MINUTE).toInt()
        val seconds = (totalSeconds % SECONDS_PER_MINUTE).toInt()
        return minutes to seconds
    }

    private companion object {
        private const val SECONDS_PER_MINUTE: Long = 60L
    }
}
