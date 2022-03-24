package views
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseUser

import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.app.Application
import android.content.Context
import android.support.test.InstrumentationRegistry.getContext
import android.widget.Button
import android.widget.LinearLayout
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.views.HomePageActivity
import com.example.backstreet_cycles.views.LogInActivity
import com.example.backstreet_cycles.views.SignUpActivity
import com.mapbox.maps.extension.style.expressions.dsl.generated.all
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Rule
import java.lang.Thread.sleep


@RunWith(AndroidJUnit4ClassRunner::class)
class LogInActivityTest{
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())
    private lateinit var logInRegisterViewModel: LogInRegisterViewModel

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
        if (firebaseAuth.currentUser != null) {
            userRepository.logout()
        }
        ActivityScenario.launch(LogInActivity::class.java)
        Intents.init()
    }

    @Test
    fun test_activity_is_in_view() {
        intending(hasComponent(LogInActivity::class.qualifiedName))
    }

    @Test
    fun test_title_is_visible() {
        onView(withId(R.id.et_log_in_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_buttonCreateAccount_is_visible() {
        onView(withId(R.id.buttonCreateAccount)).check(matches(isDisplayed()))
    }

    @Test
    fun test_buttonLogin_is_visible() {
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun test_email_text_box_validate_input() {
        onView(withId(R.id.et_email_log_in)).perform(typeText(email)).check(matches(withText(email)))
    }

    @Test
    fun test_password_text_box_validate_input() {
        onView(withId(R.id.et_password_log_in)).perform(typeText(password)).check(matches(withText(password)))
    }

    @Test
    fun test_launch_sign_up_with_create_button() {
        onView(allOf(withId(R.id.buttonCreateAccount), withParent(withId(R.id.log_in_linear_layout)))).perform(click())
        intending(hasComponent(SignUpActivity::class.qualifiedName))

    }

    @Test
    fun test_backPress_toLogInActivity() {
        onView(withId(R.id.buttonCreateAccount)).perform(click())
        intending(hasComponent(SignUpActivity::class.qualifiedName))
        pressBack()
        intending(hasComponent(LogInActivity::class.qualifiedName))
    }

    @Test
    fun test_homePageActivityLaunched_when_login_clicked(){
        onView(withId(R.id.et_email_log_in)).perform(typeText(email)).check(matches(withText(email)))
        onView(withId(R.id.et_password_log_in)).perform(typeText(password)).check(matches(withText(password)))
        intending(hasComponent(HomePageActivity::class.qualifiedName))

    }

    @After
    fun tearDown(){
        Intents.release()
    }
}