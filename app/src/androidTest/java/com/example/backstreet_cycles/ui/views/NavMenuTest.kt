package com.example.backstreet_cycles.ui.views

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
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
class NavMenuTest{

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(HomePageActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        onView(withContentDescription(R.string.open)).perform(click())
    }

    @Test
    fun test_current_journey() {
        onView(withId(R.id.currentJourney)).perform(click())
        Intents.init()
        intending(hasComponent(JourneyActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_drawer_is_open(){
        onView(withId(R.id.homepage_nav_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_drawer_is_closed(){
        onView(withContentDescription(R.string.close)).perform(click())
        onView(withId(R.id.homepage_nav_view)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun test_EditProfile_in_NavMenu_is_visible() {
        onView(withId(R.id.profile)).check(matches(isDisplayed()))
    }
    @Test
    fun test_ChangePassword_in_NavMenu_is_visible() {
        onView(withId(R.id.changePassword)).check(matches(isDisplayed()))
    }
    @Test
    fun test_JourneyHistory_in_NavMenu_is_visible() {
        onView(withId(R.id.journeyHistory)).check(matches(isDisplayed()))
    }
    @Test
    fun test_About_in_NavMenu_is_visible() {
        onView(withId(R.id.about)).check(matches(isDisplayed()))
    }
    @Test
    fun test_FAQ_in_NavMenu_is_visible() {
        onView(withId(R.id.faq)).check(matches(isDisplayed()))
    }

    @Test
    fun test_logout_in_NavMenu_is_visible() {
//        ActivityScenario.launch(LogInActivity::class.java)
//        onView(withId(R.id.log_in_email)).perform(ViewActions.typeText(email)).check(matches(withText(email)))
//        onView(withId(R.id.log_in_password)).perform(ViewActions.typeText(password)).check(matches(withText(password)))
        onView(withId(R.id.logout)).check(matches(isDisplayed()))
    }

    @Test
    fun test_viewProfileButton_toEditProfileActivity() {
        onView(withId(R.id.profile)).perform(click())
        Intents.init()
        intending(hasComponent(EditUserProfileActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_changePassword_toChangePasswordActivity() {
        onView(withId(R.id.changePassword)).perform(click())
        Intents.init()
        intending(hasComponent(ChangePasswordActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_JourneyHistory_to_JourneyHistoryActivity(){
        onView(withId(R.id.journeyHistory)).perform(click())
        Intents.init()
        intending(hasComponent(JourneyActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_aboutButton_to_AboutActivity(){
        onView(withId(R.id.about)).perform(click())
        Intents.init()
        intending(hasComponent(AboutActivity::class.qualifiedName))
        Intents.release()
    }
    @Test
    fun test_FAQ_to_FaqActivity(){
        onView(withId(R.id.faq)).perform(click())
        Intents.init()
        intending(hasComponent(FAQActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_logout_to_loginActivity(){
        onView(withId(R.id.logout)).check(matches(isDisplayed()))
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_nav_showUserName_textField(){
        onView(withId(R.id.user_name)).check(matches(isDisplayed()))
    }

    @Test
    fun test_nav_showUserEmail_textField(){
        onView(withId(R.id.nav_header_textView_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_username_field_matches_current_user_first_name(){
        val firstName = "Hello, Test"
        onView(withId(R.id.user_name)).check(matches(withText(firstName)))
    }

    @Test
    fun test_email_field_matches_current_user_email(){
        onView(withId(R.id.nav_header_textView_email)).check(matches(withText(email)))
    }

}