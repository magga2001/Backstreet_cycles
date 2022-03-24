package views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.model.UserRepository
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.views.EditUserProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4ClassRunner::class)
class EditUserProfileActivityTest{

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var logInRegisterViewModel: LogInRegisterViewModel

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"
    @get:Rule
    val fineLocPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        if (firebaseAuth.currentUser == null) {
        logInRegisterViewModel = LogInRegisterViewModel(Application())
        logInRegisterViewModel.login(email, password)
    }
        Application().onCreate()
        ActivityScenario.launch(EditUserProfileActivity::class.java)
        sleep(100)
    }

    @Test
    fun test_activity_is_in_view() {
        onView(withId(R.id.editUserProfile)).check(matches(isDisplayed()))
    }

    @Test
    fun test_title_is_visible() {
        onView(withId(R.id.et_edit_profile_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_buttonUpdateProfile_is_visible() {
        onView(withId(R.id.buttonUpdateProfile)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_first_name_field_is_visible() {
        onView(withId(R.id.et_firstName_edit_user)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_last_name_field_is_visible() {
        onView(withId(R.id.et_lastName_edit_user)).check(matches(isDisplayed()))
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity(){
        pressBack()
        onView(withId(R.id.HomePageActivity)).check(matches(isDisplayed()))
    }

}