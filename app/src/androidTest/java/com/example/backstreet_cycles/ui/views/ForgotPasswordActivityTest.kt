package com.example.backstreet_cycles.ui.views

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import java.com.example.backstreet_cycles.FakeCyclistRepoImpl
import java.com.example.backstreet_cycles.FakeMapboxRepoImpl
import java.com.example.backstreet_cycles.FakeTflRepoImpl
import java.com.example.backstreet_cycles.FakeUserRepoImpl
import android.app.Application
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class ForgotPasswordActivityTest{

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
    var activityRule: ActivityScenarioRule<ForgotPasswordActivity> =
        ActivityScenarioRule(ForgotPasswordActivity::class.java)

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
        intending(hasComponent(ForgotPasswordActivity::class.qualifiedName))
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
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
        intending(hasComponent(LogInActivity::class.qualifiedName))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}
