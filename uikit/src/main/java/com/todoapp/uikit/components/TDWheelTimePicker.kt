package com.todoapp.uikit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun TDWheelTimePicker(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
) {
    val itemHeight = 56.dp
    val visibleHeight = itemHeight * VISIBLE_ITEMS

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WheelColumn(
            count = 24,
            selected = hour,
            onSelected = onHourChange,
            itemHeight = itemHeight,
            visibleHeight = visibleHeight,
            format = { "%02d".format(it) },
        )

        TDText(
            text = ":",
            style = TDTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
            color = TDTheme.colors.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        WheelColumn(
            count = 60,
            selected = minute,
            onSelected = onMinuteChange,
            itemHeight = itemHeight,
            visibleHeight = visibleHeight,
            format = { "%02d".format(it) },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelColumn(
    count: Int,
    selected: Int,
    onSelected: (Int) -> Unit,
    itemHeight: Dp,
    visibleHeight: Dp,
    format: (Int) -> String,
) {
    val totalItems = count * CYCLE_MULTIPLIER
    val baseIndex = (totalItems / 2) - ((totalItems / 2) % count)
    val initialIndex = baseIndex + selected

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex - VISIBLE_ITEMS / 2,
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    var lastReportedValue by remember { mutableIntStateOf(selected) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { scrolling ->
                if (!scrolling) {
                    val settled = listState.settledCenterValue(count)
                    if (settled != lastReportedValue) {
                        lastReportedValue = settled
                        onSelected(settled)
                    }
                }
            }
    }

    LaunchedEffect(selected) {
        if (selected != lastReportedValue) {
            lastReportedValue = selected
            val targetIndex = baseIndex + selected - VISIBLE_ITEMS / 2
            listState.animateScrollToItem(targetIndex)
        }
    }

    val centeredIndex by remember { derivedStateOf { listState.settledCenterIndex() } }
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(itemHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(TDTheme.colors.pendingGray.copy(alpha = 0.1f)),
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .width(80.dp)
                .height(visibleHeight),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(totalItems, key = { it }) { index ->
                val value = index % count
                val isCentered = !isScrolling && centeredIndex == index

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(80.dp)
                        .height(itemHeight),
                ) {
                    TDText(
                        text = format(value),
                        style = if (isCentered) {
                            TDTheme.typography.heading2.copy(fontWeight = FontWeight.Bold)
                        } else {
                            TDTheme.typography.heading4
                        },
                        color = if (isCentered) {
                            TDTheme.colors.purple
                        } else {
                            TDTheme.colors.onBackground.copy(alpha = 0.3f)
                        },
                    )
                }
            }
        }
    }
}

private fun LazyListState.settledCenterIndex(): Int {
    val info = layoutInfo
    val center = info.viewportStartOffset +
            (info.viewportEndOffset - info.viewportStartOffset) / 2
    return info.visibleItemsInfo.minByOrNull {
        kotlin.math.abs((it.offset + it.size / 2) - center)
    }?.index ?: 0
}

private fun LazyListState.settledCenterValue(count: Int): Int {
    return settledCenterIndex() % count
}

private const val CYCLE_MULTIPLIER = 1000
private const val VISIBLE_ITEMS = 5

@TDPreview
@Composable
private fun TDWheelTimePickerPreview() {
    TDTheme {
        Box(
            Modifier
                .background(TDTheme.colors.background)
                .padding(24.dp)
        ) {
            TDWheelTimePicker(
                hour = 9,
                minute = 0,
                onHourChange = {},
                onMinuteChange = {},
            )
        }
    }
}
