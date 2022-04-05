package com.example.backstreet_cycles.ui.views

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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class SignUpActivityTest {

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
    val activityRule = ActivityScenarioRule(SignUpActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(SignUpActivity::class.qualifiedName))
        Intents.release()
    }
    @Test
    fun test_titleText_is_visible() {
        onView(withId(R.id.sign_up_create_account_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_title_matches_required_text() {
        onView(withId(R.id.sign_up_create_account_title)).check(matches(isDisplayed())).check(matches(
            withText(R.string.sign_up_title)))
    }

    @Test
    fun test_et_first_name_is_visible() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_last_name_is_visible() {
        onView(withId(R.id.sign_up_edit_user_details_lastName)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_email_is_visible() {
        onView(withId(R.id.sign_up_change_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_password_is_visible() {
        onView(withId(R.id.sign_up_password)).check(matches(isDisplayed()))

    }

    @Test
    fun test_et_confirmPassword_is_visible() {
        onView(withId(R.id.sign_up_confirmPassword)).check(matches(isDisplayed()))

    }

    @Test
    fun test_buttonSignUp_is_visible() {
        onView(withId(R.id.buttonSignUp)).check(matches(isDisplayed()))
    }

    @Test
    fun test_firstname_is_empty() {
        onView(withId(R.id.buttonSignUp)).perform(click())
        onView(withId(R.id.sign_up_edit_user_details_firstName)).check(matches(hasErrorText("Please enter your first name")))
    }

    @Test
    fun last_name_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.buttonSignUp)).perform(click())
        onView(withId(R.id.sign_up_edit_user_details_lastName)).check(matches(hasErrorText("Please enter your last name")))
    }

    @Test
    fun email_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.buttonSignUp)).perform(click())
        onView(withId(R.id.sign_up_change_email)).check(matches(hasErrorText("Please enter your email")))
    }

    @Test
    fun password_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_change_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.buttonSignUp)).perform(click())
        onView(withId(R.id.sign_up_password)).check(matches(hasErrorText("Please enter a password")))
    }

    @Test
    fun password_confirmation_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_change_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.sign_up_password)).perform(typeText(password), closeSoftKeyboard())
        onView(withId(R.id.buttonSignUp)).perform(click())
        onView(withId(R.id.sign_up_confirmPassword)).check(matches(hasErrorText("Please confirm your password")))
    }

    @Test
    fun test_password_do_not_match(){
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(typeText("test"),closeSoftKeyboard())
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(typeText("user"),closeSoftKeyboard())
        onView(withId(R.id.sign_up_change_email)).perform(typeText("testuser@gmail.com"),closeSoftKeyboard())
        onView(withId(R.id.sign_up_password)).perform(typeText(password),closeSoftKeyboard())
        onView(withId(R.id.sign_up_confirmPassword)).perform(typeText("654321"), closeSoftKeyboard())
        onView(withId(R.id.buttonSignUp)).perform(click())
        onView(withId(R.id.sign_up_confirmPassword)).check(matches(hasErrorText("Passwords do not match")))
    }


    @Test
    fun details_entered_correctly() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_change_email)).perform(typeText("test@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_password)).perform(typeText("test12"), closeSoftKeyboard())
        onView(withId(R.id.sign_up_confirmPassword)).perform(typeText("test12"), closeSoftKeyboard())
        onView(withId(R.id.buttonSignUp)).perform(click())
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_backPressed_goes_back_to_LoginActivity(){
        pressBack()
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_trims_details(){
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(typeText("test    "), closeSoftKeyboard())
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(typeText("test   "), closeSoftKeyboard())
        onView(withId(R.id.sign_up_change_email)).perform(typeText("test@gmail.com   "), closeSoftKeyboard())
        onView(withId(R.id.sign_up_password)).perform(typeText("test12   "), closeSoftKeyboard())
        onView(withId(R.id.sign_up_confirmPassword)).perform(typeText("test12    "), closeSoftKeyboard())
        onView(withId(R.id.buttonSignUp)).perform(click())
    }

}
