package com.todoapp.uikit.modifier

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neumorphicShadow(
    lightShadow: Color,
    darkShadow: Color,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 8.dp,
): Modifier = this
    .coloredShadow(
        color = darkShadow,
        cornerRadius = cornerRadius,
        blurRadius = elevation,
        offsetX = elevation * 0.6f,
        offsetY = elevation * 0.6f,
    )
    .coloredShadow(
        color = lightShadow,
        cornerRadius = cornerRadius,
        blurRadius = elevation,
        offsetX = -elevation * 0.6f,
        offsetY = -elevation * 0.6f,
    )

private fun Modifier.coloredShadow(
    color: Color,
    cornerRadius: Dp,
    blurRadius: Dp,
    offsetX: Dp,
    offsetY: Dp,
): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.isAntiAlias = true
        if (blurRadius.value != 0f) {
            frameworkPaint.maskFilter = BlurMaskFilter(
                blurRadius.toPx(),
                BlurMaskFilter.Blur.NORMAL,
            )
        }
        paint.color = color
        canvas.save()
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = cornerRadius.toPx(),
            radiusY = cornerRadius.toPx(),
            paint = paint,
        )
        canvas.restore()
    }
}
