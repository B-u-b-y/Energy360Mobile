package com.example.energy360mobile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.energy360mobile.components.ServicesPage
import com.example.energy360mobile.databases.reservations.ReservationsEntity
import com.example.energy360mobile.databases.services.ServicesEntity
import com.example.energy360mobile.viewmodels.ReservationsViewModel
import com.example.energy360mobile.viewmodels.ServicesViewModel
import com.example.energy360mobile.viewmodels.UserViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ServicesUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockServicesViewModel: ServicesViewModel
    private lateinit var mockReservationsViewModel: ReservationsViewModel
    private lateinit var mockUserViewModel: UserViewModel

    private val servicesFlow = MutableStateFlow<List<ServicesEntity>>(emptyList())
    private val reservationsFlow = MutableStateFlow<List<ReservationsEntity>>(emptyList())
    private val userFlow = MutableStateFlow<com.example.energy360mobile.databases.userData.UserEntity?>(null)

    @Before
    fun setup() {
        mockServicesViewModel = mockk(relaxed = true)
        mockReservationsViewModel = mockk(relaxed = true)
        mockUserViewModel = mockk(relaxed = true)

        every { mockServicesViewModel.services } returns servicesFlow
        every { mockReservationsViewModel.reservations } returns reservationsFlow
        every { mockUserViewModel.userData } returns userFlow
    }

    @Test
    fun servicesPage_emptyState_displaysNoCoursesMessage() {
        servicesFlow.value = emptyList()

        composeTestRule.setContent {
            ServicesPage(
                viewModel = mockServicesViewModel,
                reservationsViewModel = mockReservationsViewModel,
                userViewModel = mockUserViewModel,
                onBack = {}
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val noCoursesText = context.getString(R.string.no_courses_available)

        composeTestRule.onNodeWithText(noCoursesText).assertIsDisplayed()
    }

    @Test
    fun servicesPage_withServices_displaysCards() {
        val currentTime = System.currentTimeMillis()
        val service = ServicesEntity(
            id = 1,
            name = "Yoga",
            category = "Benessere",
            description = "Relaxing yoga",
            duration = "60 min",
            place = "Hall A",
            date = currentTime + 86400000 // Tomorrow
        )
        servicesFlow.value = listOf(service)

        composeTestRule.setContent {
            ServicesPage(
                viewModel = mockServicesViewModel,
                reservationsViewModel = mockReservationsViewModel,
                userViewModel = mockUserViewModel,
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("Yoga").assertIsDisplayed()
        composeTestRule.onNodeWithText("Benessere").assertIsDisplayed()
        composeTestRule.onNodeWithText("Relaxing yoga").assertIsDisplayed()
    }

    @Test
    fun servicesPage_clickingCard_showsReserveButton() {
        val currentTime = System.currentTimeMillis()
        val service = ServicesEntity(
            id = 1,
            name = "Yoga",
            category = "Benessere",
            description = "Relaxing yoga",
            duration = "60 min",
            place = "Hall A",
            date = currentTime + 86400000
        )
        servicesFlow.value = listOf(service)

        composeTestRule.setContent {
            ServicesPage(
                viewModel = mockServicesViewModel,
                reservationsViewModel = mockReservationsViewModel,
                userViewModel = mockUserViewModel,
                onBack = {}
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val reserveText = context.getString(R.string.reserve)

        // Reserve button should not be visible initially
        composeTestRule.onNodeWithText(reserveText).assertDoesNotExist()

        // Click on the card
        composeTestRule.onNodeWithText("Yoga").performClick()

        // Now reserve button should be visible
        composeTestRule.onNodeWithText(reserveText).assertIsDisplayed()
    }

    @Test
    fun servicesPage_reserving_callsViewModel() {
        val currentTime = System.currentTimeMillis()
        val service = ServicesEntity(
            id = 1,
            name = "Yoga",
            category = "Benessere",
            description = "Relaxing yoga",
            duration = "60 min",
            place = "Hall A",
            date = currentTime + 86400000
        )
        servicesFlow.value = listOf(service)

        composeTestRule.setContent {
            ServicesPage(
                viewModel = mockServicesViewModel,
                reservationsViewModel = mockReservationsViewModel,
                userViewModel = mockUserViewModel,
                onBack = {}
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val reserveText = context.getString(R.string.reserve)

        // Click card to show button
        composeTestRule.onNodeWithText("Yoga").performClick()
        
        // Click reserve button
        composeTestRule.onNodeWithText(reserveText).performClick()

        // Verify viewmodel call
        coVerify { mockReservationsViewModel.insertReservation(any()) }
    }

    @Test
    fun servicesPage_alreadyReserved_showsBookedStatus() {
        val currentTime = System.currentTimeMillis()
        val service = ServicesEntity(
            id = 1,
            name = "Yoga",
            category = "Benessere",
            description = "Relaxing yoga",
            duration = "60 min",
            place = "Hall A",
            date = currentTime + 86400000
        )
        servicesFlow.value = listOf(service)
        
        // Mock that it is already reserved
        val reservation = ReservationsEntity(
            id = "101",
            eventName = "Yoga",
            userId = "user1",
            day = "Monday",
            date = currentTime + 86400000,
            place = "Hall A",
            isReserved = true
        )
        reservationsFlow.value = listOf(reservation)

        composeTestRule.setContent {
            ServicesPage(
                viewModel = mockServicesViewModel,
                reservationsViewModel = mockReservationsViewModel,
                userViewModel = mockUserViewModel,
                onBack = {}
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val bookedText = context.getString(R.string.already_reserved)

        // Should show "Booked" status immediately for reserved service
        composeTestRule.onNodeWithText(bookedText).assertIsDisplayed()
    }

    @Test
    fun servicesPage_backButton_triggersCallback() {
        var backClicked = false
        
        composeTestRule.setContent {
            ServicesPage(
                viewModel = mockServicesViewModel,
                reservationsViewModel = mockReservationsViewModel,
                userViewModel = mockUserViewModel,
                onBack = { backClicked = true }
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val backContentDesc = context.getString(R.string.back)

        composeTestRule.onNodeWithContentDescription(backContentDesc).performClick()

        assert(backClicked)
    }
}
