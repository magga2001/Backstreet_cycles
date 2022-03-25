package views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
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
import com.example.backstreet_cycles.views.ChangeEmailOrPasswordActivity
import com.example.backstreet_cycles.views.HomePageActivity
import com.example.backstreet_cycles.views.JourneyActivity
import com.example.backstreet_cycles.views.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class CurrentJourneyTest {

    lateinit var logInRegisterViewModel: LogInRegisterViewModel

    @Before
    fun setUp() {
        logInRegisterViewModel = LogInRegisterViewModel(Application())
        logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com", "123456")
        ActivityScenario.launch(JourneyActivity::class.java)
        init()
    }

    @Test
    fun test_activity_is_in_view() {
        intending(hasComponent(JourneyActivity::class.qualifiedName))

    }

//    @Test
//    fun test_on_pressBack_go_to_HomePageActivity() {
//        Espresso.pressBack()
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//
//    }

    @After
    fun tearDown(){
        Intents.release()
    }
}
