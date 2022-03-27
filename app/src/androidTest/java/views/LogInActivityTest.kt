package views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.data.repository.UserRepository
import com.example.backstreet_cycles.ui.ui.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.ui.ui.views.HomePageActivity
import com.example.backstreet_cycles.ui.ui.views.LogInActivity
import com.example.backstreet_cycles.ui.ui.views.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class LogInActivityTest{
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())
    private lateinit var logInRegisterViewModel: LogInRegisterViewModel

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"
    private val empty = ""

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

    @Test
    fun login_email_is_empty() {
        onView(withId(R.id.et_email_log_in)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonLogin)).perform(ViewActions.click())
        onView(withId(R.id.et_email_log_in)).check(matches(hasErrorText("Please enter your email")))
    }

    @Test
    fun login_password_is_empty() {
        onView(withId(R.id.et_email_log_in)).perform(ViewActions.replaceText("test"))
        onView(withId(R.id.et_password_log_in)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonLogin)).perform(ViewActions.click())
        onView(withId(R.id.et_password_log_in)).check(matches(hasErrorText("Please enter a password")))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}