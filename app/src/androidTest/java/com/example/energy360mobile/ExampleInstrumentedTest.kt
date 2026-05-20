package com.example.energy360mobile

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Automated Test Suite that runs all UI and Instrumented tests in the project.
 * You can run this class to execute all the tests listed below in one go.
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
    LoginUiTest::class,
    HomePageUiTest::class,
    BottomBarTest::class,
    NavGraphTest::class,
    ReservationsUiTest::class,
    ServicesUiTest::class,
    TrainingUiTest::class,
    LoginNoConnectionUiTest::class,
    ReservationsNoConnectionUiTest::class,
    SearchPageNoConnectionUiTest::class
)
class AllUiTestSuite
