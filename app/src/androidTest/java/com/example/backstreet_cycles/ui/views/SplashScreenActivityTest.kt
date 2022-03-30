package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
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
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class SplashScreenActivityTest{

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"
    private val userRepoImpl = UserRepositoryImpl()

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
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun test_splash_screen_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(SplashScreenActivity::class.qualifiedName))
        Intents.release()
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
    suspend fun test_if_user_is_loggedin_goes_to_homepage(){
        test_splash_screen_activity_is_in_view()
        userRepoImpl.login(email, password)
        Thread.sleep(10000)
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
        userRepoImpl.logOut()
    }

    @Test
    fun test_if_user_is_not_logged_in_goes_to_login(){
        test_splash_screen_activity_is_in_view()
        userRepoImpl.logOut()
        Intents.init()
        intending(hasComponent(LogInActivity::class.qualifiedName))
        Intents.release()
    }

    @After
    fun tearDown(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
}