package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
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
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.com.example.backstreet_cycles.*


@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class LogInActivityTest{

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private val fakeUserRepoImpl = FakeUserRepoImpl()
    private val fakeTflRepoImpl = FakeTflRepoImpl()
    private val fakeMapboxRepoImpl = FakeMapboxRepoImpl()
    private val fakeCyclistRepoImpl = FakeCyclistRepoImpl()
    private val context = Application()

    private val logInViewModel = LogInViewModel(fakeTflRepoImpl,fakeMapboxRepoImpl,fakeCyclistRepoImpl,fakeUserRepoImpl,context)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule: ActivityScenarioRule<LogInActivity> =
        ActivityScenarioRule(LogInActivity::class.java)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        hiltRule.inject()
        if (logInViewModel.getMutableLiveData().value != null){
            fakeUserRepoImpl.logout()
        }
        Intents.init()
    }

    @Test
    fun test_activity_is_in_view() {
        intending(hasComponent(LogInActivity::class.qualifiedName))
    }

//    @Test
//    fun test_logo_is_visible() {
//        onView(withId(R.id.log_in_image_view)).check(matches(isDisplayed()))
//    }

    @Test
    fun test_buttonCreateAccount_is_visible() {
        onView(withId(R.id.log_in_buttonCreateAccount)).check(matches(isDisplayed()))
    }

    @Test
    fun test_buttonLogin_is_visible() {
        onView(withId(R.id.log_in_button)).check(matches(isDisplayed()))
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
        intending(hasComponent(SignUpActivity::class.qualifiedName))

    }

    @Test
    fun test_backPress_toLogInActivity() {
        onView(withId(R.id.log_in_buttonCreateAccount)).perform(click())
        intending(hasComponent(SignUpActivity::class.qualifiedName))
        pressBack()
        intending(hasComponent(LogInActivity::class.qualifiedName))
    }

    @Test
    fun test_homePageActivityLaunched_when_login_clicked(){
        onView(withId(R.id.log_in_email)).perform(typeText(email)).check(matches(withText(email)))
        onView(withId(R.id.log_in_password)).perform(typeText(password)).check(matches(withText(password)))
        intending(hasComponent(HomePageActivity::class.qualifiedName))
    }

    @Test
    fun login_email_is_empty() {
        onView(withId(R.id.log_in_email)).perform(ViewActions.typeText(""), closeSoftKeyboard())
        onView(withId(R.id.log_in_button)).perform(ViewActions.click())
        onView(withId(R.id.log_in_email)).check(matches(hasErrorText("Please enter your email")))
    }

    @Test
    fun login_password_is_empty() {
        onView(withId(R.id.log_in_email)).perform(ViewActions.typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.log_in_password)).perform(ViewActions.typeText(""), closeSoftKeyboard())
        onView(withId(R.id.log_in_button)).perform(click())
        onView(withId(R.id.log_in_password)).check(matches(hasErrorText("Please enter a password")))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}