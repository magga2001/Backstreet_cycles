package com.example.backstreet_cycles.views
import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class EditUserProfileActivityTest{

    lateinit var logInRegisterViewModel: LogInRegisterViewModel

    @Before
    fun setUp() {
        logInRegisterViewModel= LogInRegisterViewModel(Application())
        logInRegisterViewModel.login("backstreet.cycles.test.user@gmail.com","123456")
        ActivityScenario.launch(HomePageActivity::class.java)
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())

    }
    @Test
    fun test_activity_is_in_view() {
        val activityScenario=ActivityScenario.launch(EditUserProfileActivity::class.java)
        onView(withId(R.id.editUserProfile)).check(matches(isDisplayed()))
    }

    @Test
    fun test_title_is_visible() {
        val activityScenario=ActivityScenario.launch(EditUserProfileActivity::class.java)
        onView(withId(R.id.et_edit_profile_title)).check(matches(isDisplayed()))
    }

    @Test
    fun test_buttonUpdateProfile_is_visible() {
        val activityScenario=ActivityScenario.launch(EditUserProfileActivity::class.java)
        onView(withId(R.id.buttonUpdateProfile)).check(matches(isDisplayed()))

    }

    @Test
    fun test_et_first_name_is_visible() {
        val activityScenario = ActivityScenario.launch(EditUserProfileActivity::class.java)
        onView(withId(R.id.et_firstName)).check(matches(isDisplayed()))
    }

    @Test
    fun test_et_last_name_is_visible() {
        val activityScenario = ActivityScenario.launch(EditUserProfileActivity::class.java)
        onView(withId(R.id.et_lastName)).check(matches(isDisplayed()))
    }

}