package com.example.backstreet_cycles.ui.views

import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class JourneyActivityTest {

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
    var activityRule = ActivityScenarioRule(LoadingActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        SharedPrefHelper.initialiseSharedPref(
            ApplicationProvider.getApplicationContext(),
            Constants.LOCATIONS
        )
        SharedPrefHelper.clearSharedPreferences()
    }

    @Test
    fun test_journey_activity_is_visible() {
        Intents.init()
        intending(hasComponent(JourneyActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_bottom_sheet_visible() {
        onView(withId(R.id.journey_bottom_sheet_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_journey_overview_button_visible() {
        onView(withId(R.id.overview_journey)).check(matches(isDisplayed()))
    }

    @Test
    fun test_start_navigation_button_visible() {
        onView(withId(R.id.start_navigation)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_stops_recycling_view_displayed() {
        onView(withId(R.id.plan_journey_recycling_view)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.VISIBLE
                )
            )
        )
    }

    @Test
    fun test_journey_overview_is_clickable() {
        onView(withId(R.id.overview_journey)).check(matches(isClickable()))
    }

    @Test
    fun test_expand_button_visible() {
        onView(
            Matchers.allOf(
                withId(R.id.planJourney_button_expand), withText(">"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.locationDataCardView_constraintLayout),
                        childAtPosition(
                            withId(R.id.locationDataCardView_linearLayout),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
    }

    @Test
    fun test_hire_button_visible() {
        onView(withId(R.id.santander_link)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_hire_image_visible() {
        onView(withId(R.id.SantanderCycleImage)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_checkbox_visible() {
        onView(
            Matchers.allOf(
                withId(R.id.checkBoxFinishJourney), withText("Done"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.locationDataCardView_constraintLayout),
                        childAtPosition(
                            withId(R.id.locationDataCardView_linearLayout),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
    }

    @Test
    fun test_duration_text_field_displayed() {
        //onView(withId(R.id.durations)).check(matches(withText("Duration:")))
        onView(withId(R.id.durations)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_distance_text_field_displayed() {
        onView(withId(R.id.distances)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_price_text_field_displayed() {
        onView(withId(R.id.prices)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_duration_image_field_displayed() {
        onView(withId(R.id.duration_image)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_distance_image_field_displayed() {
        onView(withId(R.id.DistanceImage)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_price_image_field_displayed() {
        onView(withId(R.id.price_image)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_finish_button_visible() {
        onView(withId(R.id.finish_journey)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun test_finish_not_clickable() {
        onView(withId(R.id.finish_journey)).check(matches(not(isEnabled())))
    }

    @Test
    fun test_from_text() {
        onView(
            Matchers.allOf(
                withId(R.id.planJourney_from), withText("From"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.locationDataCardView_constraintLayout),
                        childAtPosition(
                            withId(R.id.locationDataCardView_linearLayout),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
    }

    @Test
    fun test_to_text() {
        onView(
            Matchers.allOf(
                withId(R.id.planJourney_to), withText("To"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.locationDataCardView_constraintLayout),
                        childAtPosition(
                            withId(R.id.locationDataCardView_linearLayout),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
    }

    @Test
    fun test_set_navigation_displayed() {
        onView(
            Matchers.allOf(
                withId(R.id.setNav1), withText("set navigation"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.planJourney_constraintLayout1),
                        childAtPosition(
                            withId(R.id.planJourney_expandableLayout),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

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

}