package com.example.backstreet_cycles.ui.views

import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class JourneyHistoryActivityTest {

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private val userRepoImpl = UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        userRepoImpl.logOut()
        userRepoImpl.login(email, password)
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())
        onView(withId(R.id.journeyHistory)).perform(ViewActions.click())
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(JourneyHistoryActivity::class.qualifiedName))
        Intents.release()
    }

//    @Test
//    fun test_title_is_visible() {
//        onView(
//            Matchers.allOf(
//                withId(R.id.journey_history_title),
//                ViewMatchers.withParent(withId(R.id.recentJourney_layout))
//            )
//        ).check(matches(isDisplayed()))
//    }

    @Test
    fun test_recycler_view_is_visible() {
        onView(
            Matchers.allOf(
                withId(R.id.journey_history_recycler_view),
                ViewMatchers.withParent(withId(R.id.recentJourney_layout))
            )
        ).check(matches(isDisplayed()))
    }

    // Look into why this is not working
//    @Test
//    fun test_journey_summary_is_visible() {
//        onView(
//            Matchers.allOf(
//                withId(R.id.recentJourneyCard_locationDataCardView),
//                withParent(withId(R.id.JourneySummaryLayout1))
//            )).check(matches(isDisplayed()))
//    }

//    @Test
//    fun test_card_is_visible() {
//        onView(withId(R.id.recentJourneyCard_locationDataCardView)).check(matches(isDisplayed()))
//    }

//    @Test
//    fun test_recentJourneys_are_visible() {
//        onView(
//            Matchers.allOf(
//                withId(R.id.recentJourneyCardTextView),
//                withParent(withId(R.id.JourneySummaryLayout1))
//            )).check(matches(isDisplayed()))
//    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        pressBack()
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_go_to_HomePageActivity_on_clicking_top_back_button(){

        onView(
            Matchers.allOf(
                ViewMatchers.withContentDescription("Navigate up"),
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
        intending(hasComponent(HomePageActivity::class.qualifiedName))
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


    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        userRepoImpl.logOut()
    }
}