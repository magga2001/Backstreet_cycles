package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.ui.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.ui.ui.views.HomePageActivity
import com.example.backstreet_cycles.ui.ui.views.JourneyActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class JourneyActivityTest{

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var logInRegisterViewModel: LogInRegisterViewModel

    @Before
    fun setUp() {
        if (firebaseAuth.currentUser == null) {
            logInRegisterViewModel= LogInRegisterViewModel(Application())
            logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com","123456")
        }
        Application().onCreate()
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nextPageButton)).perform(click())
        ActivityScenario.launch(JourneyActivity::class.java)
        init()
    }

    @Test
    fun test_journey_activity_is_visible(){
        intending(hasComponent(JourneyActivity::class.qualifiedName))
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
    fun test_start_navigation_button_visible() {
        onView(withId(R.id.start_navigation)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

//    @Test
//    fun test_next_button_from_home_to_journey(){
//        pressBack()
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//    }

    @Test
    fun test_stops_recycling_view_displayed(){
//        onView(withId(R.id.plan_journey_recycling_view)).check(matches(isDisplayed()))
        onView(withId(R.id.plan_journey_recycling_view)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    /*@Test
       fun test_journey_overview_clicked(){
            onView(withId(R.id.journey_activity)).check(matches(isDisplayed()))
        }*/

//    @Test
//    fun test_start_navigation_clicked_goes_to_navigation_activity(){
//        onView(withId(R.id.start_navigation)).perform(click())
//        onView(withId(R.id.navigation_activity)).check(matches(isDisplayed()))
//    }
        /*@Test
    fun test_expand_button(){
        onView(withId(R.id.planJourney_button_expand)).check(matches(isDisplayed()))
    }*/

//    @Test
//    fun test_button_expand() {
//        onView(withId(R.id.bottom_sheet_view_journey)).check(matches(isDisplayed()))
//        onView(withId(R.id.plan_journey_recycling_view)).check(matches(isDisplayed()))
//        onView(withId(R.id.plan_journey_recycling_view)).perform(RecyclerViewActions.scrollToPosition<PlanJourneyAdapter.ViewHolder>(0))
//
//        onView(withId(R.id.plan_journey_recycling_view))
//            // scrollTo will fail the test if no item matches.
//            .perform(RecyclerViewActions.actionOnItemAtPosition<PlanJourneyAdapter.ViewHolder>(0, click()))
//        onView(withId(R.id.setNav1)).check(matches(isDisplayed()))
//        onView(withId(R.id.setNav2)).check(matches(isDisplayed()))
//        onView(withId(R.id.setNav3)).check(matches(isDisplayed()))
//
//
//
//        //
//        //        onView(withId(R.id.bottom_sheet_journey)).perform(ViewActions.swipeUp())
//        //        onView(withId(R.id.button_expand)).check(matches(isDisplayed()))
//    }


//    @Test
//    fun test_stop_clicked_set_navigation_displayed(){
//        onView(withId(R.id.bottom_sheet_view_journey)).check(matches(isDisplayed()))
//        onView(withId(R.id.bottom_sheet_view_journey)).perform(scrollTo())
//        //onView(withId(R.id.button_expand)).perform(ViewActions.)
//        onView(withId(R.id.setNav1)).check(matches(isDisplayed()))
//        onView(withId(R.id.setNav2)).check(matches(isDisplayed()))
//        onView(withId(R.id.setNav3)).check(matches(isDisplayed()))
//
//    }

//    @Test
//    fun test_stop_clicked_images_displayed(){
//        onView(withId(R.id.button_expand)).perform(click())
//        onView(withId(R.id.imageView13)).check(matches(isDisplayed()))
//        onView(withId(R.id.imageView14)).check(matches(isDisplayed()))
//    }

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

    /*@Test
    fun test_hire_button_visible(){
        //onView(withId(R.id.santander_link)).check(matches(isDisplayed()))
        onView(withId(R.id.santander_link)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }*/

    /*@Test
    fun test_hire_image_visible(){
        onView(withId(R.id.SantanderCycleImage)).check(matches(isDisplayed()))
    }*/

    /*@Test
    fun test_from_text(){
        onView(withId(R.id.planJourney_from)).check(matches(isDisplayed()))
    }

    @Test
    fun test_to_text(){
        onView(withId(R.id.planJourney_to)).check(matches(isDisplayed()))
    }*/

    @Test
    fun test_finish_journey_button_visible(){
//        onView(withId(R.id.bottom_sheet_view_journey)).perform(ViewActions.swipeUp())

        onView(withId(R.id.finish_journey)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

//    @Test
//    fun test_checkbox_visible(){
//        //onView(withId(R.id.checkBoxFinishJourney)).check(matches(isDisplayed()))
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
//
//    }

    @Test
    fun test_duration_text_field_displayed(){
//        onView(withId(R.id.bottom_sheet_view_journey)).perform(ViewActions.swipeUp())
//        onView(withId(R.id.durations)).check(matches(withText("Distance:")))
        onView(withId(R.id.durations)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }

    @Test
    fun test_distance_text_field_displayed(){
//        onView(withId(R.id.distances)).check(matches(withText("Distance:")))
        onView(withId(R.id.distances)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }

    @Test
    fun test_price_text_field_displayed(){
//        onView(withId(R.id.prices)).check(matches(withText("Price:")))
        onView(withId(R.id.prices)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
//        onView(withId(R.id.prices)).check(matches(isDisplayed()))
    }

    /*@Test
    fun test_duration_image_field_displayed(){
        onView(withId(R.id.duration_image)).check(matches(isDisplayed()))
    }

    @Test
    fun test_distance_image_field_displayed(){
        onView(withId(R.id.Distance_image)).check(matches(isDisplayed()))
    }

    @Test
    fun test_price_image_field_displayed(){
        onView(withId(R.id.price_image)).check(matches(isDisplayed()))
    }*/

//    @Test
//    fun test_check_not_checked(){
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(isNotChecked()))
//    }

//    @Test
//    fun test_finish_not_clickable(){
//        onView(withId(R.id.finish_journey)).check(matches(isNotClickable()))
//    }

//    @Test
//    fun test_not_checked_stop_clickable(){
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(isNotChecked()))
//        onView(withId(R.id.button_expand)).check(matches(isClickable()))
//    }

//    @Test
//    fun test_not_checked_finish_not_clickable(){
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(isNotChecked()))
//        onView(withId(R.id.finish_journey)).check(matches(isNotClickable()))
//    }

//    @Test
//    fun test_checked_stop_not_clickable(){
//        onView(withId(R.id.checkBoxFinishJourney)).perform(click())
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(isChecked()))
//        onView(withId(R.id.button_expand)).check(matches(isNotClickable()))
//    }

//    @Test
//    fun test_checked_finish_clickable(){
//        onView(withId(R.id.checkBoxFinishJourney)).perform(click())
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(isChecked()))
//        onView(withId(R.id.finish_journey)).check(matches(isClickable()));
//    }

    @After
    fun tearDown(){
        Intents.release()
    }
}