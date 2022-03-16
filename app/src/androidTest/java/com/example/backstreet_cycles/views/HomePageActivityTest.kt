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
    fun navigation_drawer_shows_about_button() {
        ActivityScenario.launch(HomePageActivity::class.java)



    }

    @Test
    fun navigation_drawer_shows_help_button() {

    }

    @Test
    fun navigation_drawer_shows_planjourney_button() {

    }

    @Test
    fun bottom_sheet_invoked_show_users_field() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether bottom sheet is displayed
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
        //Checking whether the users field is displayed
        onView(withId(R.id.Users)).check(matches(withText("Users:")))
    }

    @Test
    fun bottom_sheet_invoked_show_number_of_users_is_one() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether bottom sheet is displayed
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
        //Checking whether the number of users is one
        onView(withId(R.id.UserNumber)).check(matches(withText("1")))
    }

    @Test
    fun bottom_sheet_invoked_show_increment_and_decrement_buttons() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether bottom sheet is displayed
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
        //Checking whether the increment and decrement buttons are displayed
        onView(withId(R.id.decrementButton)).check(matches(isDisplayed()))
        onView(withId(R.id.incrementButton)).check(matches(isDisplayed()))
    }

    @Test
    fun increment_button_clicked_number_of_users_incremented_by_one() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether bottom sheet is displayed
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
        //Click increment button
        onView(withId(R.id.incrementButton)).perform(ViewActions.click())
        //Checking if number of users changed to two
        onView(withId(R.id.UserNumber)).check(matches(withText("2")))

    }

    @Test
    fun decrement_button_clicked_number_of_users_decremented_by_one() {
        ActivityScenario.launch(HomePageActivity::class.java)
        // Checking whether bottom sheet is displayed
        onView(withId(R.id.bottom_sheet_view)).check(matches(isDisplayed()))
        //Click increment button
        onView(withId(R.id.incrementButton)).perform(ViewActions.click())
        //Checking if number of users changed to two
        onView(withId(R.id.UserNumber)).check(matches(withText("2")))
        //Click decrement button
        onView(withId(R.id.decrementButton)).perform(ViewActions.click())
        //Checking if number of users changed back to one
        onView(withId(R.id.UserNumber)).check(matches(withText("1")))
    }



}