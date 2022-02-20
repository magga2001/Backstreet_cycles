package com.example.backstreet_cycles.views
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4ClassRunner::class)
class LogInActivityTest{
    @Test
    fun test_activity_is_in_view() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_title_is_visible() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.et_log_in_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_buttonCreateAccount_is_visble() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.buttonCreateAccount)).check(matches(isDisplayed()))

    }

    @Test
    fun test_buttonLogin_is_visible() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()))
    }

}