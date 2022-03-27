package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.activity.viewModels
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel

import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class JourneyHistoryActivityTest {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var logInViewModel: LogInViewModel by viewModels()

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"


    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)
    @Before
    fun setUp() {
        if (firebaseAuth.currentUser == null) {
            logInViewModel.login(email, password)
        }
        Application().onCreate()
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())
        onView(withId(R.id.journeyHistory)).perform(ViewActions.click())
        init()
    }

    @Test
    fun test_activity_is_in_view() {
        intending(hasComponent(JourneyHistoryActivity::class.qualifiedName))

    }

//    @Test
//    fun test_on_pressBack_go_to_HomePageActivity() {
//        Espresso.pressBack()
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//
//    }

    @Test
    fun test_title_is_visible() {

        onView(
            Matchers.allOf(
                withId(R.id.textView3),
                ViewMatchers.withParent(withId(R.id.journeyHistoryActivity))
            )
        ).check(matches(isDisplayed()))

    }

    @Test
    fun test_recycler_view_is_visible() {
        onView(
            Matchers.allOf(
                withId(R.id.journey_history_recycler_view),
                ViewMatchers.withParent(withId(R.id.journeyHistoryActivity))
            )
        ).check(matches(isDisplayed()))
    }

//    @Test
//    fun test_journey_summary_is_visible() {
//        onView(withId(R.id.journeySummaryTextView)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_card_is_visible() {
//        onView(withId(R.id.recentJourneyCardViewText)).check(matches(isDisplayed()))
//    }

    @After
    fun tearDown(){
        Intents.release()
    }
}