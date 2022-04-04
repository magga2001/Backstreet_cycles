package com.example.backstreet_cycles.ui.views

import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
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
class ForgotPasswordActivityTest{

    private val email = "backstreet.cycles.test.user@gmail.com"

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
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        onView(withId(R.id.log_in_clickForgotPassword)).perform(click())
    }

    @Test
    fun test_email_displayed(){
        onView(withId(R.id.forgot_password_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withText(email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_email_is_empty() {
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
        onView(withId(R.id.forgot_password_email)).check(matches(hasErrorText("Please enter your email")))
    }

    @Test
    fun test_Snackbar_no_such_user(){
        onView(withId(R.id.forgot_password_email)).perform(typeText("wrongEmail@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("There is no user record corresponding to this identifier. The user may have been deleted.")))
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(ForgotPasswordActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_title_is_visible() {
        onView(withId(R.id.forgot_password_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_email_text_field_is_visible() {
        onView(withId(R.id.forgot_password_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_forgotButton_is_visible() {
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).check(matches(isDisplayed()))
    }

    @Test
    fun test_forgotButton_clicked_to_LoginPage() {
        onView(withId(R.id.forgot_password_email)).perform(typeText("test12@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_back_pressed_takes_back_to_log_in_page(){
        pressBack()
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
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
        ).perform(click())

        Intents.init()
        intending(hasComponent(LoadingActivity::class.qualifiedName))
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

    @Test
    fun test_email_with_whitespace_passes(){
        val newEmail = "$email "
        onView(withId(R.id.forgot_password_email)).perform(typeText(newEmail), closeSoftKeyboard())
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
    }

}
