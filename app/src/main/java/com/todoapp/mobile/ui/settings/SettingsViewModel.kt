package com.todoapp.mobile.ui.settings

import androidx.lifecycle.ViewModel
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.onSelectedSecretModeChange -> _uiState.update { it.copy(selectedSecretMode = uiAction.label) }
        }
    }
}
