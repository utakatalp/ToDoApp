package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDScreenWithSheet(
    isSheetOpen: Boolean = false,
    skipPartiallyExpanded: Boolean = true,
    containerColor: Color = TDTheme.colors.background,
    contentColor: Color = TDTheme.colors.onBackground,
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    sheetContent: @Composable () -> Unit,
    onDismissSheet: () -> Unit,
    content: @Composable () -> Unit,
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isSheetOpen) {
        if (isSheetOpen) {
            coroutineScope.launch {
                openBottomSheet = true
                bottomSheetState.show()
            }
        } else {
            coroutineScope
                .launch { bottomSheetState.hide() }
                .invokeOnCompletion {
                    if (!bottomSheetState.isVisible) {
                        openBottomSheet = false
                    }
                }
        }
    }

    content()

    if (openBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier,
            onDismissRequest = { onDismissSheet() },
            sheetState = bottomSheetState,
            containerColor = containerColor,
            contentColor = contentColor,
            sheetMaxWidth = sheetMaxWidth,
            dragHandle = null,
        ) {
            sheetContent()
        }
    }
}

@TDPreview
@Composable
private fun TDScreenWithSheetPreview() {
    TDTheme {
        TDScreenWithSheet(
            isSheetOpen = true,
            sheetContent = {
                Box(
                    modifier =
                    Modifier
                        .background(TDTheme.colors.background)
                        .padding(24.dp),
                ) {
                    TDText(text = "Sheet Content")
                }
            },
            onDismissSheet = {},
        ) {
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .background(TDTheme.colors.background)
                    .padding(16.dp),
            ) {
                TDText(text = "Main Content")
            }
        }
    }
}

@TDPreview
@Composable
private fun TDScreenWithSheetClosedPreview() {
    TDTheme {
        TDScreenWithSheet(
            isSheetOpen = false,
            sheetContent = {},
            onDismissSheet = {},
        ) {
            Box(
                modifier =
                Modifier
                    .fillMaxSize()
                    .background(TDTheme.colors.background)
                    .padding(16.dp),
            ) {
                TDText(text = "Main Content (sheet closed)")
            }
        }
    }
}
