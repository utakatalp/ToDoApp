package com.todoapp.uikit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.previews.TDPreviewWide
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDTaskCard(
    modifier: Modifier = Modifier,
    taskTitle: String,
    taskTimeStart: String,
    taskTimeEnd: String,
    isCompleted: Boolean = false,
    description: String? = null,
    onClick: () -> Unit = {},
) {
    val cardBg by animateColorAsState(
        targetValue = if (isCompleted) TDTheme.colors.lightGreen else TDTheme.colors.lightPending,
        animationSpec = tween(300),
        label = "cardBg",
    )
    val accentColor = if (isCompleted) TDTheme.colors.mediumGreen else TDTheme.colors.purple

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VerticalDivider(
            thickness = 3.dp,
            color = accentColor,
            modifier = Modifier.height(48.dp),
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            TDText(
                text = taskTitle,
                style = TDTheme.typography.heading7.copy(
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                ),
                color = TDTheme.colors.onBackground,
            )
            if (!description.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                TDText(
                    text = description,
                    style = TDTheme.typography.subheading1,
                    color = TDTheme.colors.onBackground.copy(alpha = 0.6f),
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(4.dp))
            TDText(
                text = "$taskTimeStart - $taskTimeEnd",
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.onBackground.copy(alpha = 0.7f),
            )
        }
        Spacer(Modifier.width(8.dp))
        TDTaskStatusLabel(isCompleted = isCompleted)
    }
}

@TDPreview
@Composable
fun TDTaskCardPreview() {
    TDTheme {
        TDTaskCard(
            taskTitle = "Read Book",
            taskTimeStart = "09:30",
            taskTimeEnd = "10:15",
            isCompleted = false,
            description = "Read chapter 5 of Clean Code",
        )
    }
}

@TDPreview
@Composable
fun TDTaskCardCompletedPreview() {
    TDTheme {
        TDTaskCard(
            taskTitle = "Morning Run",
            taskTimeStart = "07:00",
            taskTimeEnd = "07:45",
            isCompleted = true,
            description = "5km around the park",
        )
    }
}

@TDPreviewWide
@Composable
fun TDTaskCardLongTitlePreview() {
    TDTheme {
        TDTaskCard(
            modifier = Modifier.fillMaxWidth(),
            taskTitle = "Prepare Presentation Slides",
            taskTimeStart = "14:00",
            taskTimeEnd = "16:30",
        )
    }
}
