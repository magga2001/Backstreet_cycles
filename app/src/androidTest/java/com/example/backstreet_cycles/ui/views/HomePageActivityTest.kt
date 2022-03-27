//package com.example.backstreet_cycles.ui.views
//
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.*
//import androidx.test.rule.GrantPermissionRule
//import com.example.backstreet_cycles.R
//
//import org.junit.Before
//import org.junit.Test
//
//class HomePageActivityTest {
//
//    @Before
//    fun setUp() {
//        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
//        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_NETWORK_STATE)
//        GrantPermissionRule.grant(android.Manifest.permission.INTERNET)
//    }
//
//    @Test
//    fun test_map_on_homepage_is_displayed() {
//        ActivityScenario.launch(HomePageActivity::class.java)
//        // Checking whether the map is displayed
//        onView(withId(R.id.homepage_mapView)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_bottom_sheet_is_displayed() {
//        ActivityScenario.launch(HomePageActivity::class.java)
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_drawer_layout_shown() {
//        ActivityScenario.launch(HomePageActivity::class.java)
//        onView(withId(R.id.HomePageActivity)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_three_buttons_shown() {
//        ActivityScenario.launch(HomePageActivity::class.java)
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        //Checking whether the three buttons at the top of the bottom sheet are displayed
//        onView(withId(R.id.myLocationButton)).check(matches(isDisplayed()))
//        onView(withId(R.id.addingBtn)).check(matches(isDisplayed()))
//        onView(withId(R.id.nextPageButton)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_current_location_card_shown() {
//        ActivityScenario.launch(HomePageActivity::class.java)
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        // Checking whether a card is shown in the bottom sheet
//        onView(withId(R.id.locationDataCardView)).check(matches(isDisplayed()))
//        onView(withId(R.id.LocationDataCardName)).check(matches(withText("Current Location")))
//    }
//
//    @Test
//    fun navigation_drawer_shows_about_button() {
//        ActivityScenario.launch(HomePageActivity::class.java)
//
//
//
//    }
//
//    @Test
//    fun navigation_drawer_shows_help_button() {
//
//    }
//
//    @Test
//    fun navigation_drawer_shows_planjourney_button() {
//
//    }
//}