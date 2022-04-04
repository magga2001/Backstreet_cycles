package com.example.backstreet_cycles.ui.views

import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.mapbox.maps.extension.style.expressions.dsl.generated.match
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class JourneyHistoryActivityTest {

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
            android.Manifest.permission.INTERNET)

    @get:Rule
    val activityRule = ActivityScenarioRule(JourneyHistoryActivity::class.java)


    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(JourneyHistoryActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_title_is_visible() {

        onView(withId(R.id.journey_history_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_recycler_view_is_visible() {
        onView(
            Matchers.allOf(
                withId(R.id.journey_history_recycler_view),
                withParent(withId(R.id.recentJourney_layout))
            )
        ).check(matches(isDisplayed()))
    }


    @Test
    fun test_journey_summary_card_is_visible(){
        onView(
            Matchers.allOf(
                withId(R.id.journey_history_recycler_view),
                childAtPosition(
                    withId(R.id.recentJourney_layout),
                    0
                )
            )
        ).check(matches(isDisplayed()))

    }

    @Test
    fun test_card_is_visible() {
        onView(withId(R.id.recentJourneyCard_locationDataCardView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_recentJourneys_are_visible() {
        onView(
            Matchers.allOf(
                withId(R.id.recentJourneyCardTextView),
                withParent(withId(R.id.JourneySummaryLayout1))
            )).check(matches(isDisplayed()))
    }

    @Test
    fun test_go_to_HomePageActivity_on_clicking_top_back_button(){

        onView(
            Matchers.allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        ).perform(ViewActions.click())

        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
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