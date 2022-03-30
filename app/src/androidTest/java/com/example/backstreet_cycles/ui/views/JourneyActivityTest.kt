package com.example.backstreet_cycles.ui.views

import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep


@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class JourneyActivityTest{

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private val userRepoImpl = UserRepositoryImpl()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule: ActivityScenarioRule<HomePageActivity> =
        ActivityScenarioRule(HomePageActivity::class.java)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        userRepoImpl.logOut()
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(LogInActivity::class.java)
        ActivityScenario.launch(HomePageActivity::class.java)
        add_stop("covent gardens")
        onView(withId(R.id.nextPageButton)).perform(click())
        ActivityScenario.launch(JourneyActivity::class.java)
    }

    @Test
    fun test_journey_activity_is_visible(){
        intending(hasComponent(JourneyActivity::class.qualifiedName))
    }

    @Test
    fun test_bottom_sheet_visible(){
        onView(withId(R.id.journey_bottom_sheet_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_journey_overview_button_visible(){
        onView(withId(R.id.overview_journey)).check(matches(isDisplayed()))
    }

    @Test
    fun test_start_navigation_button_visible() {
        onView(withId(R.id.start_navigation)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_next_button_from_home_to_journey(){
        pressBack()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
    }

    @Test
    fun test_stops_recycling_view_displayed(){
        onView(withId(R.id.plan_journey_recycling_view)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_journey_overview_is_clickable(){
        onView(withId(R.id.overview_journey)).check(matches(isClickable()))

    }

//    @Test
//    fun test_journey_overview_is_clicked(){
//        onView(withId(R.id.overview_journey)).perform(click())
//        intending(hasComponent(JourneyActivity::class.qualifiedName))
//    }

//    @Test
//    fun test_start_navigation_clicked_goes_to_navigation_activity(){
//        onView(withId(R.id.start_navigation)).perform(click())
//        onView(withId(R.id.navigation_layout)).check(matches(isDisplayed()))
//    }

//    @Test
//    fun test_expand_button(){
//        onView(withId(R.id.planJourney_button_expand)).check(matches(isDisplayed()))
//    }

//    @Test
//    fun test_button_expand() {
//        onView(withId(R.id.journey_bottom_sheet_view)).check(matches(isDisplayed()))
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
    //        onView(withId(R.id.bottom_sheet_journey)).perform(ViewActions.swipeUp())
    //        onView(withId(R.id.button_expand)).check(matches(isDisplayed()))
    //}


//    @Test
//    fun test_stop_clicked_set_navigation_displayed(){
//        onView(withId(R.id.bottom_sheet_journey)).check(matches(isDisplayed()))
//        onView(withId(R.id.bottom_sheet_journey)).perform(scrollTo())
//        //onView(withId(R.id.button_expand)).perform(ViewActions.)
//        onView(withId(R.id.setNav1)).check(matches(isDisplayed()))
//        onView(withId(R.id.setNav2)).check(matches(isDisplayed()))
//        onView(withId(R.id.setNav3)).check(matches(isDisplayed()))
//
//    }
//
//    @Test
//    fun test_stop_clicked_images_displayed(){
//        onView(withId(R.id.planJourney_button_expand)).perform(click())
//        onView(withId(R.id.imageView13)).check(matches(isDisplayed()))
//        onView(withId(R.id.imageView14)).check(matches(isDisplayed()))
    // }

    @Test
    fun test_set_navigation_displayed(){

        onView(withId(R.id.journeyActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_hire_button_visible(){
        onView(withId(R.id.santander_link)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_hire_image_visible(){
        onView(withId(R.id.SantanderCycleImage)).check(matches(isDisplayed()))
    }

//    @Test
//    fun test_from_text(){
//        onView(withId(R.id.planJourney_from)).check(matches(isDisplayed()))
//    }

//    @Test
//    fun test_to_text(){
//        onView(withId(R.id.planJourney_to)).check(matches(isDisplayed()))
//    }

    @Test
    fun test_finish_journey_button_visible(){
//        onView(withId(R.id.bottom_sheet_view_journey)).perform(ViewActions.swipeUp())

        onView(withId(R.id.finish_journey)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_checkbox_visible(){
        //onView(withId(R.id.checkBoxFinishJourney)).check(matches(isDisplayed()))
        onView(withId(R.id.checkBoxFinishJourney)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }

//    @Test
//    fun test_duration_text_field_displayed(){
////        onView(withId(R.id.bottom_sheet_view_journey)).perform(ViewActions.swipeUp())
////        onView(withId(R.id.durations)).check(matches(withText("Distance:")))
//        onView(withId(R.id.durations)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
//
//    }

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

    @Test
    fun test_duration_image_field_displayed(){
        onView(withId(R.id.duration_image)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_distance_image_field_displayed(){
        onView(withId(R.id.DistanceImage)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_price_image_field_displayed(){
        onView(withId(R.id.price_image)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

//    @Test
//    fun test_check_not_checked(){
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(isNotChecked()))
//    }

//    @Test
//    fun test_journey_nav_not_clickable_if_not_set(){
//
//    }

    @Test
    fun test_finish_button_visible(){
        onView(withId(R.id.finish_journey)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_finish_not_clickable(){
        onView(withId(R.id.finish_journey)).check(matches(not(isEnabled())));
    }

//    @Test
//    fun test_not_checked_stop_clickable(){
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(isNotChecked()))
//        onView(withId(R.id.planJourney_button_expand)).check(matches(isClickable()))
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
//        onView(withId(R.id.planJourney_button_expand)).check(matches(isNotClickable()))
//    }

//    @Test
//    fun test_checked_finish_clickable(){
//        onView(withId(R.id.checkBoxFinishJourney)).perform(click())
//        onView(withId(R.id.checkBoxFinishJourney)).check(matches(isChecked()))
//        onView(withId(R.id.finish_journey)).check(matches(isClickable()));
//    }

    fun add_stop(name: String){

        val addStopButton = onView(
            Matchers.allOf(
                withId(R.id.addingBtn), withText("Add Stop"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.homepage_bottom_sheet_constraintLayout),
                        childAtPosition(
                            withId(R.id.homepage_bottom_sheet_linearLayout),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        addStopButton.perform(click())

        val location = onView(
            Matchers.allOf(
                withId(R.id.edittext_search),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.searchView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
//        location.perform(
//            ViewActions.replaceText(name),
//            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER),
//            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
//        )
        location.perform(ViewActions.replaceText(name))
        sleep(2500)
        location.perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER),
            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}

