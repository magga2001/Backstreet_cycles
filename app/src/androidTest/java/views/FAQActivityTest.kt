/*
package views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.views.FAQActivity
import com.example.backstreet_cycles.views.HomePageActivity
import com.example.backstreet_cycles.views.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class FAQActivityTest {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var logInRegisterViewModel: LogInRegisterViewModel

    @Before
    fun setUp() {
        if (firebaseAuth.currentUser == null) {
            logInRegisterViewModel= LogInRegisterViewModel(Application())
            logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com","123456")
        }
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())
        onView(withId(R.id.faq)).perform(ViewActions.click())
        init()
    }


    @Test
    fun test_about_help_to_FAQActivity() {

        onView(ViewMatchers.withId(R.id.faq)).perform(ViewActions.click())
        Intents.intending(IntentMatchers.hasComponent(FAQActivity::class.qualifiedName))

    }

    @Test
    fun test_FAQtitle_is_displayed() {
        onView(withId(R.id.FAQTitle)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_title1_is_displayed() {

        onView(ViewMatchers.withId(R.id.faq)).perform(ViewActions.click())
        onView(withId(R.id.FAQ_q1)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_text1_is_displayed() {

        onView(ViewMatchers.withId(R.id.faq)).perform(ViewActions.click())
        onView(withId(R.id.FAQ_a1)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_title2_is_displayed() {

        onView(ViewMatchers.withId(R.id.faq)).perform(ViewActions.click())
        onView(withId(R.id.FAQ_q1)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_text2_is_displayed() {

        onView(ViewMatchers.withId(R.id.faq)).perform(ViewActions.click())
        onView(withId(R.id.FAQ_a2)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_title3_is_displayed() {

        onView(ViewMatchers.withId(R.id.faq)).perform(ViewActions.click())
        onView(withId(R.id.FAQ_q2)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_text3_is_displayed() {
        onView(ViewMatchers.withId(R.id.faq)).perform(ViewActions.click())
        onView(withId(R.id.FAQ_a3)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {

        onView(ViewMatchers.withId(R.id.faq)).perform(ViewActions.click())
        pressBack()
        Intents.intending(IntentMatchers.hasComponent(HomePageActivity::class.qualifiedName))
    }
    @After
    fun tearDown(){
        Intents.release()
    }

}*/
