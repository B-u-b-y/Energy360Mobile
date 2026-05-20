package com.example.energy360mobile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.energy360mobile.components.HomeDashboard
import com.example.energy360mobile.components.Screen
import com.example.energy360mobile.viewmodels.StepsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomePageUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockStepsViewModel: StepsViewModel

    @Before
    fun setup() {
        mockStepsViewModel = mockk(relaxed = true)

        // Mock steps flow
        every { mockStepsViewModel.steps } returns MutableStateFlow(null)
    }

    @Test
    fun homeDashboard_displaysMainTexts() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            val navController = rememberNavController()

            HomeDashboard(
                navController = navController,
                stepsViewModel = mockStepsViewModel
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.welcome))
            .assertExists()

        composeTestRule.onNodeWithText(context.getString(R.string.app_label))
            .assertExists()

        composeTestRule.onNodeWithText(context.getString(R.string.our_suggestions))
            .assertExists()

        composeTestRule.onNodeWithText(context.getString(R.string.fast_actions))
            .assertExists()
    }

    @Test
    fun homeDashboard_displaysPromotionCards() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            HomeDashboard(
                navController = navController,
                stepsViewModel = mockStepsViewModel
            )
        }

        composeTestRule.onNodeWithText("Yoga")
            .assertExists()

        composeTestRule.onNodeWithText("Open Day")
            .assertExists()
    }

    @Test
    fun homeDashboard_displaysQuickActions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            val navController = rememberNavController()

            HomeDashboard(
                navController = navController,
                stepsViewModel = mockStepsViewModel
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.reserve))
            .assertExists()
            .assertHasClickAction()

        composeTestRule.onNodeWithText(context.getString(R.string.courses))
            .assertExists()
            .assertHasClickAction()

        composeTestRule.onNodeWithText(context.getString(R.string.start_membership))
            .assertExists()

        composeTestRule.onNodeWithText(context.getString(R.string.contact_us))
            .assertExists()
    }

    @Test
    fun homeDashboard_navigationToSearchWorks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeDashboard(
                        navController = navController,
                        stepsViewModel = mockStepsViewModel
                    )
                }
                composable(Screen.Search.route) {
                    androidx.compose.material3.Text("Search Screen")
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.reserve))
            .performClick()

        composeTestRule.onNodeWithText("Search Screen")
            .assertExists()
    }

    @Test
    fun homeDashboard_navigationToServicesWorks() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeDashboard(
                        navController = navController,
                        stepsViewModel = mockStepsViewModel
                    )
                }
                composable("dashboard/services") {
                    androidx.compose.material3.Text("Services Screen")
                }
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.courses))
            .performClick()

        composeTestRule.onNodeWithText("Services Screen")
            .assertExists()
    }

    @Test
    fun stepCounter_displaysDefaultSteps() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            HomeDashboard(
                navController = navController,
                stepsViewModel = mockStepsViewModel
            )
        }

        composeTestRule.onNodeWithText("0")
            .assertExists()
    }
}