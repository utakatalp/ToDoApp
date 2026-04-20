package com.todoapp.mobile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.MainContract.UiEffect
import com.todoapp.mobile.data.repository.DataStoreHelper
import com.todoapp.mobile.domain.engine.PomodoroEngine
import com.todoapp.mobile.domain.repository.AuthEvent
import com.todoapp.mobile.domain.repository.AuthRepository
import com.todoapp.mobile.domain.repository.GroupRepository
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
@Suppress("LongParameterList")
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionPreferences: SessionPreferences,
    private val taskRepository: TaskRepository,
    private val groupRepository: GroupRepository,
    private val dataStoreHelper: DataStoreHelper,
    private val userRepository: UserRepository,
    private val pomodoroEngine: PomodoroEngine,
) : ViewModel() {

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    var isLoggedIn by mutableStateOf<Boolean?>(null)
        private set

    init {
        viewModelScope.launch {
            dataStoreHelper.isLoggedIn.collect {
                Log.d("SPLASH_DEBUG", "isLoggedIn: $it, was null: ${isLoggedIn == null}")
                isLoggedIn = it
            }
        }

        viewModelScope.launch {
            authRepository.events.collect { event ->
                when (event) {
                    is AuthEvent.Logout -> {
                        clearLocalSession()
                        _navEffect.send(
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
        groupRepository.deleteAllLocalGroups()
        pomodoroEngine.finish()
        dataStoreHelper.clearUser()
        dataStoreHelper.setLoggedIn(false)
    }

    private suspend fun refreshUserCache() {
        userRepository.getUserInfo()
            .onSuccess { dataStoreHelper.setUser(it) }
            .onFailure { dataStoreHelper.clearUser() }
    }
}
