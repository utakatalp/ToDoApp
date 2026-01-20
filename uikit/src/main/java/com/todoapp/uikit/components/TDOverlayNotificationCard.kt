package com.todoapp.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TDOverlayNotificationCard(
    message: String,
    show: Boolean,
    modifier: Modifier = Modifier,
    onDismissClick: () -> Unit = {},
    onOpenClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = show,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight }
            ) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            NotificationCardContent(
                message = message,
                onDismissClick = onDismissClick,
                onOpenClick = onOpenClick
            )
        }
    }
}

@Composable
private fun NotificationCardContent(
    message: String,
    onDismissClick: () -> Unit = {},
    onOpenClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            shadowElevation = 10.dp,
            color = Color(0xFF121826), // dark card
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top row: icon + texts + time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF2D6BFF)), // accent
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⏰", fontSize = 20.sp)
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Reminder",
                            fontSize = 14.sp,
                            color = Color(0xFFB7C0D8)
                        )
                        Text(
                            text = message,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Tap an action below",
                            fontSize = 12.sp,
                            color = Color(0xFF8B95B3)
                        )
                    }

                    Text(
                        text = "now",
                        fontSize = 12.sp,
                        color = Color(0xFF8B95B3)
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E2A44),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    onDismissClick()
                                }
                        ) {
                            Text("Dismiss", color = Color(0xFFB7C0D8), fontSize = 14.sp)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF2D6BFF),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    onOpenClick()
                                }
                        ) {
                            Text("Open", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF313131)
@Composable
fun OverlayNotificationCardPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        TDOverlayNotificationCard(
            message = "Go to the gym and do cardio",
            show = true
        )
    }
}

@Preview(showBackground = true, name = "Card Content Only")
@Composable
private fun NotificationCardContentPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        NotificationCardContent(message = "Meeting with the team at 2 PM")
    }
}
