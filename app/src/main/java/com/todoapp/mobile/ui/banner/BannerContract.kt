package com.todoapp.mobile.ui.banner

object BannerContract {

    data class UiState(
        val minutes: Int? = null,
        val seconds: Int? = null,
        val isBannerActivated: Boolean = false,
        val isOverTime: Boolean? = null,
        val isVisible: Boolean = true,
    )
    sealed interface UiEffect {
        data object SessionFinished : UiEffect
    }
    sealed interface UiAction {
        data object OnBannerTap : UiAction
    }
}
