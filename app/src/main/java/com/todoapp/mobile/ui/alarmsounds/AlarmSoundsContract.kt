package com.todoapp.mobile.ui.alarmsounds

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.todoapp.mobile.domain.repository.AlarmSoundOption

object AlarmSoundsContract {
    sealed interface UiState {
        data object Loading : UiState

        @Immutable
        data class Success(
            val items: List<AlarmSoundOption>,
            val selectedUri: Uri,
            val previewingUri: Uri? = null,
        ) : UiState

        data class Error(val message: String) : UiState
    }

    sealed interface UiAction {
        data class OnSelect(val uri: Uri) : UiAction
        data object OnStopPreview : UiAction
    }
}
