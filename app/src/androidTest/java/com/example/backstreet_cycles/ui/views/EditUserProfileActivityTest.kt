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
class EditUserProfileActivityTest {

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
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET
        )

    @Before
    fun setUp() {
        userRepoImpl.logOut()
        userRepoImpl.login(email, password)
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withContentDescription(R.string.open)).perform(ViewActions.click())
        onView(withId(R.id.profile)).perform(ViewActions.click())
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(EditUserProfileActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_title_is_visible() {
        onView(withId(R.id.edit_user_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_first_name_field_is_visible() {
        onView(withId(R.id.edit_user_details_firstName)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_last_name_field_is_visible() {
        onView(withId(R.id.edit_user_details_lastName)).check(matches(isDisplayed()))
    }

    @Test
    fun test_buttonUpdateProfile_is_visible() {
        onView(withId(R.id.edit_user_details_SaveButton)).check(matches(isDisplayed()))
    }

    @Test
    fun test_check_if_first_name_is_inputted() {
        onView(withId(R.id.edit_user_details_firstName)).check(matches(isDisplayed()))
        val testInput = "Test"
        onView(withId(R.id.edit_user_details_firstName)).perform(ViewActions.replaceText(testInput))
        onView(withId(R.id.edit_user_details_firstName)).check(matches(withText(testInput)))
    }

    @Test
    fun test_check_if_last_name_is_inputted() {
        onView(withId(R.id.edit_user_details_lastName)).check(matches(isDisplayed()))
        val testInput = "User"
        onView(withId(R.id.edit_user_details_lastName)).perform(ViewActions.replaceText(testInput))
        onView(withId(R.id.edit_user_details_lastName)).check(matches(withText(testInput)))

    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        pressBack()
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
    }


    @Test
    fun test_on_clickUpdateProfile_Empty_textboxes() {
        onView(withId(R.id.edit_user_details_firstName)).perform(
            ViewActions.replaceText(""),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.edit_user_details_SaveButton)).perform(ViewActions.click())
        onView(withId(R.id.edit_user_details_firstName)).check(matches(hasErrorText("Please enter your first name")))
    }

    @Test
    fun test_on_clickUpdateProfile_Empty_firstName() {
        onView(withId(R.id.edit_user_details_lastName)).perform(
            ViewActions.replaceText("Test"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.edit_user_details_firstName)).perform(
            ViewActions.replaceText(""),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.edit_user_details_SaveButton)).perform(ViewActions.click())
        onView(withId(R.id.edit_user_details_firstName)).check(matches(hasErrorText("Please enter your first name")))
    }

    @Test
    fun test_on_clickUpdateProfile_Empty_lastName() {
        onView(withId(R.id.edit_user_details_firstName)).perform(
            ViewActions.replaceText("Test"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.edit_user_details_lastName)).perform(
            ViewActions.replaceText(""),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.edit_user_details_SaveButton)).perform(ViewActions.click())
        onView(withId(R.id.edit_user_details_lastName)).check(matches(hasErrorText("Please enter your last name")))
    }

    @Test
    fun test_trims_passwords(){
        onView(withId(R.id.edit_user_details_firstName)).perform(ViewActions.replaceText(" John   "))
        onView(withId(R.id.edit_user_details_lastName)).perform(ViewActions.replaceText("   Doe "))
        onView(withId(R.id.edit_user_details_SaveButton)).perform(ViewActions.click())
    }

    @Test
    fun test_on_clickUpdateProfile_save() {
        onView(withId(R.id.edit_user_details_firstName)).perform(ViewActions.replaceText("Name"))
        onView(withId(R.id.edit_user_details_lastName)).perform(ViewActions.replaceText("Surname"))
        onView(withId(R.id.edit_user_details_SaveButton)).perform(ViewActions.click())
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
    }

    // Test can work later
//    @Test
//    fun test_snack_bar_shown_after_saved() {
//        onView(withId(R.id.edit_user_details_firstName)).perform(ViewActions.replaceText("Name"))
//        onView(withId(R.id.edit_user_details_lastName)).perform(ViewActions.replaceText("Surname"))
//        onView(withId(R.id.edit_user_details_SaveButton)).perform(ViewActions.click())
//        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("profile updated successfully")))
//    }

    @Test
    fun test_go_to_HomePageActivity_on_clicking_top_back_button() {

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