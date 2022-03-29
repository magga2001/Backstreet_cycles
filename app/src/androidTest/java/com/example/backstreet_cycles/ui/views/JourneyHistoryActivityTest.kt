package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.com.example.backstreet_cycles.FakeCyclistRepoImpl
import java.com.example.backstreet_cycles.FakeMapboxRepoImpl
import java.com.example.backstreet_cycles.FakeTflRepoImpl
import java.com.example.backstreet_cycles.FakeUserRepoImpl

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class JourneyHistoryActivityTest {

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
        onView(withId(R.id.journeyHistory)).perform(ViewActions.click())
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
                withId(R.id.journey_history_title),
                ViewMatchers.withParent(withId(R.id.recentJourney_layout))
            )
        ).check(matches(isDisplayed()))

    }

    @Test
    fun test_recycler_view_is_visible() {
        onView(
            Matchers.allOf(
                withId(R.id.journey_history_recycler_view),
                ViewMatchers.withParent(withId(R.id.recentJourney_layout))
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