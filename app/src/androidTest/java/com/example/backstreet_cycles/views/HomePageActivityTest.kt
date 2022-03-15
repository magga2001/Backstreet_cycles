package com.example.backstreet_cycles.views

import org.junit.Assert.*

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R

import org.junit.After
import org.junit.Before
import org.junit.Test

class HomePageActivityTest {

    @Before
    fun setUp() {
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_NETWORK_STATE)
        GrantPermissionRule.grant(android.Manifest.permission.INTERNET)
    }

    @Test
    fun test_map_on_homepage_is_displayed(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.mapView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_bottom_sheet_is_displayed(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
    }


}