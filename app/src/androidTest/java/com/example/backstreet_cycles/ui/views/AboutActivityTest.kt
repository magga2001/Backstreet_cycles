package com.example.backstreet_cycles.ui.views
import android.app.Application
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
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
class AboutActivityTest {

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
    var activityRule: ActivityScenarioRule<HomePageActivity> =
        ActivityScenarioRule(HomePageActivity::class.java)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        hiltRule.inject()
        if (logInViewModel.getMutableLiveData().value == null){
            fakeUserRepoImpl.login(email, password)
        }
        Intents.init()
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(click())
        onView(withId(R.id.homepage_nav_view)).perform(click())
    }


    @Test
    fun test_about_button_to_AboutActivity() {
        onView(withId(R.id.aboutActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_title_is_displayed() {
        onView(withId(R.id.aboutTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun test_description_is_displayed() {
        onView(withId(R.id.aboutDescription)).check(matches(isDisplayed()))
    }

    @Test
    fun test_logo_is_displayed() {
        onView(withId(R.id.about_image)).check(matches(isDisplayed()))
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity() {
        pressBack()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
    }

    @After
    fun tearDown(){
        Intents.release()
    }
}
