package com.todoapp.mobile.ui.alarmsounds

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.common.RingtoneHolder
import com.todoapp.mobile.domain.repository.AlarmSoundPreferences
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.alarmsounds.AlarmSoundsContract.UiAction
import com.todoapp.mobile.ui.alarmsounds.AlarmSoundsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmSoundsViewModel @Inject constructor(
    private val preferences: AlarmSoundPreferences,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private val ringtone = RingtoneHolder()

    init {
        load()
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnSelect -> select(action.uri)
            UiAction.OnStopPreview -> {
                ringtone.stop()
                _uiState.update {
                    if (it is UiState.Success) it.copy(previewingUri = null) else it
                }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            val currentUri = preferences.currentAlarmSoundUri()
            val items = preferences.systemAlarmSounds()
            _uiState.value = if (items.isEmpty()) {
                UiState.Error(message = "No alarm sounds available on this device")
            } else {
                UiState.Success(items = items, selectedUri = currentUri)
            }
        }
    }

    private fun select(uri: Uri) {
        viewModelScope.launch {
            preferences.saveAlarmSoundUri(uri)
            _uiState.update {
                if (it is UiState.Success) it.copy(selectedUri = uri, previewingUri = uri) else it
            }
            ringtone.play(context = appContext, explicitUri = uri, autoStopMillis = PREVIEW_MILLIS)
        }
    }

    override fun onCleared() {
        ringtone.stop()
        super.onCleared()
    }

    private companion object {
        const val PREVIEW_MILLIS = 2_000L
    }
}
