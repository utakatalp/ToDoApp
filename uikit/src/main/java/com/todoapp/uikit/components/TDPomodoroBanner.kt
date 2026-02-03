package com.todoapp.uikit.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDPomodoroBanner(
    isBannerActivated: Boolean,
    minutes: Int = 25,
    seconds: Int = 40,
    isOverTime: Boolean,
    onClick: () -> Unit,
) {
    if (!isBannerActivated) return
    var started by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        started = true
    }

    val bannerHeight by animateDpAsState(
        targetValue = if (started) 60.dp else 0.dp,
        animationSpec = tween(800),
        label = "Banner Height Animation"
    )

    val statusBarTopPadding = WindowInsets.statusBars
        .asPaddingValues()
        .calculateTopPadding()

    val animatedStatusBarTopPadding by animateDpAsState(
        targetValue = if (started) statusBarTopPadding else 0.dp,
        animationSpec = tween(800),
        label = "StatusBarTopPaddingAnimation"
    )

    val targetBackgroundColor = if (isOverTime) TDTheme.colors.red else TDTheme.colors.green
    val backgroundColor = remember { Animatable(targetBackgroundColor) }

    LaunchedEffect(targetBackgroundColor) {
        backgroundColor.animateTo(targetBackgroundColor)
    }

    val color = if (isOverTime) TDTheme.colors.lightPurple else TDTheme.colors.purple
    val style = TDTheme.typography.heading1.copy(fontSize = 48.sp)

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .background(backgroundColor.value)
            .padding(top = animatedStatusBarTopPadding)
            .fillMaxWidth()
            .height(bannerHeight),
        verticalAlignment = Alignment.Bottom,
    ) {
        Spacer(Modifier.weight(1f))
        Box(modifier = Modifier.width(140.dp)) {
            AnimatedTimeMmSs(
                minutes = minutes,
                seconds = seconds,
                style = style,
                color = color
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
fun TDPomodoroBannerPreview_Normal() {
    TDTheme {
        TDPomodoroBanner(
            isBannerActivated = true,
            minutes = 25,
            seconds = 0,
            isOverTime = false,
            onClick = {}
        )
    }
}

@Composable
fun AnimatedTimeMmSs(
    minutes: Int,
    seconds: Int,
    modifier: Modifier = Modifier,
    digitModifier: Modifier = Modifier,
    style: TextStyle,
    color: Color
) {
    val safeMinutes = minutes.coerceAtLeast(0)
    val safeSeconds = (seconds.coerceAtLeast(0))

    val mTens = (safeMinutes / 10) % 10
    val mOnes = safeMinutes % 10
    val sTens = (safeSeconds / 10) % 10
    val sOnes = safeSeconds % 10

    Row(modifier = modifier) {
        AnimatedDigit(mTens, modifier = digitModifier, style, color)
        AnimatedDigit(mOnes, modifier = digitModifier, style, color)

        TDText(text = ":", style = style, color = color)

        AnimatedDigit(sTens, modifier = digitModifier, style, color)
        AnimatedDigit(sOnes, modifier = digitModifier, style, color)
    }
}

@Composable
private fun AnimatedDigit(
    digit: Int,
    modifier: Modifier = Modifier,
    style: TextStyle,
    color: Color
) {
    AnimatedContent(
        targetState = digit,
        label = "timer_digit",
        transitionSpec = {
            (slideInVertically { it } + fadeIn()) togetherWith
                    (slideOutVertically { -it } + fadeOut())
        }
    ) { d ->
        TDText(
            text = d.toString(),
            modifier = modifier,
            style = style,
            color = color
        )
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
fun TDPomodoroBannerPreview_OverTime() {
    TDTheme {
        TDPomodoroBanner(
            isBannerActivated = true,
            minutes = 0,
            seconds = 42,
            isOverTime = true,
            onClick = {}
        )
    }
}
