/**
 * In order to run the coverage, comment out this file so every activity test does
 * not run twice.
 * However, you can use this file inorder to run all the activity tests in Android
 * Studio
 */

package com.example.backstreet_cycles.ui.views

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(

    SplashScreenActivityTest::class,
    SignUpActivityTest::class,
    LogInActivityTest::class,
    HomePageActivityTest::class,
    EditUserProfileActivityTest::class,
    JourneyActivityTest::class,
    AboutActivityTest::class,
    JourneyHistoryActivityTest::class,
    NavMenuTest::class,
    ChangePasswordActivityTest::class,
    FAQActivityTest::class,
    ForgotPasswordActivityTest::class,
    NavigationActivityTest::class
)

class ActivitiesTestSuite