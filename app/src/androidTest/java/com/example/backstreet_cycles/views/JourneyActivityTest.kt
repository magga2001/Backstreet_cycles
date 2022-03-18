package com.example.backstreet_cycles.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.adapter.PlanJourneyAdapter
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class JourneyActivityTest{

    lateinit var logInRegisterViewModel:LogInRegisterViewModel


    @Before
    fun setUp() {
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_NETWORK_STATE)
        GrantPermissionRule.grant(android.Manifest.permission.INTERNET)
        logInRegisterViewModel= LogInRegisterViewModel(Application())
        logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com","123456")

        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nextPageButton)).perform(ViewActions.click())

        /*if (viewExists(android.R.id.button1)){
            onView(withId(android.R.id.button1)).perform(click())
        }*/
        onView(withId(android.R.id.button1)).perform(click())

    }

    @Test
    fun test_journey_activity_is_visible(){
        onView(withId(R.id.journey_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_bottom_sheet_visible(){
        onView(withId(R.id.bottom_sheet_view_journey)).check(matches(isDisplayed()))
    }

    @Test
    fun test_journey_overview_button_visible(){
        onView(withId(R.id.overview_journey)).check(matches(isDisplayed()))
    }

    @Test
    fun test_start_navigation_button_visible(){
        onView(withId(R.id.start_navigation)).check(matches(isDisplayed()))
    }

    @Test
    fun test_next_button_from_home_to_journey(){
        pressBack()
        onView(withId(R.id.HomePageActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_stops_text_field_displayed(){
        onView(withId(R.id.stopsText)).check(matches(ViewMatchers.withText("stops:")))
    }

    @Test
    fun test_stops_recycling_view_displayed(){
        onView(withId(R.id.plan_journey_recycling_view)).check(matches(isDisplayed()))
    }

    /*@Test
       fun test_journey_overview_clicked(){
            onView(withId(R.id.journey_activity)).check(matches(isDisplayed()))
        }*/

    @Test
    fun test_start_navigation_clicked_goes_to_navigation_activity(){
        onView(withId(R.id.start_navigation)).perform(ViewActions.click())
        onView(withId(R.id.navigationActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_button_expand() {
        onView(withId(R.id.bottom_sheet_view_journey)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.plan_journey_recycling_view)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.plan_journey_recycling_view)).perform(RecyclerViewActions.scrollToPosition<PlanJourneyAdapter.ViewHolder>(0))

        onView(ViewMatchers.withId(R.id.plan_journey_recycling_view))
            // scrollTo will fail the test if no item matches.
            .perform(RecyclerViewActions.actionOnItemAtPosition<PlanJourneyAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.setNav1)).check(matches(isDisplayed()))
        onView(withId(R.id.setNav2)).check(matches(isDisplayed()))
        onView(withId(R.id.setNav3)).check(matches(isDisplayed()))



        //
        //        onView(withId(R.id.bottom_sheet_journey)).perform(ViewActions.swipeUp())
        //        onView(withId(R.id.button_expand)).check(matches(isDisplayed()))
    }


    @Test
    fun test_stop_clicked_set_navigation_displayed(){
        onView(withId(R.id.bottom_sheet_view_journey)).check(matches(isDisplayed()))
        onView(withId(R.id.bottom_sheet_view_journey)).perform(ViewActions.scrollTo())
        //onView(withId(R.id.button_expand)).perform(ViewActions.)
        onView(withId(R.id.setNav1)).check(matches(isDisplayed()))
        onView(withId(R.id.setNav2)).check(matches(isDisplayed()))
        onView(withId(R.id.setNav3)).check(matches(isDisplayed()))

    }

    @Test
    fun test_stop_clicked_images_displayed(){
        onView(withId(R.id.button_expand)).perform(ViewActions.click())
        onView(withId(R.id.imageView13)).check(matches(isDisplayed()))
        onView(withId(R.id.imageView14)).check(matches(isDisplayed()))
    }

    /*@Test
    fun test_set_navigation_clicked(){

        onView(withId(R.id.journey_activity)).check(matches(isDisplayed()))
    }*/

    fun viewExists(id: Int): Boolean {
        return try {
            onView(withId(id)).check(doesNotExist())
            true
        } catch (e: RuntimeException) {
            false
        }
    }
}