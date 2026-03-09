package com.todoapp.mobile.ui.secondarytopbar

import androidx.lifecycle.ViewModel
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.navigation.Screen
import com.todoapp.mobile.ui.secondarytopbar.SecondaryTopBarContract.UiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class SecondaryTopBarViewModel @Inject constructor() : ViewModel() {

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.NavigateToActivity -> _navEffect.trySend(
                NavigationEffect.Navigate(Screen.GroupDetails.Overview(uiAction.groupId))
            )
            is UiAction.NavigateToMembers -> _navEffect.trySend(
                NavigationEffect.Navigate(Screen.GroupDetails.Members(uiAction.groupId))
            )
            is UiAction.NavigateToOverview -> _navEffect.trySend(
                NavigationEffect.Navigate(Screen.GroupDetails.Activity(uiAction.groupId))
            )
        }
    }
}
