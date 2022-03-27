//package com.example.backstreet_cycles.ui.views
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import com.example.backstreet_cycles.R
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//class EditUserProfileActivityTest{
//    @Test
//    fun test_activity_is_in_view() {
//        val activityScenario=ActivityScenario.launch(EditUserProfileActivity::class.java)
//        onView(withId(R.id.editUserProfile)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_title_is_visible() {
//        val activityScenario=ActivityScenario.launch(EditUserProfileActivity::class.java)
//        onView(withId(R.id.change_password_profile_details_title)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_buttonUpdateProfile_is_visible() {
//        val activityScenario=ActivityScenario.launch(EditUserProfileActivity::class.java)
//        onView(withId(R.id.edit_user_details_SaveButton)).check(matches(isDisplayed()))
//
//    }
//
//    @Test
//    fun test_et_first_name_is_visible() {
//        val activityScenario = ActivityScenario.launch(EditUserProfileActivity::class.java)
//        onView(withId(R.id.edit_user_details_firstName)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_et_last_name_is_visible() {
//        val activityScenario = ActivityScenario.launch(EditUserProfileActivity::class.java)
//        onView(withId(R.id.edit_user_details_lastName)).check(matches(isDisplayed()))
//    }
//
//}