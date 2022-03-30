//package com.example.backstreet_cycles.ui.views
//
//import android.app.Application
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.Espresso.pressBack
//import androidx.test.espresso.IdlingRegistry
//import androidx.test.espresso.action.ViewActions
//import androidx.test.espresso.action.ViewActions.click
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.intent.Intents
//import androidx.test.espresso.intent.Intents.intending
//import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
//import androidx.test.espresso.matcher.ViewMatchers
//import androidx.test.espresso.matcher.ViewMatchers.*
//import androidx.test.ext.junit.rules.ActivityScenarioRule
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import androidx.test.rule.GrantPermissionRule
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.common.EspressoIdlingResource
//import com.example.backstreet_cycles.data.repository.UserRepositoryImpl
//import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.com.example.backstreet_cycles.FakeCyclistRepoImpl
//import java.com.example.backstreet_cycles.FakeMapboxRepoImpl
//import java.com.example.backstreet_cycles.FakeTflRepoImpl
//import java.com.example.backstreet_cycles.FakeUserRepoImpl
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//@HiltAndroidTest
//class HomePageActivityTest{
//
//    private val email = "backstreet.cycles.test.user@gmail.com"
//    private val password = "123456"
//
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
//        if(userRepoImpl.getFirebaseAuthUser() != null){
//            userRepoImpl.logout()
//        }
//        userRepoImpl.login(email, password)
//        hiltRule.inject()
//        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
//        ActivityScenario.launch(HomePageActivity::class.java)
//    }
//
//    @Test
//    fun test_map_on_homepage_is_displayed() {
//        onView(withId(R.id.homepage_mapView)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_bottom_sheet_is_displayed() {
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_drawer_layout_shown() {
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//    }
//
////    @Test
////    fun test_current_location_card_shown() {
////        //ActivityScenario.launch(HomePageActivity::class.java)
////        onView(
////            Matchers.allOf(
////                withId(R.id.card_name),
////                withParent(withId(R.id.cardView))
////            )
////        ).check(matches(withText("Current Location")))
////
////        //onView(withId(R.id.card_name)).check(matches(withText("Current Location")))
////    }
//
////    @Test
////    fun navigation_drawer_shows_about_button() {
////        ActivityScenario.launch(HomePageActivity::class.java)
////
////    }
////
////    @Test
////    fun navigation_drawer_shows_help_button() {
////
////    }
//
//    @Test
//    fun test_add_stop_button_is_enabled(){
//        onView(withId(R.id.addingBtn)).check(matches(isEnabled()))
//    }
//
//    @Test
//    fun test_add_stop_button_is_clickable(){
//        onView(withId(R.id.addingBtn)).check(matches(isClickable()))
//    }
//
//    @Test
//    fun test_current_location_button_is_disabled(){
//        onView(withId(R.id.myLocationButton)).check(matches(isNotEnabled()))
//    }
//
//    @Test
//    fun test_nextPage_is_disabled(){
//        onView(withId(R.id.nextPageButton)).check(matches(isNotEnabled()))
//    }
//
////    @Test
////    fun test_recyclerView_is_displayed(){
////        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
////    }
//
////    @Test
////    fun test_cardView_is_visible(){
//////        ActivityScenario.launch(HomePageActivity::class.java)
////        onView(withId(R.id.cardView)).check(matches(isDisplayed()))
////        onView(
////            Matchers.allOf(
////                withId(R.id.cardView),
////                withParent(withId(R.id.recyclerView))
////            )).check(matches(isDisplayed()))
////    }
////
////    @Test
////    fun test_cardView_is_swipeable(){
//////        ActivityScenario.launch(HomePageActivity::class.java)
////        onView(withId(R.id.cardView)).perform(swipeLeft())
////    }
////
////    @Test
////    fun test_cardView_is_draggable(){
//////        ActivityScenario.launch(HomePageActivity::class.java)
////        onView(withId(R.id.cardView)).perform(ViewActions.swipeUp())
////        onView(withId(R.id.cardView)).perform(ViewActions.swipeDown())
////    }
//
////    @Test
////    fun test_first_item_in_recycler_view_is_current_location(){
////
////        onView(withId(R.id.recyclerView))
////            .perform(scrollToPosition<StopsAdapter.StopViewHolder>(0))
////            .check(matches(hasDescendant(withText("Current Location"))))
////    }
//
////    @Test
////    fun test_next_page_button_disabled_when_one_item_in_recyclerView(){
//////        ActivityScenario.launch(HomePageActivity::class.java)
////
////        if(onView(withId(R.id.recyclerView)).(hasChildCount(1))){
////            onView(withId(R.id.nextPageButton)).check(matches(isNotEnabled()))
////        }
////        else{
////            onView(withId(R.id.nextPageButton)).check(matches(isEnabled()))
////
////        }
////        onView(withId(R.id.nextPageButton)).check(matches(isNotEnabled()))
////    }
//
//    @Test
//    fun bottom_sheet_invoked_show_users_field() {
//
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        //Checking whether the users field is displayed
//        onView(withId(R.id.users)).check(matches(withText("Cyclists:")))
//    }
//
//    @Test
//    fun bottom_sheet_invoked_show_number_of_users_is_one() {
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        //Checking whether the number of users is one
//        onView(withId(R.id.UserNumber)).check(matches(withText("1")))
//    }
//
//    @Test
//    fun bottom_sheet_invoked_show_increment_and_decrement_buttons() {
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        //Checking whether the increment and decrement buttons are displayed
//        onView(withId(R.id.decrementButton)).check(matches(isDisplayed()))
//        onView(withId(R.id.incrementButton)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun increment_button_clicked_number_of_users_incremented_by_one() {
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        //Click increment button
//        onView(withId(R.id.incrementButton)).perform(click())
//        //Checking if number of users changed to two
//        onView(withId(R.id.UserNumber)).check(matches(withText("2")))
//
//    }
//
//    @Test
//    fun decrement_button_clicked_number_of_users_decremented_by_one() {
//        // Checking whether bottom sheet is displayed
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        //Click increment button
//        onView(withId(R.id.incrementButton)).perform(click())
//        //Checking if number of users changed to two
//        onView(withId(R.id.UserNumber)).check(matches(withText("2")))
//        //Click decrement button
//        onView(withId(R.id.decrementButton)).perform(click())
//        //Checking if number of users changed back to one
//        onView(withId(R.id.UserNumber)).check(matches(withText("1")))
//    }
//
//    @Test
//    fun back_button_from_ChangeEmailOrPasswordActivity_to_HomePageActivity() {
//        onView(withContentDescription(R.string.open)).perform(click())
//        onView(withId(R.id.changePassword)).perform(click())
//        intending(hasComponent(ChangePasswordActivity::class.qualifiedName))
//        pressBack()
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//    }
//
//    @Test
//    fun back_button_from_EditUserProfileActivity_to_HomePageActivity() {
//        onView(withContentDescription(R.string.open)).perform(click())
//        onView(withId(R.id.profile)).perform(click())
//        intending(hasComponent(EditUserProfileActivity::class.qualifiedName))
//        pressBack()
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//    }
//
////     @Test
////     fun back_button_from_HomePageActivity_to_LogInActivity() {
////         ActivityScenario.launch(HomePageActivity::class.java)
////         onView(withContentDescription(R.string.open)).perform(ViewActions.click())
////         onView(withId(R.id.logout)).perform(ViewActions.click())
////         intending(hasComponent(LogInActivity::class.qualifiedName))
////         onView(withId(R.id.buttonCreateAccount)).perform(ViewActions.click())
////         pressBack()
////         intending(hasComponent(LogInActivity::class.qualifiedName))
////     }
//
//    @Test
//    fun test_current_location_button_disabled_when_already_in_recyclerView(){
//        //test_current_location_is_in_list()
//         onView(withId(R.id.myLocationButton)).check(matches(isNotEnabled()))
//    }
//
////    @Test
////    fun test_current_location_is_in_list(){
////        onView(withId(R.id.recyclerView))
////            .check(matches(hasDescendant(withText("Current Location"))))
////    }
//
////    @Test
////    fun test_fail_to_delete_first_item_in_recyclerView(){
//////        ActivityScenario.launch(HomePageActivity::class.java)
////        onView(withId(R.id.recyclerView)).check(matches(hasChildCount(1)))
////        onView(withId(R.id.recyclerView)).perform(
////            RecyclerViewActions.actionOnItemAtPosition<StopsAdapter.StopViewHolder>(0, swipeLeft()))
////        //swipeleft usu
////        ally deletes the card but since it's at the first place, it is not swipeable, the swipe is considered as a click
////        // so press back goes back to the home page and checks the number of items
////        pressBack()
////        onView(withId(R.id.recyclerView)).check(matches(hasChildCount(1)))
////    }
//
////    @Test
////    fun test_search_location_is_shown_when_a_stop_is_clicked(){
//////        ActivityScenario.launch(HomePageActivity::class.java)
////        onView(withId(R.id.HomePageActivity)).isVisible()
////        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<StopsAdapter.StopViewHolder>(0,
////            click()))
////        onView(withId(R.id.HomePageActivity)).isGone()
////
////    }
//
////    @Test
////    fun test_search_location_is_shown_when_add_stop_button_is_clicked(){
//////        ActivityScenario.launch(HomePageActivity::class.java)
////        intending(hasComponent(HomePageActivity::class.qualifiedName))
////        onView(withId(R.id.addingBtn)).perform(click())
////        onView(withId(R.id.HomePageActivity)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
////    }
//
//    /*@Test
//    fun test_refresh_button_is_visible() {
//        onView(withId(R.id.refresh_button)).check(matches(isDisplayed()))
//
//    }*/
//
////    @Test
////    fun test_goBackTo_homepage_when_back_clicked_from_autoCompleteAPI(){
////        ActivityScenario.launch(HomePageActivity::class.java)
////        onView(withId(R.id.HomePageActivity)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
////        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<StopsAdapter.StopViewHolder>
////            (0, click()))
////        onView(withId(R.id.HomePageActivity)).check(matches(withEffectiveVisibility(Visibility.GONE)))
////        pressBack()
////        intending(hasComponent(HomePageActivity::class.qualifiedName))
////    }
//
//        @After
//        fun tearDown(){
//            if(userRepoImpl.getFirebaseAuthUser() != null){
//                IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
//                userRepoImpl.logout()
//            }
//        }
//
//
//}