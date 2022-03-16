package com.example.backstreet_cycles.views
import org.junit.runner.RunWith
import org.junit.runners.Suite
//Log out user before running this test suite
@RunWith(Suite::class)
@Suite.SuiteClasses(
    LogInActivityTest::class,
    SignUpActivityTest::class,
)
class LoginSignUpTestSuite