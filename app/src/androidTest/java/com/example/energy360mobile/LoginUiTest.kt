package com.example.energy360mobile

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.energy360mobile.components.Login
import com.example.energy360mobile.viewmodels.UserViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.coEvery
import org.junit.Before
import org.junit.runner.RunWith
import androidx.test.platform.app.InstrumentationRegistry
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText

@RunWith(AndroidJUnit4::class)
class LoginUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: UserViewModel

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
    }

    @Test
    fun loginScreen_displaysAllComponents() {
        composeTestRule.setContent {
            Login(
                viewModel = mockViewModel,
                rootNavController = rememberNavController(),
                innerNavController = rememberNavController()
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val emailLabel = context.getString(R.string.email_label)
        val passwordLabel = context.getString(R.string.password_label)
        val welcomeText = context.getString(R.string.welcome_login)

        composeTestRule.onNodeWithText("Energy360").assertIsDisplayed()
        composeTestRule.onNodeWithText(welcomeText).assertIsDisplayed()
        composeTestRule.onNodeWithText(emailLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(passwordLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText("LOGIN").assertIsDisplayed()
    }

    @Test
    fun loginScreen_userCanTypeEmailAndPassword() {
        composeTestRule.setContent {
            Login(
                viewModel = mockViewModel,
                rootNavController = rememberNavController(),
                innerNavController = rememberNavController()
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val emailLabel = context.getString(R.string.email_label)
        val passwordLabel = context.getString(R.string.password_label)

        composeTestRule.onNodeWithText(emailLabel)
            .performTextInput("test@email.com")

        composeTestRule.onNodeWithText(passwordLabel)
            .performTextInput("mypassword")

        composeTestRule.onNodeWithText("test@email.com").assertExists()
        // Password shouldn't be visible by default (PasswordVisualTransformation)
        // We check the password field doesn't show the plain text using its testTag
        composeTestRule.onNodeWithTag("passwordField")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, androidx.compose.ui.text.AnnotatedString("••••••••••")))
    }

    @Test
    fun loginScreen_passwordVisibilityToggleWorks() {
        composeTestRule.setContent {
            Login(
                viewModel = mockViewModel,
                rootNavController = rememberNavController(),
                innerNavController = rememberNavController()
            )
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val passwordLabel = context.getString(R.string.password_label)

        composeTestRule.onNodeWithText(passwordLabel)
            .performTextInput("mypassword")

        // Check it's hidden initially (masked)
        composeTestRule.onNodeWithTag("passwordField")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, androidx.compose.ui.text.AnnotatedString("••••••••••")))

        // Toggle visibility
        composeTestRule.onNodeWithTag("passwordVisibility")
            .performClick()

        // Now it should be visible
        composeTestRule.onNodeWithTag("passwordField")
            .assertTextContains("mypassword")

        // Toggle back
        composeTestRule.onNodeWithTag("passwordVisibility")
            .performClick()

        // Should be hidden again
        composeTestRule.onNodeWithTag("passwordField")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, androidx.compose.ui.text.AnnotatedString("••••••••••")))
    }

    @Test
    fun loginScreen_showsErrorForInvalidCredentials() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val emailLabel = context.getString(R.string.email_label)
        val passwordLabel = context.getString(R.string.password_label)
        val errorText = context.getString(R.string.invalid_credentials)

        coEvery {
            mockViewModel.checkCredentials(any(), any())
        } returns false

        composeTestRule.setContent {
            Login(
                viewModel = mockViewModel,
                rootNavController = rememberNavController(),
                innerNavController = rememberNavController()
            )
        }

        composeTestRule.onNodeWithText(emailLabel)
            .performTextInput("wrong@email.com")

        composeTestRule.onNodeWithText(passwordLabel)
            .performTextInput("wrongpass")

        composeTestRule.onNodeWithText("LOGIN").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(errorText)
            .assertIsDisplayed()
    }

    @Test
    fun loginScreen_registerButtonIsDisplayed() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val registerText = context.getString(R.string.not_account)

        composeTestRule.setContent {
            Login(
                viewModel = mockViewModel,
                rootNavController = rememberNavController(),
                innerNavController = rememberNavController()
            )
        }

        composeTestRule.onNodeWithText(registerText)
            .assertExists()
            .assertHasClickAction()
    }
}
