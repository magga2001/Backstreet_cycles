package views

import android.app.Application
import android.content.ComponentName
import android.support.test.InstrumentationRegistry.getTargetContext
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.UserRepository
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.views.HomePageActivity
import com.example.backstreet_cycles.views.LogInActivity
import com.example.backstreet_cycles.views.SplashScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SplashScreenActivityTest{

    lateinit var logInRegisterViewModel: LogInRegisterViewModel
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
//    private val userRepository: UserRepository =
//        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())

    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch(SplashScreenActivity::class.java)
    }

    @Test
    fun test_splash_screen_activity_is_in_view() {
        onView(withId(R.id.splashScreenActivity)).check(matches(isDisplayed()))
    }


    @Test
    fun test_image_is_in_view() {
        onView(withId(R.id.GifImage)).check(matches(isDisplayed()))
    }

    @Test
    fun progress_bar_is_displayed(){
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }

    /*@Test
    fun test_logo_is_in_view() {
        onView(withId(R.id.logo)).check(matches(isDisplayed()))
    }

    @Test
    fun test_motto_is_in_view() {
        onView(withId(R.id.motto)).check(matches(isDisplayed()))
    }*/

    @Test
    fun test_if_logged_HomePage_else_LoginPage(){

        if(firebaseAuth.currentUser != null){
            intending(hasComponent(HomePageActivity::class.qualifiedName))
        } else{
            intending(hasComponent(LogInActivity::class.qualifiedName))
        }

    }

    @After
    fun tearDown(){
        Intents.release()
    }
}