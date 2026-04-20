package com.todoapp.mobile.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.profile.ProfileContract.UiAction
import com.todoapp.mobile.ui.profile.ProfileContract.UiEffect
import com.todoapp.mobile.ui.profile.ProfileContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    init {
        load()
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnDisplayNameChange -> _uiState.update {
                it.copy(editedDisplayName = action.value)
            }
            UiAction.OnSaveName -> saveName()
            is UiAction.OnAvatarPicked -> uploadAvatar(action.bytes, action.mimeType)
            UiAction.OnBack -> _navEffect.trySend(NavigationEffect.Back)
        }
    }

    private fun load() {
        viewModelScope.launch {
            userRepository.getUserInfo().onSuccess { user ->
                _uiState.value = UiState(
                    isLoading = false,
                    userId = user.id,
                    email = user.email,
                    displayName = user.displayName,
                    editedDisplayName = user.displayName,
                    avatarUrl = user.avatarUrl,
                    avatarVersion = System.currentTimeMillis(),
                )
            }.onFailure { t ->
                _uiState.update { it.copy(isLoading = false, errorMessage = t.message) }
            }
        }
    }

    private fun saveName() {
        val state = _uiState.value
        val trimmed = state.editedDisplayName.trim()
        if (trimmed.isBlank() || trimmed == state.displayName) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            userRepository.updateDisplayName(trimmed)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            displayName = user.displayName,
                            editedDisplayName = user.displayName,
                        )
                    }
                    _uiEffect.trySend(UiEffect.ShowToast("Profile updated"))
                }
                .onFailure { t ->
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEffect.trySend(UiEffect.ShowToast(t.message ?: "Failed to update profile"))
                }
        }
    }

    private fun uploadAvatar(bytes: ByteArray, mimeType: String) {
        _uiState.update { it.copy(isUploading = true) }
        viewModelScope.launch {
            userRepository.uploadAvatar(bytes, mimeType)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isUploading = false,
                            avatarUrl = user.avatarUrl,
                            avatarVersion = System.currentTimeMillis(),
                        )
                    }
                    _uiEffect.trySend(UiEffect.ShowToast("Photo updated"))
                }
                .onFailure { t ->
                    _uiState.update { it.copy(isUploading = false) }
                    _uiEffect.trySend(UiEffect.ShowToast(t.message ?: "Failed to upload photo"))
                }
        }
    }
}
