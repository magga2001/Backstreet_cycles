package com.example.backstreet_cycles.ui.views

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
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
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class LogInActivityTest{

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

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
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)


    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_logo_is_visible() {
        onView(withId(R.id.log_in_image_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_email_text_field_is_visible() {
        onView(withId(R.id.log_in_email)).check(matches(isDisplayed()))
    }
    @Test
    fun test_email_password_is_visible() {
        onView(withId(R.id.log_in_password)).check(matches(isDisplayed()))
    }

    @Test
    fun test_forgot_password_text_box_is_displayed() {
        onView(withId(R.id.log_in_clickForgotPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun test_button_login_is_visible() {
        onView(withId(R.id.log_in_button)).check(matches(isDisplayed()))
    }

    @Test
    fun test_button_createAccount_is_visible() {
        onView(withId(R.id.log_in_buttonCreateAccount)).check(matches(isDisplayed()))
    }

    @Test
    fun test_email_text_box_validate_input() {
        onView(withId(R.id.log_in_email)).perform(typeText(email)).check(matches(withText(email)))
    }

    @Test
    fun test_password_text_box_validate_input() {
        onView(withId(R.id.log_in_password)).perform(typeText(password)).check(matches(withText(password)))
    }

    @Test
    fun test_launch_sign_up_with_create_button() {
        onView(withId(R.id.log_in_buttonCreateAccount)).perform(click())
        Intents.init()
        intending(hasComponent(SignUpActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_homePageActivityLaunched_when_login_clicked(){
        onView(withId(R.id.log_in_email)).perform(typeText(email)).check(matches(withText(email)))
        onView(withId(R.id.log_in_password)).perform(typeText(password)).check(matches(withText(password)))
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun login_email_is_empty() {
        onView(withId(R.id.log_in_email)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.log_in_button)).perform(click())
        onView(withId(R.id.log_in_email)).check(matches(hasErrorText("Please enter your email")))
    }

    @Test
    fun login_password_is_empty() {
        onView(withId(R.id.log_in_email)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.log_in_password)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.log_in_button)).perform(click())
        onView(withId(R.id.log_in_password)).check(matches(hasErrorText("Please enter a password")))
    }

}