package com.example.backstreet_cycles.ui.views
//
//import androidx.arch.core.executor.testing.CountingTaskExecutorRule
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.IdlingRegistry
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.ext.junit.rules.ActivityScenarioRule
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import androidx.test.rule.GrantPermissionRule
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.common.EspressoIdlingResource
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//@HiltAndroidTest
//class LoadingActivityTest {
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @get:Rule
//    val countingTaskExecutorRule = CountingTaskExecutorRule()
//
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val locationRule: GrantPermissionRule =
//        GrantPermissionRule.grant(
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_NETWORK_STATE,
//            android.Manifest.permission.INTERNET)
//
//    @get:Rule
//    val activityRule = ActivityScenarioRule(LoadingActivity::class.java)
//
//    @Before
//    fun setUp() {
//        hiltRule.inject()
//        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
//    }
//
//    @Test
//    fun test_loading_activity_test_is_displayed() {
//        onView(withId(R.id.loadingActivity_text)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_loading_gif_displayed() {
//        onView(withId(R.id.loadingActivity_GifImage)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_loading_dots_gif_is_displayed() {
//        onView(withId(R.id.loadingActivity_dotsLoadingGifImage)).check(matches(isDisplayed()))
//    }
//
////    private fun waitFor(delay: Long): ViewAction {
////        return object : ViewAction {
////            override fun getConstraints(): Matcher<View> = ViewMatchers.isRoot()
////            override fun getDescription(): String = "wait for $delay milliseconds"
////            override fun perform(uiController: UiController, v: View?) {
////                uiController.loopMainThreadForAtLeast(delay)
////            }
////        }
////    }
//
//
//}