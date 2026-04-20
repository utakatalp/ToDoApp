package com.todoapp.uikit.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.theme.TDTheme
import kotlin.math.roundToInt

@Composable
fun TDGeneralProgressBar(
    progress: Float,
    completedCount: Int? = null,
    totalCount: Int? = null,
) {
    val height = 32.dp
    val barColor = TDTheme.colors.pendingGray
    val progressColor = TDTheme.colors.mediumGreen
    val p = progress.coerceIn(0f, 1f)
    val animatedP by animateFloatAsState(
        targetValue = p,
        label = "progressBar",
    )

    Column {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .background(barColor)
                .height(height),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(animatedP)
                    .background(progressColor),
            )
            TDText(
                text = "${(p * 100).roundToInt()}%",
                style = TDTheme.typography.regularTextStyle,
                color = Color.White,
            )
        }

        if (completedCount != null && totalCount != null) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TDText(
                    text = stringResource(com.example.uikit.R.string.general_progress_tasks_done, completedCount),
                    style = TDTheme.typography.regularTextStyle,
                    color = TDTheme.colors.onBackground,
                )
                TDText(
                    text = stringResource(com.example.uikit.R.string.general_progress_total, totalCount),
                    style = TDTheme.typography.regularTextStyle,
                    color = TDTheme.colors.onBackground,
                )
            }
        }
    }
}

@TDPreview
@Composable
fun TDGeneralProgressBarPreviewLight() {
    TDTheme {
        Column(
            modifier =
            Modifier
                .fillMaxWidth(),
        ) {
            TDGeneralProgressBar(
                progress = 0.7f,
            )
        }
    }
}
