package com.example.backstreet_cycles.views
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)


class MainActivityTest{
        @Test
        fun test_activity_is_in_view() {
            val activityScenario = ActivityScenario.launch(MainActivity::class.java)
            onView(withId(R.id.mainActivity)).check(matches(isDisplayed()))
        }

        @Test
        fun test_title_is_visible() {
            val activityScenario=ActivityScenario.launch(MainActivity::class.java)
            onView(withId(R.id.textView)).check(matches(isDisplayed()))
        }

        @Test
        fun test_buttonLogOut_is_visible() {
            val activityScenario=ActivityScenario.launch(MainActivity::class.java)
            onView(withId(R.id.buttonLogOut)).check(matches(isDisplayed()))
        }

        @Test
        fun test_buttonUpdateProfile_is_visible() {
            val activityScenario=ActivityScenario.launch(MainActivity::class.java)
            onView(withId(R.id.buttonUpdateProfile)).check(matches(isDisplayed()))
        }

    @Test
    fun test_navigation_logOut() {
        val activityScenario=ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.buttonLogOut)).perform(ViewActions.click())
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))

    }

    @Test
    fun test_navigation_updateProfile() {
        val activityScenario=ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.buttonUpdateProfile)).perform(ViewActions.click())
        onView(withId(R.id.editUserProfile)).check(matches(isDisplayed()))

    }

    @Test
    fun test_backPress_toMainActivity() {
        val activityScenario=ActivityScenario.launch(MainActivity::class.java)
        Espresso.pressBack()
        onView(withId(R.id.mainActivity)).check(matches(isDisplayed()))

    }

}