package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
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
class SignUpActivityTest {

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private val userRepoImpl = UserRepositoryImpl()

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
        if(userRepoImpl.getFirebaseAuthUser() != null){
            userRepoImpl.logout()
        }
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(SignUpActivity::class.java)
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(SignUpActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_title_is_visible() {
        onView(withId(R.id.sign_up_create_account_title)).check(matches(isDisplayed())).check(matches(
            withText(R.string.sign_up_title)))
    }

    @Test
    fun test_et_first_name_is_visible() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).check(matches(isDisplayed()))
//        onView(
//            Matchers.allOf(
//                withId(R.id.sign_up_edit_user_details_firstName),
//                withParent(withId(R.id.sign_up_LinearLayout))
//            )
//        ).perform(typeText("firstName")).check(matches(withText("firstName")))
    }

    @Test
    fun test_et_last_name_is_visible() {
        onView(withId(R.id.sign_up_edit_user_details_lastName)).check(matches(isDisplayed()))
//        onView(
//            Matchers.allOf(
//                withId(R.id.sign_up_edit_user_details_lastName),
//                withParent(withId(R.id.sign_up_LinearLayout))
//            )
//        ).perform(typeText("lastName"), closeSoftKeyboard()).check(matches(withText("lastName")))
    }

    @Test
    fun test_et_email_is_visible() {
        //onView(withId(R.id.et_email)).perform(typeText(email)).check(matches(withText(email)))
        onView(
            Matchers.allOf(
                withId(R.id.sign_up_change_email),
                withParent(withId(R.id.sign_up_LinearLayout))
            )
        ).perform(typeText(email)).check(matches(withText(email)))
    }

    @Test
    fun test_et_password_is_visible() {
        //onView(withId(R.id.et_password)).perform(typeText(password)).check(matches(withText(password)))
        onView(
            Matchers.allOf(
                withId(R.id.sign_up_password),
                withParent(withId(R.id.sign_up_LinearLayout))
            )
        ).perform(typeText(password)).check(matches(withText(password)))
    }

    @Test
    fun test_et_confirmPassword_is_visible() {
        //onView(withId(R.id.et_confirmPassword)).perform(typeText(password)).check(matches(withText(password)))
        onView(
            Matchers.allOf(
                withId(R.id.sign_up_confirmPassword),
                withParent(withId(R.id.sign_up_LinearLayout))
            )
        ).perform(typeText(password)).check(matches(withText(password)))
    }

    @Test
    fun test_buttonSignUp_is_visible() {
        onView(withId(R.id.buttonSignUp)).check(matches(isDisplayed()))
    }

    @Test
    fun name_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.sign_up_edit_user_details_firstName)).check(matches(hasErrorText("Please enter your first name")))
    }

    @Test
    fun last_name_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.sign_up_edit_user_details_lastName)).check(matches(hasErrorText("Please enter your last name")))
    }

    @Test
    fun email_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_change_email)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.sign_up_change_email)).check(matches(hasErrorText("Please enter your email")))
    }

    @Test
    fun password_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_change_email)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_password)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.sign_up_password)).check(matches(hasErrorText("Please enter a password")))
    }

    @Test
    fun password_confirmation_is_empty() {
        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_change_email)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_password)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.sign_up_confirmPassword)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.sign_up_confirmPassword)).check(matches(hasErrorText("Please confirm your password")))
    }

//    @Test
//    fun details_entered_correctly() {
//        onView(withId(R.id.sign_up_edit_user_details_firstName)).perform(ViewActions.replaceText("test"))
//        onView(withId(R.id.sign_up_edit_user_details_lastName)).perform(ViewActions.replaceText("test"))
//        onView(withId(R.id.sign_up_change_email)).perform(ViewActions.replaceText("test@gmail.com"))
//        onView(withId(R.id.sign_up_password)).perform(ViewActions.replaceText("test12"))
//        onView(withId(R.id.sign_up_confirmPassword)).perform(ViewActions.replaceText("test12"))
//        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
//        intending(hasComponent(LogInActivity::class.qualifiedName))
//    }

    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}
