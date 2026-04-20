package com.todoapp.mobile.ui.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Horizontal row of circles representing session progress.
 *
 * The current dot is slightly larger. Past and current dots use [contentColor];
 * future dots use [dimColor].
 */
@Composable
fun PomodoroSessionDots(
    totalSessions: Int,
    currentIndex: Int,
    contentColor: Color,
    dimColor: Color,
    modifier: Modifier = Modifier,
) {
    if (totalSessions <= 0) return

    val displayCount = totalSessions.coerceAtMost(MAX_VISIBLE_DOTS)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 0 until displayCount) {
            val isCurrent = i == currentIndex.coerceAtMost(displayCount - 1)
            val isPast = i < currentIndex
            val dotSize = if (isCurrent) 12.dp else 8.dp
            val dotColor = if (isPast || isCurrent) contentColor else dimColor

            Box(
                modifier =
                Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }
    }
}

private const val MAX_VISIBLE_DOTS = 12
