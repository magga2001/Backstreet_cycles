package com.example.backstreet_cycles.views

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R

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
    fun test_map_on_homepage_is_displayed() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether the map is displayed
        onView(withId(R.id.mapView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_bottom_sheet_is_displayed() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether bottom sheet is displayed
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_drawer_layout_shown() {
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.HomePageActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_three_buttons_shown() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether bottom sheet is displayed
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
        //Checking whether the three buttons at the top of the bottom sheet are displayed
        onView(withId(R.id.myLocationButton)).check(matches(isDisplayed()))
        onView(withId(R.id.addingBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.nextPageButton)).check(matches(isDisplayed()))
    }

    @Test
    fun test_current_location_card_shown() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether bottom sheet is displayed
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
        // Checking whether a card is shown in the bottom sheet
        onView(withId(R.id.cardView)).check(matches(isDisplayed()))
        onView(withId(R.id.card_name)).check(matches(withText("Current Location")))
    }

    @Test
    fun test_addStopbtn_is_enabled(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.addingBtn)).check(matches(isEnabled()))
    }

    @Test
    fun test_addStopbtn_is_clickable(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.addingBtn)).check(matches(isClickable()))
    }

    @Test
    fun test_currentLocationbtn_is_disabled(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.myLocationButton)).check(matches(isNotEnabled()))
    }

    @Test
    fun test_currentLocationbtn_is_not_clickable(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.myLocationButton)).check(matches(isNotClickable()))
    }
    @Test
    fun test_nextPage_is_disabled(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nextPageButton)).check(matches(isNotEnabled()))
    }

    @Test
    fun test_nextPage_is_not_clickable(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nextPageButton)).check(matches(isNotClickable()))
    }

    @Test
    fun test_recyclerView_is_displayed(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_cardView_is_visible(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.cardView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_cardView_is_swipeable(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.cardView)).perform(ViewActions.swipeLeft())
    }

    @Test
    fun test_cardView_is_draggable(){
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.cardView)).perform(ViewActions.swipeUp())
        onView(withId(R.id.cardView)).perform(ViewActions.swipeDown())
    }

    @Test
    fun navigation_drawer_shows_about_button() {

    }

    @Test
    fun navigation_drawer_shows_help_button() {

    }

    @Test
    fun navigation_drawer_shows_planjourney_button() {

    }
}