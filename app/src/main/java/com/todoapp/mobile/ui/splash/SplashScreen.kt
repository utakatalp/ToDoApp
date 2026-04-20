package com.todoapp.mobile.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDSplashScreen() {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = EaseOutBack),
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(400),
        )
    }

    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                modifier =
                Modifier
                    .size(120.dp)
                    .scale(scale.value),
            )
            Spacer(Modifier.height(16.dp))
            TDText(
                text = "ToDo App",
                style = TDTheme.typography.heading6,
                color = TDTheme.colors.pendingGray,
                modifier = Modifier.alpha(alpha.value),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TDSplashScreenPreview() {
    TDTheme {
        TDSplashScreen()
    }
}
