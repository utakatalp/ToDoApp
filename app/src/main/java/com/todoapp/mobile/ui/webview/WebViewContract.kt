package com.todoapp.mobile.ui.webview

object WebViewContract {

    sealed interface UiAction {
        data object OnCloseWebView : UiAction
    }
    sealed interface UiEffect {
        data class OpenWebApp(val url: String) : UiEffect
    }
}
