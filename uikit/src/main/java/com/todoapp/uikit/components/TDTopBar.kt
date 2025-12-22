package com.todoapp.uikit.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.uikit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDTopBar(state: TopBarState) {
    TopAppBar(
        title = { Text(state.title) },
        navigationIcon = {
            state.navigationIcon.let {
                IconButton(onClick = state.onNavigationClick!!) {
                    Icon(painterResource(it), contentDescription = null)
                }
            }
        },
        actions = {
            state.actions.forEach {
                IconButton(onClick = it.onClick) {
                    Icon(painterResource(it.icon), contentDescription = null)
                }
            }
        },
    )
}

data class TopBarState(
    val title: String,
    @DrawableRes val navigationIcon: Int,
    val onNavigationClick: (() -> Unit)? = null,
    val actions: List<TopBarAction> = emptyList(),
)

data class TopBarAction(
    @DrawableRes val icon: Int,
    val onClick: () -> Unit,
)

@Preview(showBackground = true)
@Composable
fun TDTopBarPreview() {
    TDTopBar(
        state =
            TopBarState(
                title = "Home",
                navigationIcon = R.drawable.ic_settings,
                onNavigationClick = {},
                actions =
                    listOf(
                        TopBarAction(
                            icon = R.drawable.ic_search,
                            onClick = {},
                        ),
                        TopBarAction(
                            icon = R.drawable.ic_notification,
                            onClick = {},
                        ),
                    ),
            ),
    )
}
