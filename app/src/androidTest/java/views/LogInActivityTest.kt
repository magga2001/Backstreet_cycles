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
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.views.LogInActivity
import org.junit.After
import java.lang.Thread.sleep


@RunWith(AndroidJUnit4ClassRunner::class)
class LogInActivityTest{
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())
    private lateinit var logInRegisterViewModel: LogInRegisterViewModel

    @Before
    fun setUp() {
        Application().onCreate()
        if (firebaseAuth.currentUser != null) {
            userRepository.logout()
        }
        ActivityScenario.launch(LogInActivity::class.java)
        sleep(100)
    }

    @Test
    fun test_activity_is_in_view() {
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))
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
    fun test_et_email_is_visible() {
        onView(withId(R.id.et_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_password_is_visible() {
        onView(withId(R.id.et_password)).check(matches(isDisplayed()))
    }

    @Test
    fun test_navigation_createAccount() {
        onView(withId(R.id.buttonCreateAccount)).perform(click())
        onView(withId(R.id.signUpActivity)).check(matches(isDisplayed()))

    }
    @Test
    fun test_backPress_onLogInActivity() {
        pressBack()
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_backPress_toLogInActivity() {
        onView(withId(R.id.buttonCreateAccount)).perform(click())
        onView(withId(R.id.signUpActivity)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown(){
        logInRegisterViewModel = LogInRegisterViewModel(Application())
        logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com", "123456")
    }

}