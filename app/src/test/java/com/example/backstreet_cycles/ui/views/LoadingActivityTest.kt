//package com.example.backstreet_cycles.ui.views
//
//
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.Espresso.pressBack
//import androidx.test.espresso.UiController
//import androidx.test.espresso.ViewAction
//import androidx.test.espresso.action.ViewActions.*
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.contrib.RecyclerViewActions
//import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
//import androidx.test.espresso.intent.Intents
//import androidx.test.espresso.intent.Intents.intending
//import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
//import androidx.test.espresso.matcher.BoundedMatcher
//import androidx.test.espresso.matcher.ViewMatchers.*
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.IdlingRegistry
//import androidx.test.rule.GrantPermissionRule
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.common.EspressoIdlingResource
//import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import org.junit.Assert.*
//
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//@HiltAndroidTest
//class LoadingActivityTest {
//    private val email = "vafai.vandad@gmail.com"
//    private val password = "123456"
//    private val userRepoImpl = UserRepositoryImpl()
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @get:Rule
//    val locationRule: GrantPermissionRule =
//        GrantPermissionRule.grant(
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_NETWORK_STATE,
//            android.Manifest.permission.INTERNET)
//
//    @Before
//    fun setUp() {
//        userRepoImpl.login(email, password)
//        hiltRule.inject()
//        //IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
//        ActivityScenario.launch(LoadingActivity::class.java)
//    }
//
////    @Test
////    fun onCreate() {
////    }
////
////    @Test
////    fun onStart() {
////    }
//    @Test
//    fun test_loading_activity_test_is_dsplayed(){
//    onView(withId(R.id.loadingActivity_text)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_loading_gif_displayed(){
//        onView(withId(R.id.loadingActivity_GifImage)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_loading_dots_gif_is_displayed()
//    {
//        onView(withId(R.id.loadingActivity_dotsLoadingGifImage)).check(matches(isDisplayed()))
//
//    }
//
//
//
//    @After
//    fun tearDown() {
//        userRepoImpl.logOut()
//        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
//    }
//
//
//}