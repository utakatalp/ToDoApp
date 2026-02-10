package com.todoapp.mobile.ui.banner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.todoapp.mobile.common.RingtoneHolder
import com.todoapp.mobile.ui.banner.BannerContract.UiAction
import com.todoapp.mobile.ui.banner.BannerContract.UiState
import com.todoapp.uikit.components.TDPomodoroBanner
import kotlinx.coroutines.flow.Flow

@Composable
fun BannerOverlay(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
    uiEffect: Flow<BannerContract.UiEffect>,
) {
    val ringToneHolder = remember { RingtoneHolder() }
    val context = LocalContext.current

    LaunchedEffect(uiEffect) {
        uiEffect.collect { effect ->
            when (effect) {
                is BannerContract.UiEffect.SessionFinished -> {
                    ringToneHolder.play(context)
                }
            }
        }
    }

    BannerContent(uiState, onAction)
}

@Composable
fun BannerContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    if (!uiState.isVisible) return
    TDPomodoroBanner(
        minutes = uiState.minutes!!,
        seconds = uiState.seconds!!,
        isBannerActivated = uiState.isBannerActivated,
        isOverTime = uiState.isOverTime!!,
        onClick = { onAction(UiAction.OnBannerTap) },
    )
}
