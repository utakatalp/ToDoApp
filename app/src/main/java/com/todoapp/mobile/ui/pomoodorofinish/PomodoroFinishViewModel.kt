package com.todoapp.mobile.ui.pomoodorofinish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroFinishViewModel @Inject constructor() : ViewModel() {

    private val _navEffect = Channel<NavigationEffect>(Channel.BUFFERED)
    val navEffect = _navEffect.receiveAsFlow()

    private val _uiEffect = Channel<PomodoroFinishContract.UiAction>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onAction(action: PomodoroFinishContract.UiAction) {
        when (action) {
            PomodoroFinishContract.UiAction.OnRestartTap -> onRestartTap()
            PomodoroFinishContract.UiAction.OnEditSettingsTap -> onEditSettingsTap()
            PomodoroFinishContract.UiAction.OnDismiss -> onDismiss()
        }
    }

    private fun onRestartTap() {
        viewModelScope.launch {
            _navEffect.trySend(NavigationEffect.Navigate(Screen.Pomodoro, Screen.Home))
        }
    }

    private fun onEditSettingsTap() {
        _navEffect.trySend(NavigationEffect.Navigate(Screen.AddPomodoroTimer, Screen.Home))
    }

    private fun onDismiss() {
        _navEffect.trySend(NavigationEffect.Back)
    }
}
