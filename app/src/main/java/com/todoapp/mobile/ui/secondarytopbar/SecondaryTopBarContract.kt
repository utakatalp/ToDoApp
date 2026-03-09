package com.todoapp.mobile.ui.secondarytopbar

object SecondaryTopBarContract {
    sealed interface UiAction {
        data class NavigateToOverview(val groupId: String) : UiAction
        data class NavigateToMembers(val groupId: String) : UiAction
        data class NavigateToActivity(val groupId: String) : UiAction
    }
}
