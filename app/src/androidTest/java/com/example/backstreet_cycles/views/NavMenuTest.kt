package com.example.backstreet_cycles.views

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import kotlinx.android.synthetic.main.activity_homepage.*
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class NavMenuTest {

    @Test
    fun test_navToggle_toNavMenu() {;
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nav_view)).perform(click())
        onView(withId(R.id.changePassword)).check(matches(isDisplayed()))
        onView(withId(R.id.about)).check(matches(isDisplayed()))
        onView(withId(R.id.help)).check(matches(isDisplayed()))
        onView(withId(R.id.logout)).check(matches(isDisplayed()))
    }

    @Test
    fun test_viewProfileButton_toEditProfileActivity() {
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nav_view)).perform(click())
        onView(withId(R.id.profile)).perform(click())
        onView(withId(R.id.editUserProfile)).check(matches(isDisplayed()))
    }

    @Test
    fun test_changePassword_toChangeEmailOrPasswordActivity() {
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nav_view)).perform(click())
        onView(withId(R.id.changePassword)).perform(click())
        onView(withId(R.id.changeEmailOrPassword)).check(matches(isDisplayed()))
    }

    /*@Test
    fun test_aboutButton_???()
    {
        val activityScenario = ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nav_view)).perform(click())
        onView(withId(R.id.about)).perform(ViewActions.click())
        onView(withId(R.id.aboutActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_helpButton_???()
    {
        val activityScenario = ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nav_view)).perform(click())
        onView(withId(R.id.help)).perform(ViewActions.click())
        onView(withId(R.id.helpActivity)).check(matches(isDisplayed()))
    }*/

    @Test
    fun test_logoutButton_toLoginActivity() {
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nav_view)).perform(click())
        onView(withId(R.id.logout)).perform(click())
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_backPress_toPreviousPage() {
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nav_view)).perform(click())
        pressBack()
        onView(withId(R.id.homePageActivity)).check(matches(isDisplayed()))
    }
}