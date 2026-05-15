package com.example.energy360mobile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.energy360mobile.components.BottomBar
import com.example.energy360mobile.components.Screen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bottomBar_displaysAllItems() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            BottomBar(navController = navController)
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val homeLabel = context.getString(R.string.home_label)
        val searchLabel = context.getString(R.string.search_label)
        val reservationLabel = context.getString(R.string.reserve)
        val messagesLabel = context.getString(R.string.message)
        val profileLabel = context.getString(R.string.profile_label)

        // Verify all labels are displayed
        composeTestRule.onNodeWithText(homeLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(searchLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(reservationLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(messagesLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(profileLabel).assertIsDisplayed()
    }

    @Test
    fun bottomBar_messagesBadgeIsDisplayed() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            // Wrap in theme to ensure Material components (Badge, NavigationBar)
            // have the correct styling and visibility.
            BottomBar(navController = navController)
        }

        // The Messages screen has a badge with "2" as hardcoded in the component.
        // We use useUnmergedTree = true because Badge content might be merged into
        // the parent semantics in some versions of Compose.
        composeTestRule.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun bottomBar_navigationUpdatesDestination() {
        // We use a real NavHost to verify that clicking the bottom bar items
        // actually triggers navigation and changes the current destination.

        var currentRoute: String? = null

        composeTestRule.setContent {
            val navController = rememberNavController()

            // Listen for destination changes to verify navigation
            navController.addOnDestinationChangedListener { _, destination, _ ->
                currentRoute = destination.route
            }

            Scaffold(
                bottomBar = { BottomBar(navController = navController) }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) { Text("Home Screen") }
                    composable(Screen.Search.route) { Text("Search Screen") }
                    composable(Screen.Reservation.route) { Text("Reservation Screen") }
                    composable(Screen.Messages.route) { Text("Messages Screen") }
                    composable(Screen.Profile.route) { Text("Profile Screen") }
                }
            }
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val searchLabel = context.getString(R.string.search_label)
        val reservationLabel = context.getString(R.string.reserve)
        val messagesLabel = context.getString(R.string.message)
        val profileLabel = context.getString(R.string.profile_label)

        // 1. Initial destination should be Home
        assert(currentRoute == Screen.Home.route)
        composeTestRule.onNodeWithText("Home Screen").assertIsDisplayed()

        // 2. Click on Search
        composeTestRule.onNodeWithText(searchLabel).performClick()
        composeTestRule.waitForIdle()
        assert(currentRoute == Screen.Search.route)
        composeTestRule.onNodeWithText("Search Screen").assertIsDisplayed()

        // 3. Click on Reservation
        composeTestRule.onNodeWithText(reservationLabel).performClick()
        composeTestRule.waitForIdle()
        assert(currentRoute == Screen.Reservation.route)
        composeTestRule.onNodeWithText("Reservation Screen").assertIsDisplayed()

        // 4. Click on Messages
        composeTestRule.onNodeWithText(messagesLabel).performClick()
        composeTestRule.waitForIdle()
        assert(currentRoute == Screen.Messages.route)
        composeTestRule.onNodeWithText("Messages Screen").assertIsDisplayed()

        // 5. Click on Profile
        composeTestRule.onNodeWithText(profileLabel).performClick()
        composeTestRule.waitForIdle()
        assert(currentRoute == Screen.Profile.route)
        composeTestRule.onNodeWithText("Profile Screen").assertIsDisplayed()
    }

    @Test
    fun bottomBar_selectionStateIsReflected() {
        composeTestRule.setContent {
            val navController = rememberNavController()

            Scaffold(
                bottomBar = { BottomBar(navController = navController) }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) { Text("Home") }
                    composable(Screen.Search.route) { Text("Search") }
                }
            }
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val homeLabel = context.getString(R.string.home_label)
        val searchLabel = context.getString(R.string.search_label)

        // Initially Home should be selected.
        // We use hasAnyDescendant(hasText(homeLabel)) to find the NavigationBarItem container.
        // We filter by checking the Role property directly.
        // We use useUnmergedTree = true because NavigationBarItem might merge semantics in a way that hides descendants from the merged tree.
        composeTestRule.onNode(hasAnyDescendant(hasText(homeLabel)) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab), useUnmergedTree = true).assertIsSelected()
        composeTestRule.onNode(hasAnyDescendant(hasText(searchLabel)) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab), useUnmergedTree = true).assertIsNotSelected()

        // Click Search
        composeTestRule.onNode(hasAnyDescendant(hasText(searchLabel)) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab), useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        // After clicking, Search should be selected
        composeTestRule.onNode(hasAnyDescendant(hasText(searchLabel)) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab), useUnmergedTree = true).assertIsSelected()
        composeTestRule.onNode(hasAnyDescendant(hasText(homeLabel)) and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab), useUnmergedTree = true).assertIsNotSelected()
    }
}
