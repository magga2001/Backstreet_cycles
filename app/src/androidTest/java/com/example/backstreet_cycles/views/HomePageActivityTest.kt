package com.example.backstreet_cycles.views

import androidx.appcompat.app.ActionBarDrawerToggle
import org.junit.Assert.*

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import kotlinx.android.synthetic.main.activity_homepage.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class HomePageActivityTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun onCreate() {
    }

    @Test
    fun onMapReady() {
    }

    @Test
    fun onActivityResult() {
    }

    @Test
    fun onRequestPermissionsResult() {
    }

    @Test
    fun onExplanationNeeded() {
    }

    @Test
    fun onPermissionResult() {
    }

    @Test
    fun onOptionsItemSelected() {
    }

    @Test
    fun onStart() {
    }

    @Test
    fun onStop() {
    }

    @Test
    fun onLowMemory() {
    }

    @Test
    fun onDestroy() {
    }

    @Test
    fun test_drawer_layout_shown(){
        val activityScenario=ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.drawerLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun navigation_drawer_shows_about_button(){

    }

    @Test
    fun navigation_drawer_shows_help_button(){

    }

    @Test
    fun navigation_drawer_shows_planjourney_button(){

    }
}