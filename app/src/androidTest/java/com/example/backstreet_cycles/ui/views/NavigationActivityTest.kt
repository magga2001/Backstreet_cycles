package com.example.backstreet_cycles.ui.views

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class NavigationActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET
        )

    @get:Rule
    val activityRule = ActivityScenarioRule(NavigationActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        ActivityScenario.launch(NavigationActivity::class.java)
    }

    @Test
    fun navigation_activity_is_displayed(){
        Intents.init()
        intending(hasComponent(NavigationActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun navigation_tripProgressCard_is_displayed(){
        onView(withId(R.id.tripProgressCard)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_tripProgressView_is_displayed(){
        onView(withId(R.id.tripProgressView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
    @Test
    fun navigation_imageViewStop_is_displayed(){
        onView(withId(R.id.stop)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_soundButton_is_displayed(){
        onView(withId(R.id.soundButton)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_routeOverview_is_displayed(){
        onView(withId(R.id.routeOverview)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_MapboxRecenterButton_is_displayed(){
        onView(withId(R.id.recenter)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_click_on_cancel_journey(){
        onView(withId(R.id.stop)).perform(click())
        Intents.init()
        intending(hasComponent(LoadingActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_click_on_back_button_to_journey(){
        pressBackUnconditionally()
        Intents.init()
        intending(hasComponent(LoadingActivity::class.qualifiedName))
        Intents.release()
    }

}