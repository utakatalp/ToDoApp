package com.todoapp.mobile.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.todoapp.mobile.R
import com.todoapp.uikit.components.TDFeatureExplainer
import com.todoapp.uikit.extensions.collectWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun ScreenInfoDialog(
    infoClicks: Flow<Unit>,
    @StringRes titleRes: Int,
    @StringRes descriptionRes: Int,
    bulletPointRes: List<Int> = emptyList(),
) {
    var show by rememberSaveable { mutableStateOf(false) }
    infoClicks.collectWithLifecycle { show = true }
    if (show) {
        TDFeatureExplainer(
            title = stringResource(titleRes),
            description = stringResource(descriptionRes),
            bulletPoints = bulletPointRes.map { stringResource(it) },
            buttonText = stringResource(R.string.got_it),
            onDismiss = { show = false },
        )
    }
}
