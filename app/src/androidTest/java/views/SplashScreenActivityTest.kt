package views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.UserRepository
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.views.ChangeEmailOrPasswordActivity
import com.example.backstreet_cycles.views.SplashScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4ClassRunner::class)
class SplashScreenActivityTest{

    lateinit var logInRegisterViewModel: LogInRegisterViewModel
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())

    @Before
    fun setUp() {
        Application().onCreate()
        /*logInRegisterViewModel= LogInRegisterViewModel(Application())
        logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com","123456")*/
        ActivityScenario.launch(SplashScreenActivity::class.java)
        sleep(100)
    }

    @Test
    fun test_splash_screen_activity_is_in_view() {
        onView(withId(R.id.splashScreenActivity)).check(matches(isDisplayed()))
    }

   /* @Test
    fun test_progress_bar_is_in_view() {
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }*/

    @Test
    fun test_image_is_in_view() {
        onView(withId(R.id.GifImage)).check(matches(isDisplayed()))
    }

    /*@Test
    fun test_logo_is_in_view() {
        onView(withId(R.id.logo)).check(matches(isDisplayed()))
    }

    @Test
    fun test_motto_is_in_view() {
        onView(withId(R.id.motto)).check(matches(isDisplayed()))
    }*/

//    @Test
//    fun test_if_logged_HomePage_else_LoginPage(){
//        if(firebaseAuth.currentUser != null){
//            onView(withId(R.id.HomePageActivity)).check(matches(isDisplayed()))
//        }
//        else{
//            onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))
//        }
//    }

}