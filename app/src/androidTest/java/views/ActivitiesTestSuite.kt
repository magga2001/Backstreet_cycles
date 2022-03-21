package views
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.Suite

//Ensure user is logged out before running these tests

@get:Rule
val fineLocPermissionRule: GrantPermissionRule =
    GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.INTERNET)

@RunWith(Suite::class)
@Suite.SuiteClasses(
    SplashScreenActivityTest::class,
    SignUpActivityTest::class,
    LogInActivityTest::class,
    EditUserProfileActivityTest::class,
    HomePageActivityTest::class,
    JourneyActivityTest::class,
    NavMenuTest::class,
)
class ActivitiesTestSuite