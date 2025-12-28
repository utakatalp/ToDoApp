package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDLoading() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TDTheme.colors.brown.copy(alpha = 0.4f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {},
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = TDTheme.colors.brown,
            strokeCap = StrokeCap.Round,
        )
    }
}
