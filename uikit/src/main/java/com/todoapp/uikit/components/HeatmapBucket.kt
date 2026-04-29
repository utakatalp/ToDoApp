package com.todoapp.uikit.components

import androidx.compose.ui.graphics.Color
import com.todoapp.uikit.theme.TDColor

internal const val HEATMAP_THRESHOLD_LIGHT = 1
internal const val HEATMAP_THRESHOLD_MID_END = 3

internal fun heatmapBucketColor(count: Int, colors: TDColor): Color = when {
    count <= 0 -> colors.lightGray
    count == HEATMAP_THRESHOLD_LIGHT -> colors.lightGreen
    count in (HEATMAP_THRESHOLD_LIGHT + 1)..HEATMAP_THRESHOLD_MID_END -> colors.mediumGreen
    else -> colors.darkGreen
}
