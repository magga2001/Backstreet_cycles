package com.example.backstreet_cycles.ui.views

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.ui.views.NavigationActivity
import org.junit.After

import org.junit.Before
import org.junit.Test

class NavigationActivityTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(NavigationActivity::class.java)
        Intents.init()
    }

    @Test
    fun navigation_activity_is_displayed(){
        intending(hasComponent(NavigationActivity::class.qualifiedName))
    }

    @Test
    fun navigation_mapView_is_displayed(){
        onView(withId(R.id.mapView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_tripProgressCard_is_not_displayed(){
        onView(withId(R.id.tripProgressCard)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun navigation_tripProgressView_is_displayed(){
        onView(withId(R.id.tripProgressView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
    @Test
    fun navigation_imageViewStop_is_displayed(){
        onView(withId(R.id.stop)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun navigation_maneuverView_is_not_displayed(){
        onView(withId(R.id.maneuverView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun navigation_soundButton_is_not_displayed(){
        onView(withId(R.id.soundButton)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun navigation_routeOverview_is_not_displayed(){
        onView(withId(R.id.routeOverview)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun navigation_MapboxRecenterButton_is_not_displayed(){
        onView(withId(R.id.recenter)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}