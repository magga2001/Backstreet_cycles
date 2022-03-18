package com.example.backstreet_cycles.views
import org.junit.runner.RunWith
import org.junit.runners.Suite

//Ensure user is logged out before running these tests
@RunWith(Suite::class)
@Suite.SuiteClasses(
    EditUserProfileActivityTest::class,
    HomePageActivityTest::class,
    LogInActivityTest::class,
    NavMenuTest::class,
    SignUpActivityTest::class,
    SplashScreenActivityTest::class
)
class ActivitiesTestSuite