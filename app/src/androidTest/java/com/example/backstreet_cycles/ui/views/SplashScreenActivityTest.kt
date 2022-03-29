package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.com.example.backstreet_cycles.FakeCyclistRepoImpl
import java.com.example.backstreet_cycles.FakeMapboxRepoImpl
import java.com.example.backstreet_cycles.FakeTflRepoImpl
import java.com.example.backstreet_cycles.FakeUserRepoImpl

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class SplashScreenActivityTest{

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private val fakeUserRepoImpl = FakeUserRepoImpl()
    private val fakeTflRepoImpl = FakeTflRepoImpl()
    private val fakeMapboxRepoImpl = FakeMapboxRepoImpl()
    private val fakeCyclistRepoImpl = FakeCyclistRepoImpl()
    private val context = Application()

    private val logInViewModel = LogInViewModel(fakeTflRepoImpl,fakeMapboxRepoImpl,fakeCyclistRepoImpl,fakeUserRepoImpl,context)

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule: ActivityScenarioRule<SplashScreenActivity> =
        ActivityScenarioRule(SplashScreenActivity::class.java)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        hiltRule.inject()
        if (logInViewModel.getMutableLiveData().value != null){
            fakeUserRepoImpl.logout()
        }
        Intents.init()
    }

    @Test
    fun test_splash_screen_activity_is_in_view() {
        intending(hasComponent(SplashScreenActivity::class.qualifiedName))
    }

    @Test
    fun test_image_is_in_view() {
        onView(withId(R.id.backStreet_cycles_image)).check(matches(isDisplayed()))
    }

    @Test
    fun progress_bar_is_displayed(){
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }

    @Test
    fun test_text_is_in_view() {
        onView(withId(R.id.splash_screen_activity_text)).check(matches(isDisplayed()))
    }

//    @Test
//    fun test_if_logged_HomePage_else_LoginPage(){
//
//        if(fakeUserRepoImpl.getCurrentUser() != null){
//            intending(hasComponent(HomePageActivity::class.qualifiedName))
//        } else{
//            intending(hasComponent(LogInActivity::class.qualifiedName))
//        }
//
//    }

    @After
    fun tearDown(){
        Intents.release()
    }
}