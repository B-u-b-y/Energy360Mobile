package com.example.energy360mobile.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import com.example.energy360mobile.viewmodels.StepsViewModel
import com.example.energy360mobile.viewmodels.UserViewModel

@Composable
fun NavGraph(navController: NavHostController,
    paddingValues: PaddingValues,
    startingDestination: String,
    userViewModel: UserViewModel,
    stepsViewModel: StepsViewModel,
    reservationsViewModel: ReservationsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startingDestination,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("auth") { Execution(userViewModel, navController) }
        composable(Screen.Home.route) { HomeScreen(navController, stepsViewModel) }
        composable(Screen.Messages.route) { MessagesScreen(navController) }
        composable(Screen.Search.route) { SearchScreen() }
        composable(Screen.Reservation.route) { ReservationScreen(reservationsViewModel) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        composable("training_page") { TrainingSession(onBack = { navController.popBackStack() }) }
        composable("chat_detail/{senderName}") { backStackEntry ->
            val senderName = backStackEntry.arguments?.getString("senderName") ?: ""
            ChatDetailScreen(senderName = senderName, onBack = { navController.popBackStack() })
        }
        composable("services") { ServicesPage(onBack = { navController.popBackStack() }) }
    }
}