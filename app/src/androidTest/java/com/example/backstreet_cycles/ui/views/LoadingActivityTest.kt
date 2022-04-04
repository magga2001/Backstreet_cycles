package com.example.backstreet_cycles.ui.views


import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
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
import okhttp3.internal.wait
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class LoadingActivityTest {
    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"
    private val userRepoImpl = UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

//    @get:Rule
//    var activityRule: ActivityScenarioRule<LoadingActivity> =
//        ActivityScenarioRule(LoadingActivity::class.java)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

    }


    private fun addStop(name: String) {
        val addStopButton = onView(
            Matchers.allOf(
                withId(R.id.addingBtn), ViewMatchers.withText("Add Stop"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.homepage_bottom_sheet_constraintLayout),
                        childAtPosition(
                            withId(R.id.homepage_bottom_sheet_linearLayout),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        addStopButton.perform(ViewActions.click())

        onView(ViewMatchers.isRoot()).perform(waitFor(500))
        val location = onView(
            Matchers.allOf(
                withId(R.id.edittext_search),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.searchView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        onView(ViewMatchers.isRoot()).perform(waitFor(500))
        location.perform(ViewActions.replaceText(name))
        onView(ViewMatchers.isRoot()).perform(waitFor(500))
        location.perform(
            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER),
            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
        )

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int): Matcher<View> {

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

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    @Test
    fun test_loading_activity_is_in_view() {
        ActivityScenario.launch(HomePageActivity::class.java)
        addStop("Covent Garden")
        onView(withId(R.id.nextPageButton)).perform(click())
        onView(withText("Create New Journey")).perform(click())
        onView(withId(R.id.loadingActivity_GifImage)).check(matches(isDisplayed()))
    }
    @Test
    fun test_loading_activity_test_is_displayed() {
    onView(withId(R.id.loadingActivity_text)).check(matches(isDisplayed()))
    }

    @Test
    fun test_loading_gif_displayed() {
        onView(withId(R.id.loadingActivity_GifImage)).check(matches(isDisplayed()))
    }

    @Test
    fun test_loading_dots_gif_is_displayed() {
        onView(withId(R.id.loadingActivity_dotsLoadingGifImage)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}