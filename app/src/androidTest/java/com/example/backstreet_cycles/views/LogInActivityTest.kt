package com.example.backstreet_cycles.views
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBackUnconditionally
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import org.junit.Test
import org.junit.runner.RunWith

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
    fun test_buttonCreateAccount_is_visible() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.buttonCreateAccount)).check(matches(isDisplayed()))

    }

    @Test
    fun test_buttonLogin_is_visible() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_email_is_visible() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.et_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_password_is_visible() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.et_password)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigation_createAccount() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.buttonCreateAccount)).perform(click())
        onView(withId(R.id.signUpActivity)).check(matches(isDisplayed()))

    }
    @Test
    fun test_backPress_onLogInActivity() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        pressBack()
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))


    }

    @Test
    fun test_backPress_toLogInActivity() {
        val activityScenario=ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.buttonCreateAccount)).perform(click())
        onView(withId(R.id.signUpActivity)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))


    }


}