package com.todoapp.uikit.components

import android.content.res.Configuration
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDWeeklyCircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color = TDTheme.colors.purple,
    trackColor: Color = TDTheme.colors.lightPurple,
    strokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
    gapSize: Dp = ProgressIndicatorDefaults.CircularIndicatorTrackGapSize,
    animationSpec: AnimationSpec<Float> = ProgressIndicatorDefaults.ProgressAnimationSpec,
) {
    val target = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = target,
        animationSpec = animationSpec,
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center,
        ) {
            TDText(
                text = "%${(100 * progress).roundToInt()}",
                color = TDTheme.colors.onBackground,
                style = TDTheme.typography.heading7,
                textAlign = TextAlign.Center,
            )

            CircularProgressIndicator(
                modifier = modifier,
                progress = { animatedProgress },
                color = color,
                trackColor = trackColor,
                strokeWidth = strokeWidth,
                strokeCap = strokeCap,
                gapSize = gapSize,
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start,
        ) {
            TDText(
                text = stringResource(id = R.string.activity_screen_weekly_progress_text),
                color = TDTheme.colors.onBackground,
                style = TDTheme.typography.heading5,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LegendColoredBoxItem(
                color = TDTheme.colors.purple,
                text = stringResource(id = R.string.activity_screen_weekly_progress_legend_complete_text),
            )

            Spacer(modifier = Modifier.height(4.dp))

            LegendColoredBoxItem(
                color = TDTheme.colors.lightPurple,
                text = stringResource(id = R.string.activity_screen_weekly_progress_legend_in_progress_text),
            )

            Spacer(modifier = Modifier.height(4.dp))

            LegendColoredBoxItem(
                color = TDTheme.colors.gray,
                text = stringResource(id = R.string.activity_screen_weekly_progress_legend_not_started_text),
            )
        }
    }
}

@Composable
fun LegendColoredBoxItem(
    color: Color,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color)
        )

        Spacer(modifier = Modifier.width(8.dp))

        TDText(
            text = text,
            color = TDTheme.colors.onBackground,
            style = TDTheme.typography.subheading1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview("Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun TDWeeklyCircularProgressIndicatorPreviewLight(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        TDTheme {
            TDWeeklyCircularProgressIndicator(
                modifier = modifier.size(100.dp),
                progress = 0.65f,
                color = TDTheme.colors.purple,
                trackColor = TDTheme.colors.lightPurple,
                strokeWidth = 14.dp,
                strokeCap = Butt,
                gapSize = 0.dp,
            )
        }
    }
}

@Preview("Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TDWeeklyCircularProgressIndicatorPreviewDark(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        TDTheme {
            TDWeeklyCircularProgressIndicator(
                modifier = modifier.size(100.dp),
                progress = 0.65f,
                color = TDTheme.colors.purple,
                trackColor = TDTheme.colors.lightPurple,
                strokeWidth = 14.dp,
                strokeCap = Butt,
                gapSize = 0.dp,
            )
        }
    }
}
