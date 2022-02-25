package com.example.backstreet_cycles.views
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)


class SignUpActivityTest {

    @Test
    fun test_activity_is_in_view() {
        val activityScenario = ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.signUpActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_title_is_visible() {
        val activityScenario = ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.et_create_account_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_first_name_is_visible() {
        val activityScenario = ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.et_firstName)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_last_name_is_visible() {
        val activityScenario = ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.et_lastName)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_email_is_visible() {
        val activityScenario = ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.et_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_password_is_visible() {
        val activityScenario = ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.et_password)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_confirmPassword_is_visible() {
        val activityScenario = ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.et_confirmPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun test_buttonSignUp_is_visible() {
        val activityScenario=ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.buttonSignUp)).check(matches(isDisplayed()))
    }

    @Test
    fun test_typing_TRASHTEST(){

        val activityScenario=ActivityScenario.launch(SignUpActivity::class.java)
        onView(withId(R.id.signUpActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.et_firstName)).perform(
            ViewActions.typeText("john"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_lastName)).perform(
            ViewActions.typeText("john"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_email)).perform(
            ViewActions.typeText("john@doebo.com"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_password)).perform(
            ViewActions.typeText("123456"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.et_confirmPassword)).perform(
            ViewActions.typeText("123456"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())


    }




}
