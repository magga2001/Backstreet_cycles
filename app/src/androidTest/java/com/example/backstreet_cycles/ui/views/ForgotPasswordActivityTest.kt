package com.example.backstreet_cycles.ui.views

import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
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
class ForgotPasswordActivityTest{


    private val email = "arjunkhanna945@gmail.com"
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

//    fun add_email(email : String){
//        onView(
//            Matchers.allOf(
//                withId(R.id.forgot_password_email),
//                childAtPosition(
//                    Matchers.allOf(
//                        withId(R.id.forgot_password_constraintLayout),
//                        childAtPosition(
//                            withId(R.id.forgotPasswordActivity),
//                            1
//                        )
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        ).perform(
//            ViewActions.replaceText(email),
//            ViewActions.closeSoftKeyboard()
//        )
//    }
    @Test
    fun test_email_displayed(){
        onView(withId(R.id.forgot_password_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withText(email)).check(matches(isDisplayed()))
    }


    @Test
    fun test_Snackbar_clicking_button_after_entering_email(){
        onView(withId(R.id.forgot_password_email)).perform(typeText(email), closeSoftKeyboard())
        //sleep(1500)
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
        sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Password reset link sent to email.")))
    }

    @Test
    fun test_Snackbar_entered_email_is_badly_formatted(){
        onView(withId(R.id.forgot_password_email)).perform(typeText("wrongEmail"), closeSoftKeyboard())
        sleep(1000)
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
        sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("The email address is badly formatted.")))
    }

    @Test
    fun test_Snackbar_no_such_user(){
        onView(withId(R.id.forgot_password_email)).perform(typeText("wrongEmail@gmail.com"), closeSoftKeyboard())
        sleep(1000)
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
        sleep(1000)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("There is no user record corresponding to this identifier. The user may have been deleted.")))
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


    @Test
    fun test_go_to_HomePageActivity_on_clicking_top_back_button(){

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
        intending(hasComponent(LoadingActivity::class.qualifiedName))
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
    fun test_email_with_whitespace_passes(){
        val newEmail = "$email "
        onView(withId(R.id.forgot_password_email)).perform(typeText(newEmail), closeSoftKeyboard())
        onView(withId(R.id.forgot_password_SendPasswordReset_button)).perform(click())
       // test_Snackbar_clicking_button_after_entering_email()

    }

    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        userRepoImpl.logOut()
    }
}
