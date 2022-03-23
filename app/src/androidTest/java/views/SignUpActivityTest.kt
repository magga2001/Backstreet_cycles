package views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.UserRepository
import com.example.backstreet_cycles.views.LogInActivity
import com.example.backstreet_cycles.views.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep


@RunWith(AndroidJUnit4ClassRunner::class)


class SignUpActivityTest {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())

    private val firstName = "Test"
    private val lastName = "User"
    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"



    @Before
    fun setUp() {
        if (firebaseAuth.currentUser != null){
            userRepository.logout()
        }
        ActivityScenario.launch(SignUpActivity::class.java)
        Intents.init()
    }

    @Test
    fun test_activity_is_in_view() {

        intending(hasComponent(SignUpActivity::class.qualifiedName))
    }

    @Test
    fun test_title_is_visible() {
        onView(withId(R.id.et_create_account_title)).check(matches(isDisplayed())).check(matches(
            withText(R.string.sign_up_title)))
    }

    @Test
    fun test_et_first_name_is_visible() {
        onView(withId(R.id.et_firstName)).perform(typeText(firstName)).check(matches(withText(firstName)))
    }

    @Test
    fun test_et_last_name_is_visible() {
        onView(withId(R.id.et_lastName)).perform(typeText(lastName)).check(matches(withText(lastName)))
    }

    @Test
    fun test_et_email_is_visible() {
        onView(withId(R.id.et_email)).perform(typeText(email)).check(matches(withText(email)))
    }

    @Test
    fun test_et_password_is_visible() {
        onView(withId(R.id.et_password)).perform(typeText(password)).check(matches(withText(password)))
    }

    @Test
    fun test_et_confirmPassword_is_visible() {
        onView(withId(R.id.et_confirmPassword)).perform(typeText(password)).check(matches(withText(password)))
    }

    @Test
    fun test_buttonSignUp_is_visible() {
        onView(withId(R.id.buttonSignUp)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}
