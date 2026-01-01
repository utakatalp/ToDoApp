package com.todoapp.uikit.components

import android.content.res.Configuration
import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todoapp.uikit.theme.TDTheme
import kotlin.math.max

@Composable
fun TDWeeklyBarChart(
    title: String,
    days: List<String>,
    values: List<Int>,
    maxY: Int,
    yLabelColor: Color,
    height: Dp,
    autoScaleHeightToMaxY: Boolean = true,
    minGridStep: Dp = 24.dp,
    barWidth: Dp,
    barColor: Color,
    gridColor: Color,
) {
    if (values.size != days.size) return

    val xAxisHeight = 36.dp
    val yLabelWidth = 24.dp
    val leftGapToPlot = 2.dp

    val density = LocalDensity.current

    val safeMaxY = max(1, maxY)

    val requiredPlotHeight = (safeMaxY * minGridStep.value).dp
    val plotHeight = if (autoScaleHeightToMaxY && requiredPlotHeight > height) requiredPlotHeight else height
    val labelTextSizePx = with(density) { 12.sp.toPx() }
    val labelXOffsetPx = with(density) { 10.dp.toPx() }
    val yLabelPaint = remember(yLabelColor, labelTextSizePx) {
        Paint().apply {
            isAntiAlias = true
            textSize = labelTextSizePx
            textAlign = Paint.Align.RIGHT
            color = yLabelColor.toArgb()
        }
    }

    val dashedLines = remember { PathEffect.dashPathEffect(floatArrayOf(48f, 4f), 0f) }

    TDWeeklyBarChartContent(
        title = title,
        days = days,
        values = values,
        safeMaxY = safeMaxY,
        plotHeight = plotHeight,
        barWidth = barWidth,
        barColor = barColor,
        gridColor = gridColor,
        xAxisHeight = xAxisHeight,
        yLabelWidth = yLabelWidth,
        leftGapToPlot = leftGapToPlot,
        density = density,
        yLabelPaint = yLabelPaint,
        labelXOffsetPx = labelXOffsetPx,
        dashedLines = dashedLines
    )
}

@Composable
private fun TDWeeklyBarChartContent(
    title: String,
    days: List<String>,
    values: List<Int>,
    safeMaxY: Int,
    plotHeight: Dp,
    barWidth: Dp,
    barColor: Color,
    gridColor: Color,
    xAxisHeight: Dp,
    yLabelWidth: Dp,
    leftGapToPlot: Dp,
    density: Density,
    yLabelPaint: Paint,
    labelXOffsetPx: Float,
    dashedLines: PathEffect,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        TDText(
            text = title,
            style = TDTheme.typography.heading1,
            color = TDTheme.colors.onBackground,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(plotHeight + xAxisHeight)
                .padding(horizontal = 16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                val plotLeft = with(density) { (yLabelWidth + leftGapToPlot).toPx() }
                val plotRight = size.width
                val plotTop = 0f
                val plotBottom = with(density) { plotHeight.toPx() }
                val stepPx = (plotBottom - plotTop) / safeMaxY.toFloat()

                for (i in 0..safeMaxY) {
                    val y = plotBottom - i * stepPx

                    drawLine(
                        color = gridColor,
                        start = Offset(plotLeft, y),
                        end = Offset(plotRight, y),
                        strokeWidth = with(density) { 1.dp.toPx() },
                        pathEffect = dashedLines
                    )

                    val label = if (i == 0) "0" else i.toString().padStart(2, '0')

                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            label,
                            plotLeft - labelXOffsetPx,
                            y + yLabelPaint.textSize / 3f,
                            yLabelPaint
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = yLabelWidth + leftGapToPlot)
                    .height(plotHeight + xAxisHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { index, v ->
                    val clamped = v.coerceIn(0, safeMaxY)
                    val target = clamped / safeMaxY.toFloat()

                    val anim by animateFloatAsState(
                        targetValue = target,
                        animationSpec = tween(durationMillis = 650),
                    )

                    Column(
                        modifier = Modifier.height(plotHeight + xAxisHeight),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .height(plotHeight)
                                .width(barWidth),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(anim)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(barColor)
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        TDText(
                            text = days[index],
                            style = TDTheme.typography.regularTextStyle,
                            color = TDTheme.colors.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Composable
fun TDWeeklyBarChartPreviewLight() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TDWeeklyBarChart(
            title = "Task",
            days = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"),
            values = listOf(3, 4, 5, 2, 2, 3, 4),
            maxY = 7,
            yLabelColor = TDTheme.colors.onBackground,
            height = 220.dp,
            barWidth = 26.dp,
            barColor = TDTheme.colors.purple,
            gridColor = TDTheme.colors.gray
        )
    }
}

@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TDWeeklyBarChartPreviewDark() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TDWeeklyBarChart(
            title = "Task",
            days = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"),
            values = listOf(17, 28, 30, 20, 17, 14, 21),
            maxY = 30,
            yLabelColor = TDTheme.colors.onBackground,
            height = 220.dp,
            barWidth = 26.dp,
            barColor = TDTheme.colors.purple,
            gridColor = TDTheme.colors.onBackground
        )
    }
}
