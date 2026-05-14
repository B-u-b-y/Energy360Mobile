package com.example.energy360mobile.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.energy360mobile.viewmodels.ExercisesViewModel
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import com.example.energy360mobile.viewmodels.StepsViewModel
import com.example.energy360mobile.viewmodels.UserViewModel

/**
 * The main navigation graph of the application.
 * Defines all top-level destinations and handles transitions between different screens.
 *
 * @param navController The NavHostController used for navigating between composables.
 * @param paddingValues The padding to apply to the content.
 * @param startingDestination The initial route to display.
 * @param userViewModel ViewModel for user-related data and authentication.
 * @param stepsViewModel ViewModel for step counting and fitness tracking.
 * @param reservationsViewModel ViewModel for gym reservations and booking.
 * @param exercisesViewModel ViewModel for training sessions and exercises.
 */
@Composable
fun NavGraph(navController: NavHostController,
    paddingValues: PaddingValues,
    startingDestination: String,
    userViewModel: UserViewModel,
    stepsViewModel: StepsViewModel,
    reservationsViewModel: ReservationsViewModel,
    exercisesViewModel: ExercisesViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startingDestination,
        modifier = Modifier.padding(paddingValues)
    ) {
        // Authentication flow (Login/Register)
        composable("auth") { Execution(userViewModel, navController) }
        
        // Main bottom navigation screens
        composable(Screen.Home.route) { HomeScreen(navController, stepsViewModel) }
        composable(Screen.Messages.route) { MessagesScreen(navController) }
        composable(Screen.Search.route) { SearchScreen() }
        composable(Screen.Reservation.route) { ReservationScreen(reservationsViewModel) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        
        // Secondary screens and detail pages
        composable("training_page") { TrainingSession(exercisesViewModel, onBack = { navController.popBackStack() }) }
        
        composable("chat_detail/{senderName}") { backStackEntry ->
            val senderName = backStackEntry.arguments?.getString("senderName") ?: ""
            ChatDetailScreen(senderName = senderName, onBack = { navController.popBackStack() })
        }
        
        composable("services") { ServicesPage(onBack = { navController.popBackStack() }) }
    }
}