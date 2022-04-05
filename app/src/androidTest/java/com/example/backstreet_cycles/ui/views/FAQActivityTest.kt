package com.example.backstreet_cycles.ui.views

import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
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

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class FAQActivityTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @get:Rule
    val activityRule = ActivityScenarioRule(HomePageActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        onView(withContentDescription(R.string.open)).perform(ViewActions.click())
        onView(withId(R.id.faq)).perform(ViewActions.click())
    }

    @Test
    fun test_FAQActivity_is_displayed() {
        Intents.init()
        Intents.intending(IntentMatchers.hasComponent(FAQActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_FAQ_title_is_displayed() {
        onView(withId(R.id.FAQTitle)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun test_question1_is_displayed() {
        onView(withId(R.id.FAQ_q1)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun test_answer1_is_displayed() {
        onView(withId(R.id.FAQ_a1)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun test_question2_is_displayed() {
        onView(withId(R.id.FAQ_q2)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun test_answer2_is_displayed() {
        onView(withId(R.id.FAQ_a2)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun test_question3_is_displayed() {
        onView(withId(R.id.FAQ_q3)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun test_answer3_is_displayed() {
        onView(withId(R.id.FAQ_a3)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        pressBack()
        Intents.init()
        Intents.intending(IntentMatchers.hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
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
        Intents.intending(IntentMatchers.hasComponent(HomePageActivity::class.qualifiedName))
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
