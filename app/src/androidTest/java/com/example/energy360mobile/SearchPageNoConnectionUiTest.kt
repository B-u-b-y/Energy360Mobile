package com.example.energy360mobile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.energy360mobile.components.noConnection.SearchPageNoConnection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchPageNoConnectionUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchPageNoConnection_displaysAllComponents() {
        composeTestRule.setContent {
            SearchPageNoConnection()
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val whatDoingLabel = context.getString(R.string.what_doin)
        val exploreLabel = context.getString(R.string.explore)
        val noConnectionText = context.getString(R.string.no_connection)

        // Verify Header
        composeTestRule.onNodeWithText(whatDoingLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(exploreLabel).assertIsDisplayed()

        // Verify that "No connection" appears twice:
        // 1. As the placeholder in the disabled search bar
        // 2. As the central error message
        composeTestRule.onAllNodesWithText(noConnectionText).assertCountEquals(2)

        // Verify both are displayed
        composeTestRule.onAllNodesWithText(noConnectionText).apply {
            fetchSemanticsNodes().forEachIndexed { index, _ ->
                get(index).assertIsDisplayed()
            }
        }
    }
}
