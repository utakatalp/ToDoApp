package com.todoapp.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.previews.TDPreviewDialog
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.math.roundToInt

@Composable
fun TDOverlayDailyPlanNotificationCard(
    isVisible: Boolean,
    onOpenApp: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismissMillis: Int = 10_000,
    initialPosition: Flow<Offset> = flowOf(Offset.Zero),
    onPositionChange: (Offset) -> Unit = {},
) {
    val position by initialPosition.collectAsState(initial = Offset.Zero)
    var offsetX by remember(position) { mutableFloatStateOf(position.x) }
    var offsetY by remember(position) { mutableFloatStateOf(position.y) }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "alpha"
    )

    LaunchedEffect(isVisible, autoDismissMillis) {
        if (isVisible) {
            delay(autoDismissMillis.toLong())
            onDismiss()
        }
    }

    if (alpha > 0f) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter =
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                    ) + fadeIn(),
                exit =
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                    ) + fadeOut(),
            ) {
                LaunchedEffect(isVisible, autoDismissMillis) {
                    if (isVisible) {
                        delay(autoDismissMillis.toLong())
                        onDismiss()
                    }
                }

                val overlayTextColor = TDTheme.colors.onBackground
                val overlaySurfaceColor = TDTheme.colors.background

                Surface(
                    modifier =
                        Modifier
                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        onPositionChange(Offset(offsetX, offsetY))
                                    }
                                ) { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .widthIn(max = 520.dp)
                            .clickable { onOpenApp() },
                    shape = RoundedCornerShape(22.dp),
                    tonalElevation = 6.dp,
                    color = overlaySurfaceColor,
                    contentColor = overlayTextColor,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(TDTheme.colors.background.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "🗓️",
                                style = MaterialTheme.typography.titleMedium,
                                color = overlayTextColor,
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.daily_plan_notification_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = overlayTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = stringResource(R.string.daily_plan_notification_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = overlayTextColor.copy(alpha = 0.8f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable { onDismiss() },
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = "Close",
                                    tint = overlayTextColor.copy(alpha = 0.85f),
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@TDPreviewDialog
@Composable
fun TDOverlayDailyPlanNotificationCardPreview() {
    TDTheme {
        TDOverlayDailyPlanNotificationCard(
            isVisible = true,
            onOpenApp = {},
            onDismiss = {},
        )
    }
}
