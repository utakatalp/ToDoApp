package com.todoapp.uikit.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDChatThinkingIndicator(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
            .background(TDTheme.colors.lightPending)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        ThinkingDot(delayMillis = 0)
        Spacer(Modifier.width(6.dp))
        ThinkingDot(delayMillis = 160)
        Spacer(Modifier.width(6.dp))
        ThinkingDot(delayMillis = 320)
    }
}

@Composable
private fun ThinkingDot(delayMillis: Int) {
    val transition = rememberInfiniteTransition(label = "td_chat_thinking")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 480, easing = LinearEasing, delayMillis = delayMillis),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "td_chat_thinking_alpha_$delayMillis",
    )
    Spacer(
        modifier = Modifier
            .size(7.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(TDTheme.colors.darkPending),
    )
}

@TDPreview
@Composable
private fun TDChatThinkingIndicatorPreview() {
    TDTheme {
        TDChatThinkingIndicator(modifier = Modifier.padding(16.dp))
    }
}
