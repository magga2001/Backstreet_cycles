package com.example.backstreet_cycles.views
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LogInActivityTest::class,
    SignUpActivityTest::class,
    MainActivityTest::class,
    EditUserProfileActivityTest::class
)
class UITestSuite