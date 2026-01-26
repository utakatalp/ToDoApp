package com.todoapp.uikit.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDGeneralProgressBar(
    progress: Float,
) {
    val height = 32.dp
    val barColor = TDTheme.colors.lightPurple
    val progressColor = TDTheme.colors.purple
    val p = progress.coerceIn(0f, 1f)
    val animatedP by animateFloatAsState(
        targetValue = p,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(barColor)
            .height(height)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedP)
                .background(progressColor)
        )
    }
}

@Preview("Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun TDGeneralProgressBarPreviewLight() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TDGeneralProgressBar(
            progress = 0.7f,
        )
    }
}
