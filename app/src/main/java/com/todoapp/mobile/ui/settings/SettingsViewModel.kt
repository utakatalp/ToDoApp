package com.todoapp.mobile.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.repository.ThemeRepository
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiEffect
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        observeTheme()
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
            is UiAction.OnThemeChange -> {
                viewModelScope.launch {
                    themeRepository.saveTheme(action.theme)
                }
            }

            UiAction.OnBackClick -> {
                viewModelScope.launch {
                    _uiEffect.send(UiEffect.NavigateToBack)
                }
            }
        }
    }
}
