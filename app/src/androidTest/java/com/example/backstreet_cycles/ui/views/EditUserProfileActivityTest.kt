package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.ui.viewModel.LogInRegisterViewModel
import com.example.backstreet_cycles.ui.ui.views.EditUserProfileActivity
import com.example.backstreet_cycles.ui.ui.views.HomePageActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
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
        init()
    }
    @Test
    fun test_activity_is_in_view() {
        intending(hasComponent(EditUserProfileActivity::class.qualifiedName))
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

//    @Test
//    fun test_check_if_first_name_is_inputted(){
//
//    }
//    @Test
//    fun test_check_if_last_name_is_inputted(){
//
//    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity(){
        pressBack()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
    }


    @Test
    fun test_on_clickUpdateProfile_Empty_firstName(){
        onView(withId(R.id.et_firstName_edit_user)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonUpdateProfile)).perform(ViewActions.click())
        onView(withId(R.id.et_firstName_edit_user)).check(matches(hasErrorText("Please enter your first name")))
    }

    @Test
    fun test_on_clickUpdateProfile_Empty_lastName(){
        onView(withId(R.id.et_firstName_edit_user)).perform(ViewActions.replaceText("Test"))
        onView(withId(R.id.et_lastName_edit_user)).perform(ViewActions.replaceText(""))
        onView(withId(R.id.buttonUpdateProfile)).perform(ViewActions.click())
        onView(withId(R.id.et_lastName_edit_user)).check(matches(hasErrorText("Please enter your last name")))
    }

    @Test
    fun test_on_clickUpdateProfile_save(){
        onView(withId(R.id.et_firstName_edit_user)).perform(ViewActions.replaceText("Name"))
        onView(withId(R.id.et_lastName_edit_user)).perform(ViewActions.replaceText("Surname"))
        onView(withId(R.id.buttonUpdateProfile)).perform(ViewActions.click())
        intending(hasComponent(HomePageActivity::class.qualifiedName))
    }

    @After
    fun tearDown(){
        Intents.release()
    }




}