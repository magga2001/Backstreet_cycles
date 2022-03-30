package com.example.backstreet_cycles.ui.views

import android.os.SystemClock.sleep
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class CurrentJourneyTest{

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private val userRepoImpl = UserRepositoryImpl()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET)

    @Before
    fun setUp() {
        if(userRepoImpl.getFirebaseAuthUser() == null){
            userRepoImpl.login(email, password)
        }
        hiltRule.inject()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(HomePageActivity::class.java)
        Espresso.onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.currentJourney)).perform(ViewActions.click())
    }

    @Test
    fun test_activity_is_in_view() {
        Intents.init()
        intending(hasComponent(JourneyActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun test_on_pressBack_go_to_HomePageActivity(){
        Intents.init()
        intending(hasComponent(JourneyActivity::class.qualifiedName))
        Intents.release()
        pressBackUnconditionally()
        Intents.init()
        intending(hasComponent(HomePageActivity::class.qualifiedName))
        Intents.release()
    }

    @After
    fun tearDown(){
        if(userRepoImpl.getFirebaseAuthUser() != null){
            IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
            userRepoImpl.logout()
        }
    }
}
