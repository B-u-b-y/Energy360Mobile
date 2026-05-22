package com.example.energy360mobile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.energy360mobile.components.ReservationsWithConnection
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReservationsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: ReservationsViewModel

    private val futureDate = System.currentTimeMillis() + 1000 * 60 * 60 * 24 // +1 day

    private val reservation = ReservationsEntity(
        id = "1",
        eventName = "Yoga Class",
        userId = "user1",
        day = "Monday",
        date = futureDate,
        place = "Rome",
        isReserved = true
    )

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
    }

    // ---------------------------
    // EMPTY STATE
    // ---------------------------
    @Test
    fun reservations_emptyState_showsHint() {

        every { viewModel.reservations } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            ReservationsWithConnection(viewModel)
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val noReservationsText = context.getString(R.string.no_reservations)

        composeTestRule
            .onNodeWithText(noReservationsText)
            .assertExists()
    }

    // ---------------------------
    // LIST RENDERING
    // ---------------------------
    @Test
    fun reservations_showsUpcomingReservation() {

        every { viewModel.reservations } returns MutableStateFlow(listOf(reservation))

        composeTestRule.setContent {
            ReservationsWithConnection(viewModel)
        }

        composeTestRule
            .onNodeWithText("Yoga Class")
            .assertExists()

        // Match substring since the date and brackets are also present in the text
        composeTestRule
            .onNodeWithText("Rome", substring = true)
            .assertExists()
    }

    // ---------------------------
    // ONLY UPCOMING (isPast FILTER)
    // ---------------------------
    @Test
    fun reservations_filtersPastReservations() {

        val pastReservation = reservation.copy(
            id = "2",
            eventName = "Past Class",
            date = System.currentTimeMillis() - 100000 // past
        )

        every { viewModel.reservations } returns MutableStateFlow(
            listOf(reservation, pastReservation)
        )

        composeTestRule.setContent {
            ReservationsWithConnection(viewModel)
        }

        composeTestRule
            .onNodeWithText("Yoga Class")
            .assertExists()

        composeTestRule
            .onNodeWithText("Rome", substring = true)
            .assertExists()

        // past item should NOT appear
        composeTestRule
            .onNodeWithText("Past Class")
            .assertDoesNotExist()
    }

    // ---------------------------
    // DELETE BUTTON EXISTS
    // ---------------------------
    @Test
    fun reservations_deleteButton_isClickable() {

        every { viewModel.reservations } returns MutableStateFlow(listOf(reservation))

        composeTestRule.setContent {
            ReservationsWithConnection(viewModel)
        }

        composeTestRule
            .onNodeWithTag("deleteReservation")
            .assertExists()
            .assertHasClickAction()
    }

    // ---------------------------
    // DELETE CALLBACK TRIGGERED
    // ---------------------------
    @Test
    fun reservations_deleteButton_callsViewModel() {

        every { viewModel.reservations } returns MutableStateFlow(listOf(reservation))

        every { viewModel.deleteReservation(any()) } just Runs

        composeTestRule.setContent {
            ReservationsWithConnection(viewModel)
        }

        composeTestRule
            .onNodeWithTag("deleteReservation")
            .performClick()

        verify(exactly = 1) {
            viewModel.deleteReservation(reservation)
        }
    }
}