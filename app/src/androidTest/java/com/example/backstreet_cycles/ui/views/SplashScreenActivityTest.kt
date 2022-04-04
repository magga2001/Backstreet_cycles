package com.example.backstreet_cycles.ui.views

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class SplashScreenActivityTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(SplashScreenActivity::class.java)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_splash_screen_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(SplashScreenActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_image_is_in_view() {
        onView(withId(R.id.backStreet_cycles_image)).check(matches(isDisplayed()))
    }

    @Test
    fun progress_bar_is_displayed(){
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }

    @Test
     fun test_if_user_is_loggedin_goes_to_homepage(){
        test_splash_screen_activity_is_in_view()
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_if_user_is_not_logged_in_goes_to_login(){
        test_splash_screen_activity_is_in_view()
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
        Intents.release()
    }

}