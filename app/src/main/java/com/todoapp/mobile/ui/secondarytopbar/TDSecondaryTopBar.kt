package com.todoapp.mobile.ui.secondarytopbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.todoapp.mobile.LocalNavController
import com.todoapp.mobile.navigation.Screen
import com.todoapp.uikit.theme.TDTheme
private val secondaryTopBarRoutes: List<String> = listOfNotNull(
    Screen.GroupDetails.Overview::class.qualifiedName,
    Screen.GroupDetails.Members::class.qualifiedName,
    Screen.GroupDetails.Activity::class.qualifiedName,
)

@Composable
fun rememberShowSecondaryTopBar(): Boolean {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    return currentDestination?.hierarchy?.any { dest ->
        val route = dest.route.orEmpty()
        secondaryTopBarRoutes.any {
            route.startsWith(it)
        }
    } == true
}

@Composable
fun TDSecondaryTopBar() {
    val navController = LocalNavController.current
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination

    val groupId = entry?.arguments?.getString("groupId")
    if (groupId.isNullOrBlank()) return

    val selectedIndex by remember(destination) {
        derivedStateOf {
            when {
                destination?.hasRoute<Screen.GroupDetails.Overview>() == true -> 0
                destination?.hasRoute<Screen.GroupDetails.Members>() == true -> 1
                destination?.hasRoute<Screen.GroupDetails.Activity>() == true -> 2
                else -> 0
            }
        }
    }

    val tabs = listOf(
        SecondaryTabItem("Overview"),
        SecondaryTabItem("Members"),
        SecondaryTabItem("Activity"),
    )

    TDSecondaryTopBarDesign(
        selectedIndex = selectedIndex,
        tabs = tabs,
        onTabClick = { index ->
            if (index == selectedIndex) return@TDSecondaryTopBarDesign

            when (index) {
                0 -> navController.navigate(Screen.GroupDetails.Overview(groupId)) {
                    launchSingleTop = true
                    popUpTo(Screen.GroupDetails.Overview(groupId))
                }
                1 -> navController.navigate(Screen.GroupDetails.Members(groupId)) {
                    launchSingleTop = true
                    popUpTo(Screen.GroupDetails.Members(groupId))
                }
                2 -> navController.navigate(Screen.GroupDetails.Activity(groupId)) {
                    launchSingleTop = true
                    popUpTo(Screen.GroupDetails.Activity(groupId))
                }
            }
        }
    )
}

private data class SecondaryTabItem(
    val title: String,
)

@Composable
private fun TDSecondaryTopBarDesign(
    selectedIndex: Int,
    tabs: List<SecondaryTabItem>,
    onTabClick: (Int) -> Unit,
    containerBg: Color = Color(0xFFF3F4F6),
    selectedBg: Color = Color.White,
    selectedText: Color = TDTheme.colors.purple,
    unselectedText: Color = Color(0xFF6B7280),
) {
    val containerShape = RoundedCornerShape(12.dp)
    val itemShape = RoundedCornerShape(10.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(containerShape)
            .background(containerBg)
            .padding(4.dp),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tabs.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) selectedText else unselectedText,
                    label = "secondaryTopBarTextColor",
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(itemShape)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(elevation = 2.dp, shape = itemShape, clip = false)
                                    .background(selectedBg)
                            } else {
                                Modifier.background(Color.Transparent)
                            }
                        )
                        .clickable { onTabClick(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item.title,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview(name = "SecondaryTopBar - Selected 0", showBackground = true)
@Composable
private fun PreviewSecondaryTopBarSelected0() {
    MaterialTheme {
        TDSecondaryTopBarDesign(
            selectedIndex = 0,
            tabs = listOf(
                SecondaryTabItem("Overview"),
                SecondaryTabItem("Members"),
                SecondaryTabItem("Activity"),
            ),
            onTabClick = {},
            // Explicit colors so Preview is stable even without app theme setup
            selectedText = Color(0xFF7C3AED),
        )
    }
}

@Preview(name = "SecondaryTopBar - Selected 1", showBackground = true)
@Composable
private fun PreviewSecondaryTopBarSelected1() {
    MaterialTheme {
        TDSecondaryTopBarDesign(
            selectedIndex = 1,
            tabs = listOf(
                SecondaryTabItem("Overview"),
                SecondaryTabItem("Members"),
                SecondaryTabItem("Activity"),
            ),
            onTabClick = {},
            selectedText = Color(0xFF7C3AED),
        )
    }
}
