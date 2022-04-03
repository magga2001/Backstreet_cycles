package com.example.backstreet_cycles.ui.views

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class ForgotPasswordActivityTest{


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
        userRepoImpl.logOut()
        userRepoImpl.login(email, password)
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(LogInActivity::class.java)
        onView(withId(R.id.log_in_clickForgotPassword)).perform(click())
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(ForgotPasswordActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_title_is_visible() {
        onView(withId(R.id.forgot_password_title)).check(matches(isDisplayed()))
    }

//    @Test
//    fun test_email_text_field_is_visible() {
//        onView(withId(R.id.forgot_password_email)).check(matches(isDisplayed()))
//    }

    @Test
    fun test_forgotButton_is_visible() {
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).check(matches(isDisplayed()))
    }

    //Functionality issue, error comes after clicking twice
//    @Test
//    fun test_no_email_entered(){
//        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(ViewActions.click())
//        onView(withId(R.id.forgot_password_email)).check(matches(hasErrorText("Please enter your email")))
//    }

    //Functionality issue
//    @Test
//    fun test_forgotButton_clicked_to_LoginPage() {
//        onView(withId(R.id.forgot_password_email)).perform(ViewActions.typeText("test12@gmail.com"),
//            ViewActions.closeSoftKeyboard()
//        )
//        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
//        Intents.init()
//        intending(hasComponent(LogInActivity::class.qualifiedName))
//        Intents.release()
//    }

    @Test
    fun test_back_pressed_takes_back_to_log_in_page(){
        pressBack()
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
        Intents.release()
    }

    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        userRepoImpl.logOut()
    }
}
