package views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.DTO.Users
import com.example.backstreet_cycles.model.UserRepository
import com.example.backstreet_cycles.views.HomePageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class NavMenuTest {
//    private var logInRegisterViewModel: LogInRegisterViewModel = LogInRegisterViewModel(Application())

    private val email: String = "backstreet.cycles.test.user@gmail.com"
    private val password: String =" 123456"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository: UserRepository =
        UserRepository(Application(), Firebase.firestore, FirebaseAuth.getInstance())

    @Before
    fun setUp() {
//        logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com","123456")
//        if (firebaseAuth.currentUser != null){
//            firebaseAuth.signOut()
//            userRepository.login(email,password)
////            userRepository.getMutableLiveData().postValue(firebaseAuth.currentUser)
//        } else {
//            userRepository.login(email, password)
//        }
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(withContentDescription(R.string.open)).perform(click())
    }

    @Test
    fun test_drawer_is_open(){
        onView(withId(R.id.nav_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_drawer_is_closed(){
        onView(withContentDescription(R.string.close)).perform(click())
        onView(withId(R.id.nav_view)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun test_navToggle_toNavMenu() {
        onView(withId(R.id.profile)).check(matches(isDisplayed()))
        onView(withId(R.id.changePassword)).check(matches(isDisplayed()))
        onView(withId(R.id.about)).check(matches(isDisplayed()))
//        onView(withId(R.id.help)).check(matches(isDisplayed()))
        onView(withId(R.id.logout)).check(matches(isDisplayed()))
        onView(withId(R.id.currentJourney)).check(matches(isDisplayed()))
        onView(withId(R.id.journeyHistory)).check(matches(isDisplayed()))
    }

    @Test
    fun test_viewProfileButton_toEditProfileActivity() {
        onView(withId(R.id.profile)).perform(click())
        onView(withId(R.id.editUserProfile)).check(matches(isDisplayed()))
    }

    @Test
    fun test_viewCurrentJourney_toJourneyActivity() {
        onView(withId(R.id.currentJourney)).perform(click())
        onView(withId(R.id.journey_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_viewJourneyHistory_toJourneyHistoryActivity() {
        onView(withId(R.id.journeyHistory)).perform(click())
        onView(withId(R.id.journeyHistoryActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_logOutButton_toLogInActivity() {
        onView(withId(R.id.logout)).perform(click())
        onView(withId(R.id.logInActivity)).check(matches(isDisplayed()))
    }


    @Test
    fun test_changePassword_toChangeEmailOrPasswordActivity() {
        onView(withId(R.id.changePassword)).perform(click())
        onView(withId(R.id.changeEmailOrPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun test_aboutButton_to_aboutActivity()
    {
        val activityScenario = ActivityScenario.launch(HomePageActivity::class.java)
        onView(withId(R.id.nav_view)).perform(click())
        onView(withId(R.id.about)).perform(ViewActions.click())
        onView(withId(R.id.aboutActivity)).check(matches(isDisplayed()))
    }


    @Test
    fun test_nav_showUserName(){
        onView(withId(R.id.user_name)).check(matches(isDisplayed()))
    }

    @Test
    fun test_nav_showUserEmail(){
        onView(withId(R.id.tv_email)).check(matches(isDisplayed()))
    }

    @Test
    fun test_nav_equalCurrentUserName(){
        val testUserName = FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("email", FirebaseAuth.getInstance().currentUser!!.email)
            .get().result.toObjects(Users::class.java)[0].firstName
        val textElement = "Hello: "//$testUserName"
        val testDisplayedName = getApplicationContext<Application>().getString(R.id.user_name)
        assert(testDisplayedName == textElement)

        //val email = FirebaseAuth.getInstance().currentUser?.
    }

    @Test
    fun test_nav_equalCurrentUserEmail(){
        val email = FirebaseAuth.getInstance().currentUser?.email
        onView(withId(R.id.tv_email)).check(matches(withText(email)))
    }
}