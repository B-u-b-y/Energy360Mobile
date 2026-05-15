package com.example.energy360mobile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.energy360mobile.components.NavGraph
import com.example.energy360mobile.components.Screen
import com.example.energy360mobile.viewmodels.ExercisesViewModel
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import com.example.energy360mobile.viewmodels.StepsViewModel
import com.example.energy360mobile.viewmodels.UserViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.ui.unit.dp
import com.example.energy360mobile.databases.exercises.ExercisesEntity
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.databases.steps.StepsEntity
import com.example.energy360mobile.databases.userData.UserEntity

@RunWith(AndroidJUnit4::class)
class NavGraphTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockUserViewModel: UserViewModel
    private lateinit var mockStepsViewModel: StepsViewModel
    private lateinit var mockReservationsViewModel: ReservationsViewModel
    private lateinit var mockExercisesViewModel: ExercisesViewModel

    @Before
    fun setup() {
        mockUserViewModel = mockk(relaxed = true)
        mockStepsViewModel = mockk(relaxed = true)
        mockReservationsViewModel = mockk(relaxed = true)
        mockExercisesViewModel = mockk(relaxed = true)

        // Mock StateFlows to avoid ClassCastException with relaxed mocks
        every { mockUserViewModel.userData } returns MutableStateFlow<UserEntity?>(null)
        every { mockStepsViewModel.steps } returns MutableStateFlow<StepsEntity?>(null)
        every { mockReservationsViewModel.reservations } returns MutableStateFlow<List<ReservationsEntity>>(emptyList())
        every { mockExercisesViewModel.exercise } returns MutableStateFlow<List<ExercisesEntity>>(emptyList())
        every { mockExercisesViewModel.completedExercises } returns MutableStateFlow<List<ExercisesEntity>>(emptyList())
        every { mockExercisesViewModel.availableExercises } returns MutableStateFlow<List<ExercisesEntity>>(emptyList())
    }

    @Test
    fun navGraph_startingAtAuth_displaysLogin() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            NavGraph(
                navController = navController,
                paddingValues = PaddingValues(0.dp),
                startingDestination = "auth",
                userViewModel = mockUserViewModel,
                stepsViewModel = mockStepsViewModel,
                reservationsViewModel = mockReservationsViewModel,
                exercisesViewModel = mockExercisesViewModel
            )
        }

        // Verify Login screen is shown (branding + login button)
        composeTestRule.onNodeWithText("Energy360").assertIsDisplayed()
        composeTestRule.onNodeWithText("LOGIN").assertIsDisplayed()
    }

    @Test
    fun navGraph_startingAtHome_displaysHome() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val welcomeText = context.getString(R.string.welcome)

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavGraph(
                navController = navController,
                paddingValues = PaddingValues(0.dp),
                startingDestination = Screen.Home.route,
                userViewModel = mockUserViewModel,
                stepsViewModel = mockStepsViewModel,
                reservationsViewModel = mockReservationsViewModel,
                exercisesViewModel = mockExercisesViewModel
            )
        }

        // Verify Home screen is shown
        composeTestRule.onNodeWithText(welcomeText, substring = true).assertIsDisplayed()
    }

    @Test
    fun navGraph_navigatesToMessages() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val messagesLabel = context.getString(R.string.message)

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavGraph(
                navController = navController,
                paddingValues = PaddingValues(0.dp),
                startingDestination = Screen.Home.route,
                userViewModel = mockUserViewModel,
                stepsViewModel = mockStepsViewModel,
                reservationsViewModel = mockReservationsViewModel,
                exercisesViewModel = mockExercisesViewModel
            )
            
            // Manual navigation trigger for test
            navController.navigate(Screen.Messages.route)
        }

        // Verify Messages screen is shown
        composeTestRule.onNodeWithText(messagesLabel).assertIsDisplayed()
    }

    @Test
    fun navGraph_navigatesToProfile() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val accountInfoLabel = context.getString(R.string.account_infos)

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavGraph(
                navController = navController,
                paddingValues = PaddingValues(0.dp),
                startingDestination = Screen.Home.route,
                userViewModel = mockUserViewModel,
                stepsViewModel = mockStepsViewModel,
                reservationsViewModel = mockReservationsViewModel,
                exercisesViewModel = mockExercisesViewModel
            )
            
            navController.navigate(Screen.Profile.route)
        }

        // Verify Profile screen is shown by checking for "Account Informations" header
        composeTestRule.onNodeWithText(accountInfoLabel).assertIsDisplayed()
    }

    @Test
    fun navGraph_navigatesToReservation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val reservationTitle = context.getString(R.string.reservation_title)

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavGraph(
                navController = navController,
                paddingValues = PaddingValues(0.dp),
                startingDestination = Screen.Home.route,
                userViewModel = mockUserViewModel,
                stepsViewModel = mockStepsViewModel,
                reservationsViewModel = mockReservationsViewModel,
                exercisesViewModel = mockExercisesViewModel
            )
            
            navController.navigate(Screen.Reservation.route)
        }

        // Verify Reservation screen is shown
        composeTestRule.onNodeWithText(reservationTitle).assertIsDisplayed()
    }

    @Test
    fun navGraph_navigatesToTrainingPage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val trainingTitle = context.getString(R.string.training_title)

        composeTestRule.setContent {
            val navController = rememberNavController()
            NavGraph(
                navController = navController,
                paddingValues = PaddingValues(0.dp),
                startingDestination = Screen.Home.route,
                userViewModel = mockUserViewModel,
                stepsViewModel = mockStepsViewModel,
                reservationsViewModel = mockReservationsViewModel,
                exercisesViewModel = mockExercisesViewModel
            )
            
            navController.navigate("training_page")
        }

        // Verify Training screen is shown
        composeTestRule.onNodeWithText(trainingTitle).assertIsDisplayed()
    }
}
