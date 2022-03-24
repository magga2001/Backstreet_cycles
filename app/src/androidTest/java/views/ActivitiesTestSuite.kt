package views
import android.app.Application
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.Suite
import java.lang.Thread.sleep

//Ensure user is logged out before running these tests

@get:Rule
val locationRule: GrantPermissionRule =
    GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.INTERNET)

//@Before
//fun setUp() {
//    Application().onCreate()
//}


@RunWith(Suite::class)
@Suite.SuiteClasses(
    SplashScreenActivityTest::class,
    SignUpActivityTest::class,
    LogInActivityTest::class,
    HomePageActivityTest::class,
    EditUserProfileActivityTest::class,
//    JourneyActivityTest::class,
//    AboutActivityTest::class,
//    CurrentJourneyTest::class,
    NavMenuTest::class
)
class ActivitiesTestSuite