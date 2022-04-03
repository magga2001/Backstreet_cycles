package com.example.backstreet_cycles.ui.views

import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
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
import kotlinx.coroutines.runBlocking
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
class ChangePasswordActivityTest {

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private val userRepoImpl =
        UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET
        )

    @Before
    fun setUp() {
        runBlocking { userRepoImpl.logOut() }
        runBlocking { userRepoImpl.login(email, password) }
        runBlocking { userRepoImpl.getUserDetails() }
//        userRepoImpl.getUserDetails()
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())
        onView(withId(R.id.changePassword)).perform(ViewActions.click())
    }

    @Test
    fun test_activity_launched_title_displayed() {
        onView(withId(R.id.change_password_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_activity_launched_user_email_displayed() {
        onView(withId(R.id.change_password_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_current_password_field_is_displayed() {
        onView(withId(R.id.change_password_currentPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun test_new_password_field_is_displayed() {
        onView(withId(R.id.change_password_NewPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun test_save_button_is_displayed() {
        onView(withId(R.id.change_password_SaveButton)).check(matches(isDisplayed()))
    }

    @Test
    fun test_activity_launched_text_field_current_password() {
        onView(withId(R.id.change_password_currentPassword)).check(matches(isDisplayed()))
        val testInput = "password"
        onView(withId(R.id.change_password_currentPassword)).perform(typeText(testInput))
        onView(withId(R.id.change_password_currentPassword)).check(
            matches(
                ViewMatchers.withText(
                    testInput
                )
            )
        )
    }

    @Test
    fun test_activity_launched_text_field_new_password() {
        onView(withId(R.id.change_password_NewPassword)).check(matches(isDisplayed()))
        val testInput = "password"
        onView(withId(R.id.change_password_NewPassword)).perform(typeText(testInput))
        onView(withId(R.id.change_password_NewPassword)).check(
            matches(
                ViewMatchers.withText(
                    testInput
                )
            )
        )
    }

//    //    Test will work later
//    @Test
//    fun test_snack_bar_appears_on_change_password() {
//        val testInput = "123456"
//        onView(withId(R.id.change_password_currentPassword)).perform(
//            typeText(testInput),
//            ViewActions.closeSoftKeyboard()
//        )
//        onView(withId(R.id.change_password_currentPassword)).check(
//            matches(
//                ViewMatchers.withText(
//                    testInput
//                )
//            )
//        )
//        val newPassInput = "newPassword"
//        onView(withId(R.id.change_password_NewPassword)).perform(
//            typeText(newPassInput),
//            ViewActions.closeSoftKeyboard()
//        )
//        onView(withId(R.id.change_password_NewPassword)).check(
//            matches(
//                ViewMatchers.withText(
//                    newPassInput
//                )
//            )
//        )
//        onView(withId(R.id.change_password_SaveButton)).perform(ViewActions.click())
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//            .check(matches(ViewMatchers.withText("Password successfully updated")))
//    }
//
//    //Test will work later
//    @Test
//    fun test_saves_new_password() {
//        val testInput = "123456"
//        onView(withId(R.id.change_password_currentPassword)).perform(
//            typeText(testInput),
//            ViewActions.closeSoftKeyboard()
//        )
//        onView(withId(R.id.change_password_currentPassword)).check(
//            matches(
//                ViewMatchers.withText(
//                    testInput
//                )
//            )
//        )
//        val newPassInput = "newPassword"
//        onView(withId(R.id.change_password_NewPassword)).perform(
//            typeText(newPassInput),
//            ViewActions.closeSoftKeyboard()
//        )
//        onView(withId(R.id.change_password_NewPassword)).check(
//            matches(
//                ViewMatchers.withText(
//                    newPassInput
//                )
//            )
//        )
//        onView(withId(R.id.change_password_SaveButton)).perform(ViewActions.click())
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//            .check(matches(ViewMatchers.withText("Password successfully updated")))
//        Intents.init()
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//        Intents.release()
//    }

    @Test
    fun test_old_password_not_entered() {
        val newPassInput = "newPassword"
        onView(withId(R.id.change_password_NewPassword)).perform(
            typeText(newPassInput),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.change_password_NewPassword)).check(
            matches(
                ViewMatchers.withText(
                    newPassInput
                )
            )
        )
        onView(withId(R.id.change_password_SaveButton)).perform(ViewActions.click())
        onView(withId(R.id.change_password_currentPassword)).check(
            matches(
                ViewMatchers.hasErrorText(
                    "In order for use to change your email or password you need to enter your old password"
                )
            )
        )
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        pressBack()
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_go_to_HomePageActivity_on_clicking_top_back_button() {

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
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        userRepoImpl.logOut()
    }
}