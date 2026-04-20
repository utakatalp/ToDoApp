package com.todoapp.mobile.ui.groups.groupdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
internal fun PriorityBadge(
    priority: String,
    modifier: Modifier = Modifier,
) {
    val normalized = priority.uppercase()
    val (bg, fg, label) =
        when (normalized) {
            "HIGH" -> Triple(TDTheme.colors.lightRed, TDTheme.colors.crossRed, "HIGH")
            "MEDIUM" -> Triple(TDTheme.colors.lightOrange, TDTheme.colors.orange, "MED")
            "LOW" -> Triple(TDTheme.colors.lightPending, TDTheme.colors.darkPending, "LOW")
            else -> Triple(TDTheme.colors.lightPending, TDTheme.colors.pendingGray, normalized)
        }
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(bg)
                .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        TDText(
            text = label,
            style = TDTheme.typography.subheading1,
            color = fg,
        )
    }
}
