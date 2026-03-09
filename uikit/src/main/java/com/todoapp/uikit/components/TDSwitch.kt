package com.todoapp.uikit.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = TDTheme.colors.white,
            checkedTrackColor = TDTheme.colors.primary,
            uncheckedThumbColor = TDTheme.colors.gray,
            uncheckedTrackColor = TDTheme.colors.lightGray
        )
    )
}

@Composable
@Suppress("ComposableLambdaParameterNaming", "ComposableLambdaParameterPosition")
fun TDSegmentedSwitch(
    leftText: String,
    rightText: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    val density = LocalDensity.current

    var leftWidthPx by remember { mutableStateOf(0) }
    var rightWidthPx by remember { mutableStateOf(0) }

    val toggleableModifier =
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interaction,
                indication = null,
            )
        } else {
            Modifier
        }

    val containerShape = RoundedCornerShape(25)
    val thumbShape = RoundedCornerShape(25)

    val leftWidthDp = with(density) { leftWidthPx.toDp() }
    val rightWidthDp = with(density) { rightWidthPx.toDp() }

    val targetOffset = if (checked) leftWidthDp else 0.dp
    val animatedOffset by animateDpAsState(targetValue = targetOffset, label = "ThumbOffset")

    val thumbWidth = if (checked) rightWidthDp else leftWidthDp

    Box(
        modifier = modifier
            .then(toggleableModifier)
            .clip(containerShape)
            .border(1.dp, TDTheme.colors.lightGray, containerShape)
            .background(TDTheme.colors.lightGray.copy(alpha = 0.25f))
            .padding(2.dp)
            .height(32.dp)
    ) {
        // Sliding thumb (dynamic width)
        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .width(thumbWidth)
                .fillMaxHeight()
                .padding(1.dp)
                .clip(thumbShape)
                .background(TDTheme.colors.white)
        )

        Row {
            Box(
                modifier = Modifier
                    .onSizeChanged { leftWidthPx = it.width }
                    .clickable(
                        enabled = enabled,
                        interactionSource = interaction,
                        indication = null
                    ) { onCheckedChange?.invoke(false) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                TDText(
                    text = leftText,
                    color = if (!checked) TDTheme.colors.black else TDTheme.colors.lightGray
                )
            }

            Box(
                modifier = Modifier
                    .onSizeChanged { rightWidthPx = it.width }
                    .clickable(
                        enabled = enabled,
                        interactionSource = interaction,
                        indication = null
                    ) { onCheckedChange?.invoke(true) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                TDText(
                    text = rightText,
                    color = if (checked) TDTheme.colors.black else TDTheme.colors.lightGray
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
private fun TDSegmentedSwitchPreview() {
    TDTheme {
        var selected by remember { mutableIntStateOf(1) }
        Column(Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center) {
            TDSegmentedSwitch(
                options = listOf("All", "Assigned to Me"),
                selectedIndex = selected,
                onSelected = { selected = it },
                modifier = Modifier.padding(16.dp),
                enabled = true,
            )
        }
    }
}

 */
@Composable
fun TDSwitchWithLabel(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        TDText(
            text = text,
            modifier = Modifier.weight(1f)
        )

        TDSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TDSwitchPreview() {
    TDTheme {
        var checked by remember { mutableStateOf(true) }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            TDSwitch(
                checked = checked,
                onCheckedChange = { checked = it },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TDSwitchWithLabelPreview() {
    TDTheme {
        var checked by remember { mutableStateOf(false) }

        TDSwitchWithLabel(
            text = "Assigned to Me",
            checked = checked,
            onCheckedChange = { checked = it },
            modifier = Modifier.padding(16.dp)
        )
    }
}
