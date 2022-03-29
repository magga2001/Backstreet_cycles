package com.example.backstreet_cycles.ui.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.EspressoIdlingResource
import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
class NavMenuTest{
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
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())
    }

    @Test
    fun test_drawer_is_open(){
        onView(withId(R.id.homepage_nav_view)).check(matches(isDisplayed()))
    }

    @Test
    fun test_drawer_is_closed(){
        onView(withContentDescription(R.string.close)).perform(click())
        onView(withId(R.id.homepage_nav_view)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun test_navToggle_toNavMenu() {
        onView(withId(R.id.profile)).check(matches(isDisplayed()))
        onView(withId(R.id.changePassword)).check(matches(isDisplayed()))
        onView(withId(R.id.about)).check(matches(isDisplayed()))
        onView(withId(R.id.faq)).check(matches(isDisplayed()))
        onView(withId(R.id.logout)).check(matches(isDisplayed()))
    }

    @Test
    fun test_viewProfileButton_toEditProfileActivity() {
        onView(withId(R.id.profile)).perform(click())
        onView(withId(R.id.editUserProfileActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_changePassword_toChangeEmailOrPasswordActivity() {
        onView(withId(R.id.changePassword)).perform(click())
        onView(withId(R.id.changePasswordActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_aboutButton(){
        onView(withId(R.id.about)).perform(click())
        onView(withId(R.id.aboutActivity)).check(matches(isDisplayed()))
    }


    @Test
    fun test_nav_showUserName(){
        onView(withId(R.id.user_name)).check(matches(isDisplayed()))
    }

//    @Test
//    fun test_nav_showUserEmail(){
//        onView(withId(R.id.nav_header_textView_email)).check(matches(isDisplayed()))
//    }

//    @Test
//    fun test_nav_equalCurrentUserName(){
//
//        val testUserName = FirebaseFirestore.getInstance().collection("users")
//            .whereEqualTo("email", FirebaseAuth.getInstance().currentUser!!.email)
//            .get().result.toObjects(Users::class.java)[0].firstName
//
//        val textElement = "Hello: $testUserName"
//
//        val testDisplayedName = getApplicationContext<Application>().getString(R.id.user_name)
//
//
//        assert(testDisplayedName == textElement)
//    }

//    @Test
//    fun test_nav_equalCurrentUserEmail(){
//        //setContentView(R.layout.activity_homepage)
//        val email = FirebaseAuth.getInstance().currentUser?.email
//
//        //val testDisplayedEmail = onView(withId(R.id.nav_header_textView_email)).toString()
//            //getApplicationContext<Application>().onView(R.id.tv_email).text
//
//        onView(withId(R.id.nav_header_textView_email)).check(matches(withText(email)))
//        //assert(testDisplayedEmail == email)
//    }
    @After
    fun tearDown(){
        userRepoImpl.logout()
    }
}