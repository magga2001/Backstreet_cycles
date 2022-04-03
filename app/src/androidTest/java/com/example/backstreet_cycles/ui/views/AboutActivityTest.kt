package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.matcher.UriMatchers.hasHost
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class AboutActivityTest {

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private val userRepoImpl = UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        userRepoImpl.logOut()
        userRepoImpl.login(email, password)
        hiltRule.inject()
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.about)).perform(click())
    }

    @Test
    fun test_about_button_to_AboutActivity() {
        onView(withId(R.id.aboutActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_title_is_displayed() {
        onView(withId(R.id.aboutTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun test_description_is_displayed() {
        onView(withId(R.id.aboutDescription)).check(matches(isDisplayed()))
    }

    @Test
    fun test_logo_is_displayed() {
        onView(withId(R.id.about_image)).check(matches(isDisplayed()))
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        pressBack()
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_on_go_to_HomePageActivity_on_top_back_button(){

        onView(
            Matchers.allOf(
                withContentDescription("Navigate up"),
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
        ).perform(click())


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

    @Test
    fun test_open_browser(){
        onView(withId(R.id.about_image)).perform(click())
        sleep(1500)
        intended(allOf(hasData(hasHost(equalTo("www.google.com"))),
            hasAction(Intent.ACTION_VIEW),
            toPackage("com.android.chrome")))
    }

    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        userRepoImpl.logOut()
    }
}
