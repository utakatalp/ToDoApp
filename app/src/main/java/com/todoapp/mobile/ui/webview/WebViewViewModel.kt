package com.todoapp.mobile.ui.webview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.webview.WebViewContract.UiAction
import com.todoapp.mobile.ui.webview.WebViewContract.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        val url = savedStateHandle.get<String>("url")
        viewModelScope.launch {
            _uiEffect.send(UiEffect.OpenWebApp(url.toString()))
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.OnCloseWebView -> _navEffect.trySend(NavigationEffect.Back)
        }
    }
}
