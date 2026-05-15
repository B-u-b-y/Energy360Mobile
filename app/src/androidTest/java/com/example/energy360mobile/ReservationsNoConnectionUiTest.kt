package com.example.energy360mobile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.energy360mobile.components.noConnection.ReservationsNoConnection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReservationsNoConnectionUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun reservationsNoConnection_displaysAllComponents() {
        composeTestRule.setContent {
            ReservationsNoConnection()
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val recapLabel = context.getString(R.string.recap_reservation)
        val titleLabel = context.getString(R.string.reservation_title)
        val noConnectionText = context.getString(R.string.no_connection)

        // Verify Header
        composeTestRule.onNodeWithText(recapLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(titleLabel).assertIsDisplayed()

        // Verify No Connection Message
        composeTestRule.onNodeWithText(noConnectionText).assertIsDisplayed()
    }
}
