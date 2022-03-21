package views

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.views.SplashScreenActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SplashScreenActivityTest{
    @Test
    fun test_splash_screen_activity_is_in_view() {
        ActivityScenario.launch(SplashScreenActivity::class.java)
        onView(withId(R.id.splashScreenActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_progress_bar_is_in_view() {
        ActivityScenario.launch(SplashScreenActivity::class.java)
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }
}