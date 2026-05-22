package com.example.energy360mobile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.energy360mobile.components.TrainingSession
import com.example.energy360mobile.databases.exercises.ExercisesEntity
import com.example.energy360mobile.viewmodels.ExercisesViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class TrainingUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: ExercisesViewModel
    private val exerciseFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())
    private val completedExercisesFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())
    private val availableExercisesFlow = MutableStateFlow<List<ExercisesEntity>>(emptyList())

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.exercise } returns exerciseFlow
        every { mockViewModel.completedExercises } returns completedExercisesFlow
        every { mockViewModel.availableExercises } returns availableExercisesFlow
    }

    private fun getCurrentDayKey(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            Calendar.SUNDAY -> "Sun"
            else -> "Mon"
        }
    }

    @Test
    fun trainingSession_displaysHeader() {
        composeTestRule.setContent {
            TrainingSession(viewModel = mockViewModel, onBack = {})
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val title = context.getString(R.string.training_title)
        val intro = context.getString(R.string.training_intro)

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(intro).assertIsDisplayed()
    }

    @Test
    fun trainingSession_emptyState_displaysNoTrainingMessage() {
        exerciseFlow.value = emptyList()

        composeTestRule.setContent {
            TrainingSession(viewModel = mockViewModel, onBack = {})
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val noTrainingText = context.getString(R.string.no_training)

        composeTestRule.onNodeWithText(noTrainingText).assertIsDisplayed()
    }

    @Test
    fun trainingSession_displaysExercisesForCurrentDay() {
        val currentDay = getCurrentDayKey()
        val exercise = ExercisesEntity(
            id = 1,
            name = "Pushups",
            sets = 3,
            reps = "15",
            day = currentDay
        )
        exerciseFlow.value = listOf(exercise)

        composeTestRule.setContent {
            TrainingSession(viewModel = mockViewModel, onBack = {})
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val summaryText = context.getString(R.string.exercise_summary, 3, "15")

        composeTestRule.onNodeWithText("Pushups").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(summaryText, substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun trainingSession_toggleCompletion_callsViewModel() {
        val currentDay = getCurrentDayKey()
        val exercise = ExercisesEntity(
            id = 1,
            name = "Squats",
            sets = 3,
            reps = "12",
            day = currentDay,
            isCompleted = false
        )
        exerciseFlow.value = listOf(exercise)

        composeTestRule.setContent {
            TrainingSession(viewModel = mockViewModel, onBack = {})
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val doneDesc = context.getString(R.string.done_exercise)

        // Click the completion checkbox icon
        composeTestRule.onNodeWithContentDescription(doneDesc, useUnmergedTree = true)
            .performScrollTo()
            .performClick()

        verify { mockViewModel.updateExerciseCompletion(1, true, any(), any()) }
    }

    @Test
    fun trainingSession_fab_opensAddExerciseSheet() {
        composeTestRule.setContent {
            TrainingSession(viewModel = mockViewModel, onBack = {})
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val addExerciseDesc = context.getString(R.string.add_exercise)
        val chooseExerciseTitle = context.getString(R.string.choose_exercise)

        // Click the FAB
        composeTestRule.onNodeWithContentDescription(addExerciseDesc, useUnmergedTree = true).performClick()

        // Verify that fetchAvailableExercises was called
        verify { mockViewModel.fetchAvailableExercises() }

        // Verify the Bottom Sheet title is displayed
        composeTestRule.onNodeWithText(chooseExerciseTitle).assertIsDisplayed()
    }

    @Test
    fun trainingSession_dayFilter_changesDisplayedExercises() {
        val mondayExercise = ExercisesEntity(id = 1, name = "Monday Bench", day = "Mon")
        val tuesdayExercise = ExercisesEntity(id = 2, name = "Tuesday Deadlift", day = "Tue")
        exerciseFlow.value = listOf(mondayExercise, tuesdayExercise)

        composeTestRule.setContent {
            TrainingSession(viewModel = mockViewModel, onBack = {})
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val monLabel = context.getString(R.string.monday_day)
        val tueLabel = context.getString(R.string.tuesday_day)

        // Select Monday
        composeTestRule.onNodeWithText(monLabel).performClick()
        composeTestRule.onNodeWithText("Monday Bench").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Tuesday Deadlift").assertDoesNotExist()

        // Select Tuesday
        composeTestRule.onNodeWithText(tueLabel).performClick()
        composeTestRule.onNodeWithText("Tuesday Deadlift").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Monday Bench").assertDoesNotExist()
    }

    @Test
    fun trainingSession_backButton_triggersCallback() {
        var backClicked = false
        composeTestRule.setContent {
            TrainingSession(viewModel = mockViewModel, onBack = { backClicked = true })
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val backDesc = context.getString(R.string.back)

        composeTestRule.onNodeWithContentDescription(backDesc, useUnmergedTree = true).performClick()

        assert(backClicked)
    }
}
