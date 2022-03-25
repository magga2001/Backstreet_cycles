package views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.UserRepository
import com.example.backstreet_cycles.views.LogInActivity
import com.example.backstreet_cycles.views.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)


class SignUpActivityTest {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())

    private val firstName = "Test"
    private val lastName = "User"
    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

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
        //onView(withId(R.id.et_firstName)).perform(typeText(firstName)).check(matches(withText(firstName)))
        onView(
            Matchers.allOf(
                withId(R.id.et_firstName),
                withParent(withId(R.id.signUp_linear_layout))
            )
        ).perform(typeText(firstName)).check(matches(withText(firstName)))
    }

    @Test
    fun test_et_last_name_is_visible() {
        //onView(withId(R.id.et_lastName)).perform(typeText(lastName)).check(matches(withText(lastName)))
        onView(
            Matchers.allOf(
                withId(R.id.et_lastName),
                withParent(withId(R.id.signUp_linear_layout))
            )
        ).perform(typeText(lastName)).check(matches(withText(lastName)))
    }

    @Test
    fun test_et_email_is_visible() {
        //onView(withId(R.id.et_email)).perform(typeText(email)).check(matches(withText(email)))
        onView(
            Matchers.allOf(
                withId(R.id.et_email),
                withParent(withId(R.id.signUp_linear_layout))
            )
        ).perform(typeText(email)).check(matches(withText(email)))
    }

    @Test
    fun test_et_password_is_visible() {
        //onView(withId(R.id.et_password)).perform(typeText(password)).check(matches(withText(password)))
        onView(
            Matchers.allOf(
                withId(R.id.et_password),
                withParent(withId(R.id.signUp_linear_layout))
            )
        ).perform(typeText(password)).check(matches(withText(password)))
    }

    @Test
    fun test_et_confirmPassword_is_visible() {
        //onView(withId(R.id.et_confirmPassword)).perform(typeText(password)).check(matches(withText(password)))
        onView(
            Matchers.allOf(
                withId(R.id.et_confirmPassword),
                withParent(withId(R.id.signUp_linear_layout))
            )
        ).perform(typeText(password)).check(matches(withText(password)))
    }

    @Test
    fun test_buttonSignUp_is_visible() {
        onView(withId(R.id.buttonSignUp)).check(matches(isDisplayed()))
    }

    @Test
    fun name_is_empty() {
        onView(withId(R.id.et_firstName)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.et_firstName)).check(matches(hasErrorText("Please enter your first name")))
    }

    @Test
    fun last_name_is_empty() {
        onView(withId(R.id.et_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_lastName)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.et_lastName)).check(matches(hasErrorText("Please enter your last name")))
    }

    @Test
    fun email_is_empty() {
        onView(withId(R.id.et_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_lastName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_email)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.et_email)).check(matches(hasErrorText("Please enter your email")))
    }

    @Test
    fun password_is_empty() {
        onView(withId(R.id.et_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_lastName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_email)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_password)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.et_password)).check(matches(hasErrorText("Please enter a password")))
    }

    @Test
    fun password_confirmation_is_empty() {
        onView(withId(R.id.et_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_lastName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_email)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_password)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_confirmPassword)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        onView(withId(R.id.et_password)).check(matches(hasErrorText("Please confirm your password")))
    }

    @Test
    fun details_entered_correctly() {
        onView(withId(R.id.et_firstName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_lastName)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_email)).perform(ViewActions.replaceText("test@gmail.com"))
        onView(withId(R.id.et_password)).perform(ViewActions.replaceText("test12"))
        onView(withId(R.id.et_confirmPassword)).perform(ViewActions.replaceText("test12"))
        onView(withId(R.id.buttonSignUp)).perform(ViewActions.click())
        intending(hasComponent(LogInActivity::class.qualifiedName))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}
