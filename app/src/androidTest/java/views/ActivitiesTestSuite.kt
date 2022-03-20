package views
import org.junit.runner.RunWith
import org.junit.runners.Suite

//Ensure user is logged out before running these tests
@RunWith(Suite::class)
@Suite.SuiteClasses(
    EditUserProfileActivityTest::class,
    HomePageActivityTest::class,
    JourneyActivityTest::class,
    LogInActivityTest::class,
    NavMenuTest::class,
    SignUpActivityTest::class,
    SplashScreenActivityTest::class
)
class ActivitiesTestSuite