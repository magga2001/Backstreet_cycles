package views
//
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.Espresso.pressBack
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.views.EditUserProfileActivity
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.lang.Thread.sleep
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//class EditUserProfileActivityTest{
//
////    private lateinit var logInRegisterViewModel: LogInRegisterViewModel
//
////    @get:Rule val fineLocPermissionRule: GrantPermissionRule =
////        GrantPermissionRule.grant(
////            android.Manifest.permission.ACCESS_FINE_LOCATION,
////            android.Manifest.permission.ACCESS_NETWORK_STATE,
////            android.Manifest.permission.INTERNET)
//
//    @Before
//    fun setUp() {
////        Application().onCreate()
//
//        ActivityScenario.launch(EditUserProfileActivity::class.java)
//        sleep(100)
//    }
//    @Test
//    fun test_activity_is_in_view() {
//        onView(withId(R.id.editUserProfile)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_title_is_visible() {
//        onView(withId(R.id.et_edit_profile_title)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_buttonUpdateProfile_is_visible() {
//        onView(withId(R.id.buttonUpdateProfile)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_et_first_name_field_is_visible() {
//        onView(withId(R.id.et_firstName)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_et_last_name_field_is_visible() {
//        onView(withId(R.id.et_lastName)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_on_pressBack_go_to_HomePageActivity(){
//        pressBack()
//        onView(withId(R.id.HomePageActivity)).check(matches(isDisplayed()))
//    }
//
//}