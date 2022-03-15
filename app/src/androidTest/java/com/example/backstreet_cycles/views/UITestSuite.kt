package com.example.backstreet_cycles.views
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LogInActivityTest::class,
    SignUpActivityTest::class,
    HomePageActivity::class, //not all my tests pass
    EditUserProfileActivityTest::class // i do not work
)
class UITestSuite