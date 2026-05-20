package com.example.energy360mobile

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.energy360mobile.components.noConnection.LoginNoConnection
import com.example.energy360mobile.components.noConnection.RegisterNoConnection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginNoConnectionUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginNoConnection_displaysAllComponents() {
        composeTestRule.setContent {
            LoginNoConnection()
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val welcomeText = context.getString(R.string.welcome_login)
        val noConnectionText = context.getString(R.string.no_connection)
        val emailLabel = context.getString(R.string.email_label)
        val passwordLabel = context.getString(R.string.password_label)
        composeTestRule.onNodeWithText("Energy360").assertIsDisplayed()
        composeTestRule.onNodeWithText(welcomeText).assertIsDisplayed()
        composeTestRule.onNodeWithText(noConnectionText).assertIsDisplayed()

        // Fields should be disabled
        composeTestRule.onNodeWithText(emailLabel).assertIsNotEnabled()
        composeTestRule.onNodeWithText(passwordLabel).assertIsNotEnabled()

        // Login button should be disabled
        composeTestRule.onNodeWithText("LOGIN").assertIsNotEnabled()
    }
    @Test
    fun registerNoConnection_displaysAllComponents() {
        composeTestRule.setContent {
            RegisterNoConnection()
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val createAccountText = context.getString(R.string.create_account)
        val noConnectionText = context.getString(R.string.no_connection)
        val fullNameLabel = context.getString(R.string.full_name)
        val registerText = context.getString(R.string.register)
        composeTestRule.onNodeWithText(createAccountText).assertIsDisplayed()
        composeTestRule.onNodeWithText(noConnectionText).assertIsDisplayed()

        // Fields should be disabled
        composeTestRule.onNodeWithText(fullNameLabel).assertIsNotEnabled()
        composeTestRule.onNodeWithText("Email").assertIsNotEnabled()

        // Register button should be disabled
        composeTestRule.onNodeWithText(registerText).assertIsNotEnabled()
    }
}