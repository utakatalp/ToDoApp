package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val formattedMinutes = "%02d".format(minutes)
    val formattedSeconds = "%02d".format(seconds)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(if (isOverTime) Color.Red else Color.Green)
            .clickable { onClick() },
        verticalAlignment = Alignment.Bottom,
    ) {
        Spacer(Modifier.weight(1f))
        Box(modifier = Modifier.width(65.dp), contentAlignment = Alignment.Center) {
            TDText(
                text = formattedMinutes,
                    style = TDTheme.typography.heading1.copy(
                    fontSize = 48.sp
                ),
                color = if (isOverTime) TDTheme.colors.lightPurple else TDTheme.colors.purple
            )
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.width(65.dp)) {
            TDText(
                text = formattedSeconds,
                    style = TDTheme.typography.heading1.copy(
                    fontSize = 48.sp
                ),
                color = if (isOverTime) TDTheme.colors.lightPurple else TDTheme.colors.purple
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
