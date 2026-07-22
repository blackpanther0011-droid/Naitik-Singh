package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AthleteViewModel

sealed class AppTab(
    val route: String,
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    object Home : AppTab("home", "Home", Icons.Default.Home, Icons.Outlined.Home)
    object Reels : AppTab("reels", "Reels", Icons.Default.PlayCircle, Icons.Outlined.PlayCircle)
    object Explore : AppTab("explore", "Explore", Icons.Default.Search, Icons.Outlined.Search)
    object Calls : AppTab("calls", "Chatrooms", Icons.Default.Forum, Icons.Outlined.Forum)
    object Profile : AppTab("profile", "Profile", Icons.Default.Person, Icons.Outlined.Person)
}

@Composable
fun MainAppScaffold(
    viewModel: AthleteViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    var currentTab by remember { mutableStateOf<AppTab>(AppTab.Home) }

    val tabs = listOf(
        AppTab.Home,
        AppTab.Reels,
        AppTab.Explore,
        AppTab.Calls,
        AppTab.Profile
    )

    if (!isLoggedIn) {
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = { currentTab = AppTab.Home },
            modifier = modifier
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                Column {
                    // Sleek thin divider to separate content from bottom navigation bar
                    Divider(color = Color(0x1AFFFFFF), thickness = 0.5.dp)
                    NavigationBar(
                        containerColor = Color(0xFF16191C), // Matching custom theme background
                        contentColor = Color.White,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .testTag("app_bottom_nav_bar")
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = currentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                                        contentDescription = tab.title,
                                        tint = if (isSelected) Color(0xFFF59E0B) else Color(0xFF94A3B8)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color(0x1AFFFFFF) // Sleek white frosted selection indicator
                                ),
                                modifier = Modifier.testTag("nav_item_${tab.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Smoothly animate transition between screens using Framer Motion style directional sliding spring transitions
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        val initialIndex = tabs.indexOf(initialState)
                        val targetIndex = tabs.indexOf(targetState)
                        val direction = if (targetIndex > initialIndex) 1 else -1

                        val springSpec = spring<androidx.compose.ui.unit.IntOffset>(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                        val fadeSpringSpec = spring<Float>(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )

                        slideInHorizontally(animationSpec = springSpec) { width -> direction * width } +
                                fadeIn(animationSpec = fadeSpringSpec) togetherWith
                        slideOutHorizontally(animationSpec = springSpec) { width -> -direction * width } +
                                fadeOut(animationSpec = fadeSpringSpec)
                    },
                    label = "tab_navigation_transitions"
                ) { targetTab ->
                    when (targetTab) {
                        AppTab.Home -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToReels = { currentTab = AppTab.Reels },
                                onNavigateToExplore = { currentTab = AppTab.Explore }
                            )
                        }
                        AppTab.Reels -> {
                            ReelsScreen(viewModel = viewModel, isFollowingOnly = false)
                        }
                        AppTab.Explore -> {
                            ExploreScreen(viewModel = viewModel)
                        }
                        AppTab.Calls -> {
                            CallsScreen(viewModel = viewModel)
                        }
                        AppTab.Profile -> {
                            ProfileScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
