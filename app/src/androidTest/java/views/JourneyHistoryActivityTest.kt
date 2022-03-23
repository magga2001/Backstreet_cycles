package views
//
//import android.app.Application
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions.typeText
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
//import com.example.backstreet_cycles.views.ChangeEmailOrPasswordActivity
//import com.example.backstreet_cycles.views.JourneyActivity
//import com.example.backstreet_cycles.views.JourneyHistoryActivity
//import com.google.firebase.auth.FirebaseAuth
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//class JourneyHistoryActivityTest {
//
//    lateinit var logInRegisterViewModel: LogInRegisterViewModel
//
//    @Before
//    fun setUp() {
//        logInRegisterViewModel = LogInRegisterViewModel(Application())
//        logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com", "123456")
//        ActivityScenario.launch(JourneyHistoryActivity::class.java)
//    }
//
//    @Test
//    fun test_activity_is_in_view() {
//        onView(withId(R.id.journeyHistoryActivity)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_on_pressBack_go_to_HomePageActivity() {
//        Espresso.pressBack()
//        onView(withId(R.id.HomePageActivity)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_title_is_visible() {
//        onView(withId(R.id.textView3)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_recycler_view_is_visible() {
//        onView(withId(R.id.journey_history_recycler_view)).check(matches(isDisplayed()))
//    }
//}