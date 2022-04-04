package com.example.backstreet_cycles.ui.views
//
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.IdlingRegistry
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import androidx.test.rule.GrantPermissionRule
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.common.EspressoIdlingResource
//import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//@HiltAndroidTest
//class LoadingActivityTest {
//    private val email = "backstreet.cycles.test.user@gmail.com"
//    private val password = "123456"
//    private val userRepoImpl = UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @get:Rule
//    val locationRule: GrantPermissionRule =
//        GrantPermissionRule.grant(
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_NETWORK_STATE,
//            android.Manifest.permission.INTERNET)
//
//    @Before
//    fun setUp() {
//        userRepoImpl.logOut()
//        userRepoImpl.login(email, password)
//        hiltRule.inject()
//        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
//        ActivityScenario.launch(LoadingActivity::class.java)
//    }
//
//    @Test
//    fun test_loading_activity_test_is_dsplayed() {
//    onView(withId(R.id.loadingActivity_text)).check(matches(isDisplayed()))
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
//    @After
//    fun tearDown() {
//        userRepoImpl.logOut()
//        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
//    }
//}