package com.example.backstreet_cycles.ui.views

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.data.repository.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
class NavigationActivityTest {

    private val firstName = "Test"
    private val lastName = "User"
    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    @Inject
    lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    @Inject
    lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    @Inject
    lateinit var fakeUserRepoImpl: FakeUserRepoImpl
    @Inject
    lateinit var fakeLocationRepoImpl: FakeLocationRepoImpl

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET
        )

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeUserRepoImpl.logOut()
        fakeUserRepoImpl.register(firstName, lastName, email, password)
        fakeUserRepoImpl.verifyEmail(email)
        fakeUserRepoImpl.login(email, password)
        ActivityScenario.launch(NavigationActivity::class.java)
    }

    @Test
    fun navigation_activity_is_displayed(){
        Intents.init()
        intending(hasComponent(NavigationActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun navigation_mapView_is_displayed(){
        onView(withId(R.id.navigation_mapView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_tripProgressCard_is_displayed(){
        onView(withId(R.id.tripProgressCard)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_tripProgressView_is_displayed(){
        onView(withId(R.id.tripProgressView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
    @Test
    fun navigation_imageViewStop_is_displayed(){
        onView(withId(R.id.stop)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_maneuverView_is_displayed(){
        onView(withId(R.id.maneuverView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun navigation_soundButton_is_displayed(){
        onView(withId(R.id.soundButton)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_routeOverview_is_displayed(){
        onView(withId(R.id.routeOverview)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_MapboxRecenterButton_is_displayed(){
        onView(withId(R.id.recenter)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @After
    fun tearDown(){
        fakeUserRepoImpl.logOut()
    }
}