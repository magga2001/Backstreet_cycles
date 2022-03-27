package views


import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.data.repository.UserRepository
import com.example.backstreet_cycles.ui.ui.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.ui.ui.views.HomePageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class AboutActivityTest {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var logInRegisterViewModel: LogInRegisterViewModel

    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())

    @Before
    fun setUp() {
        if (firebaseAuth.currentUser == null) {
            logInRegisterViewModel= LogInRegisterViewModel(Application())
            logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com","123456")
        }
        Application().onCreate()
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.nav_view)).perform(click())
    }

    @Test
    fun test_about_button_to_AboutActivity() {
        onView(withId(R.id.aboutActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_title_is_displayed() {
        onView(withId(R.id.aboutTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun test_description_is_displayed() {
        onView(withId(R.id.aboutDescription)).check(matches(isDisplayed()))
    }

//    @Test
//    fun test_logo_is_displayed() {
//        onView(withId(R.id.about_image)).check(matches(isDisplayed()))
//    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        Intents.init()
        pressBack()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()

    }
}
