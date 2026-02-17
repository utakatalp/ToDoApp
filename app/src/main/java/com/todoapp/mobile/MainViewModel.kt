package com.todoapp.mobile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.MainContract.UiEffect
import com.todoapp.mobile.data.repository.DataStoreHelper
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.repository.AuthEvent
import com.todoapp.mobile.domain.repository.AuthRepository
import com.todoapp.mobile.domain.repository.SessionPreferences
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.domain.repository.UserRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionPreferences: SessionPreferences,
    private val taskRepository: TaskRepository,
    private val dataStoreHelper: DataStoreHelper,
    private val userRepository: UserRepository,
    private val pomodoroEngine: PomodoroEngine,
) : ViewModel() {

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.events.collect { event ->
                when (event) {
                    is AuthEvent.Logout -> {
                        clearLocalSession()
                        _navEffect.trySend(
                            NavigationEffect.NavigateClearingBackstack(Screen.Onboarding)
                        )
                    }

                    is AuthEvent.ForceLogout -> {
                        _uiEffect.send(
                            UiEffect.ShowDialog(
                                "Your session has expired. Please log in again."
                            )
                        )
                        clearLocalSession()
                    }
                }
            }
        }

        viewModelScope.launch {
            refreshUserCache()
        }
    }

    fun onAction(action: MainContract.UiAction) {
        when (action) {
            MainContract.UiAction.OnDialogOkTap ->
                _navEffect.trySend(
                    NavigationEffect.NavigateClearingBackstack(Screen.Onboarding)
                )
        }
    }

    private suspend fun clearLocalSession() {
        sessionPreferences.clear()
        taskRepository.deleteAllTasks()
        pomodoroEngine.finish()
        dataStoreHelper.clearUser()
    }

    private suspend fun refreshUserCache() {
        userRepository.getUserInfo()
            .onSuccess { dataStoreHelper.setUser(it) }
            .onFailure { dataStoreHelper.clearUser() }
    }
}
