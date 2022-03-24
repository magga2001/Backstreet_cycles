/*
package views
import android.app.Application
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.views.EditUserProfileActivity
import com.example.backstreet_cycles.views.LogInActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ForgotPasswordActivityTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(ForgotPasswordActivity::class.java)
        init()
    }

    @Test
    fun test_activity_is_in_view() {
        intending(hasComponent(ForgotPasswordActivity::class.qualifiedName))
    }

    @Test
    fun test_title_is_visible() {
        onView(withId(R.id.forgot_password_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_email_text_field_is_visible() {
        onView(withId(R.id.et_email_forgot_password)).check(matches(isDisplayed()))
    }

    @Test
    fun test_forgotButton_is_visible() {
        onView(withId(R.id.button_send_password_reset)).check(matches(isDisplayed()))
    }

    @Test
    fun test_forgotButton_clicked_to_LoginPage() {
        onView(withId(R.id.button_send_password_reset)).perform(click())
        intending(hasComponent(LogInActivity::class.qualifiedName))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}*/
