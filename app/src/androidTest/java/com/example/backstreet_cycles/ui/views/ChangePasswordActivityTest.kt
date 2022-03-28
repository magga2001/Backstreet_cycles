package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Rule
import java.com.example.backstreet_cycles.FakeCyclistRepoImpl
import java.com.example.backstreet_cycles.FakeMapboxRepoImpl
import java.com.example.backstreet_cycles.FakeTflRepoImpl
import java.com.example.backstreet_cycles.FakeUserRepoImpl

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class ChangePasswordActivityTest{

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
        hiltRule.inject()
        if (logInViewModel.getMutableLiveData().value == null){
            fakeUserRepoImpl.login(email, password)
        }
        Intents.init()
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())
        onView(withId(R.id.changePassword)).perform(ViewActions.click())
    }

    @Test
    fun test_activity_launched_user_email_displayed() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        onView(withId(R.id.change_password_email)).check(matches(ViewMatchers.withText(email)))
    }

    @Test
    fun test_current_password_field_is_displayed(){
        onView(withId(R.id.change_password_currentPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun test_new_password_field_is_displayed(){
        onView(withId(R.id.change_password_NewPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun test_save_button_is_displayed(){
        onView(withId(R.id.change_password_SaveButton)).check(matches(isDisplayed()))
    }

    @Test
    fun test_activity_launched_text_field_current_password() {
        onView(withId(R.id.change_password_currentPassword)).check(matches(isDisplayed()))
        val testInput = "password"
        onView(withId(R.id.change_password_currentPassword)).perform(typeText(testInput))
        onView(withId(R.id.change_password_currentPassword)).check(matches(ViewMatchers.withText(testInput)))
    }

    @Test
    fun test_activity_launched_text_field_new_password() {
        onView(withId(R.id.change_password_NewPassword)).check(matches(isDisplayed()))
        val testInput = "password"
        onView(withId(R.id.change_password_NewPassword)).perform(typeText(testInput))
        onView(withId(R.id.change_password_NewPassword)).check(matches(ViewMatchers.withText(testInput)))
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        Espresso.pressBack()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}