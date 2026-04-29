package com.example.energy360mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.energy360mobile.components.BottomBar
import com.example.energy360mobile.components.ChatDetailScreen
import com.example.energy360mobile.components.Execution
import com.example.energy360mobile.components.HomeScreen
import com.example.energy360mobile.components.MessagesScreen
import com.example.energy360mobile.components.ProfileScreen
import com.example.energy360mobile.components.ReservationScreen
import com.example.energy360mobile.components.Screen
import com.example.energy360mobile.components.SearchScreen
import com.example.energy360mobile.components.TrainingSession
import com.example.energy360mobile.theme.Energy360MobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Energy360MobileTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainScreens = listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Reservation.route,
        Screen.Profile.route,
        Screen.Messages.route,
        "training_page"
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in mainScreens) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "auth",
            modifier = Modifier.padding(if (currentRoute in mainScreens) innerPadding else PaddingValues(0.dp))
        ) {
            composable("auth") { Execution(navController) }
            composable(Screen.Home.route) { HomeScreen() }
            composable("message_page") { MessagesScreen(navController) }
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Reservation.route) { ReservationScreen() }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable("training_page") { TrainingSession() }
            composable("chat_detail/{senderName}") { backStackEntry ->
                val senderName = backStackEntry.arguments?.getString("senderName") ?: ""
                ChatDetailScreen(senderName = senderName, onBack = { navController.popBackStack() })
            }
        }
    }
}