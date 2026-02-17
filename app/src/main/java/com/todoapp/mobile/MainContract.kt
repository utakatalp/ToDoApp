package com.todoapp.mobile

object MainContract {

    sealed interface UiEffect {
        data class ShowDialog(val message: String) : UiEffect
    }

    sealed interface UiAction {
        data object OnDialogOkTap : UiAction
    }
}
