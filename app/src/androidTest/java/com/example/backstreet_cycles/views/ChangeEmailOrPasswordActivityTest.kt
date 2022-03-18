package com.example.backstreet_cycles.views
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ChangeEmailOrPasswordActivityTest{

    lateinit var logInRegisterViewModel: LogInRegisterViewModel

    @Before
    fun setUp() {
        logInRegisterViewModel= LogInRegisterViewModel(Application())
        logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com","123456")
        ActivityScenario.launch(ChangeEmailOrPasswordActivity::class.java)
        /*Espresso.onView(ViewMatchers.withId(R.id.nav_view)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.changeEmailOrPassword)).perform(ViewActions.click())*/

    }
    @Test
    fun test_activity_launched_user_email_displayed() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        onView(withId(R.id.et_email)).check(matches(ViewMatchers.withText(email)))
    }

    @Test
    fun test_activity_launched_text_field_current_password() {
        Espresso.onView(ViewMatchers.withId(R.id.et_current_password)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val testInput = "password"
        Espresso.onView(ViewMatchers.withId(R.id.et_current_password)).perform(typeText(testInput))
        onView(withId(R.id.et_current_password)).check(matches(ViewMatchers.withText(testInput)))
    }

    @Test
    fun test_activity_launched_text_field_new_password() {
        Espresso.onView(ViewMatchers.withId(R.id.et_new_password)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        val testInput = "password"
        Espresso.onView(ViewMatchers.withId(R.id.et_new_password)).perform(typeText(testInput))
        onView(withId(R.id.et_new_password)).check(matches(ViewMatchers.withText(testInput)))
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        Espresso.pressBack()
        Espresso.onView(ViewMatchers.withId(R.id.HomePageActivity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}