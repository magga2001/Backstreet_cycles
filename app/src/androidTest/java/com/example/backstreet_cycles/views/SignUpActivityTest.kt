package com.example.backstreet_cycles.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)


class SignUpActivityTest {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())


    @Before
    fun setUp() {
        if (firebaseAuth.currentUser != null){
            userRepository.logout()
        }

    }

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





}
