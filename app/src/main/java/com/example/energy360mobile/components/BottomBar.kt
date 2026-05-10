package com.example.energy360mobile.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.energy360mobile.R
import com.example.energy360mobile.viewmodels.ReservationsViewModel

sealed class Screen(val route: String, val labelId: Int, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("dashboard/home", R.string.home_label, Icons.Default.Home, Icons.Outlined.Home)
    object Search : Screen("search", R.string.search_label, Icons.Default.Search, Icons.Outlined.Search)
    object Reservation : Screen("reservation", R.string.reserve, Icons.Default.CalendarToday, Icons.Outlined.CalendarToday)
    object Profile : Screen("profile", R.string.profile_label, Icons.Default.Person, Icons.Outlined.Person)
    object Messages : Screen("message_page", R.string.message, Icons.Default.Email, Icons.Outlined.Email)}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.Reservation,
        Screen.Messages,
        Screen.Profile
    )
    
    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp)),
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "iconColor"
            )
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (screen == Screen.Messages) {
                                Badge {
                                    Text("2")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                            contentDescription = null,
                            modifier = Modifier.size(if (isSelected) 26.dp else 24.dp),
                            tint = iconColor
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(screen.labelId),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = iconColor
                    )
                },
                selected = isSelected,
                onClick = {
                    if (screen.route == Screen.Home.route) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }else {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )

        }
    }
}

@Composable
fun HomeScreen(rootNavController: NavHostController) {
    HomePage(rootNavController)
}

@Composable
fun ProfileScreen(rootNavController: NavHostController) {
    UserProfile(rootNavController)
}

@Composable
fun MessagesScreen(rootNavController: NavHostController) {
    MessagePage(rootNavController)
}

@Composable
fun SearchScreen() {
    SearchPage()
}

@Composable
fun ReservationScreen(viewModel: ReservationsViewModel) {
    Reservations(viewModel)
}
