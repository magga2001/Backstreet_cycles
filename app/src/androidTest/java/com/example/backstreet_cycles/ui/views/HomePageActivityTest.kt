package com.example.backstreet_cycles.ui.views

//import android.view.KeyEvent
//import android.view.View
//import android.view.ViewGroup
//import androidx.arch.core.executor.testing.CountingTaskExecutorRule
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.Espresso.pressBack
//import androidx.test.espresso.IdlingRegistry
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
//import androidx.test.ext.junit.rules.ActivityScenarioRule
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import androidx.test.rule.GrantPermissionRule
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.common.Constants
//import com.example.backstreet_cycles.common.EspressoIdlingResource
//import com.example.backstreet_cycles.domain.adapter.StopsAdapter
//import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import org.hamcrest.Description
//import org.hamcrest.Matcher
//import org.hamcrest.Matchers
//import org.hamcrest.TypeSafeMatcher
//import org.hamcrest.core.AllOf.allOf
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//@HiltAndroidTest
//class HomePageActivityTest {
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
//            android.Manifest.permission.INTERNET
//        )
//
//    @get:Rule
//    val activityRule = ActivityScenarioRule(HomePageActivity::class.java)
//
//    @Before
//    fun setUp() {
//        hiltRule.inject()
//        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
//    }
//
//    @Test
//    fun test_map_on_homepage_is_displayed() {
//        onView(withId(R.id.homepage_mapView)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_bottom_sheet_is_displayed() {
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_drawer_layout_shown() {
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//    }
//
//    @Test
//    fun test_current_location_card_shown() {
////        onView(isRoot()).perform(waitFor(1000))
//        onView(
//            Matchers.allOf(
//                withId(R.id.homepage_LocationDataCardName),
//                withParent(withId(R.id.homepage_locationDataCardView))
//            )
//        ).check(matches(withText("Current Location")))
//        onView(withId(R.id.homepage_LocationDataCardName)).check(matches(withText("Current Location")))
//    }
//
////    @Test
////    fun navigation_drawer_shows_about_button() {
////        onView(withContentDescription(R.string.open)).perform(click())
////        onView(withId(R.id.about)).check(matches(isDisplayed()))
////    }
////
////    @Test
////    fun navigation_drawer_shows_faq_button() {
////        onView(withContentDescription(R.string.open)).perform(click())
////        onView(withId(R.id.faq)).check(matches(isDisplayed()))
////    }
//
//    @Test
//    fun navigation_drawer_shows_current_Journey_button() {
//        onView(withContentDescription(R.string.open)).perform(click())
//        onView(withId(R.id.currentJourney)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun navigation_drawer_shows_logout_button() {
//        onView(withContentDescription(R.string.open)).perform(click())
//        onView(withId(R.id.logout)).check(matches(isDisplayed()))
//    }
//
//
//    @Test
//    fun test_clicking_current_Journey_button(){
//        SharedPrefHelper.initialiseSharedPref(ApplicationProvider.getApplicationContext(),Constants.LOCATIONS)
//        SharedPrefHelper.clearSharedPreferences()
//        onView(withContentDescription(R.string.open)).perform(click())
//        onView(withId(R.id.currentJourney)).perform(click())
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//            .check(matches(withText("There is no current journey")))
//    }
//
//    @Test
//    fun test_add_stop_button_is_enabled(){
//        onView(withId(R.id.addingBtn)).check(matches(isEnabled()))
//
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
////        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.nextPageButton)).check(matches(isNotEnabled()))
//    }
//
//    @Test
//    fun test_nextPage_is_enabled(){
////        onView(isRoot()).perform(waitFor(1000))
//        addStop("Covent Garden")
//        //onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.nextPageButton)).check(matches(isEnabled()))
//    }
//
//
//    @Test
//    fun test_recyclerView_is_displayed(){
////        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.homepage_recyclerView)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_cardView_is_visible(){
////        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.homepage_locationDataCardView)).check(matches(isDisplayed()))
//        onView(
//            Matchers.allOf(
//                withId(R.id.homepage_locationDataCardView),
//                withParent(withId(R.id.homepage_recyclerView))
//            )).check(matches(isDisplayed()))
//   }
//
//
//    @Test
//    fun test_stop_added(){
//        //onView(isRoot()).perform(waitFor(1000))
//        addStop("covent garden")
////        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.nextPageButton)).check(matches(isEnabled()))
//        onView(withId(R.id.homepage_recyclerView)).check(matches(hasChildCount(2)))
//    }
//
//    @Test
//    fun test_go_to_next_page_from_home_page() {
//        addStop("Covent Garden")
//        onView(withId(R.id.nextPageButton)).check(matches(isEnabled()))
//        onView(withId(R.id.nextPageButton)).perform(click())
//        Intents.init()
//        intending(hasComponent(LoadingActivity::class.qualifiedName))
//        Intents.release()
//    }
//
//    //failing
////    @Test
////    fun test_cardView_is_swipeable() {
////       //onView(isRoot()).perform(waitFor(1000))
////        add_stop("covent garden")
////        add_stop("Buckingham Palace")
////        add_stop("Westminister")
////        add_stop("Stamford Bridge")
////
//////        onView(withId(R.id.homepage_recyclerView)).perform(
//////            RecyclerViewActions.actionOnItemAtPosition<StopsAdapter.StopViewHolder>(
//////                3,
//////                swipeLeft()
//////            )
//////        )
////
////        onView(
////            Matchers.allOf(
////                withId(R.id.homepage_recyclerView),
////                childAtPosition(
////                    withId(R.id.homepage_bottom_sheet_view),
////                    3
////                ),
////                withId(R.id.homepage_locationDataCardView),
////                withId(R.id.homepage_LocationDataCardName)
////            )
////        ).perform(
////            RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
////                Matchers.hasToString("Covent Garden"),
////                swipeLeft()
////            )
////            )
////
////    }
//
//
//    //failing
////    @Test
////    fun test_cardView_is_draggable() {
////        onView(isRoot()).perform(waitFor(1000))
////        add_stop("Westminister")
////        add_stop("Buckingham Palace")
////        add_stop("covent garden")
////        add_stop("Stamford Bridge")
////
////        onView(withId(R.id.homepage_recyclerView))
////            .perform(scrollToPosition<StopsAdapter.StopViewHolder>(5))
////            .check(matches(hasDescendant(withText("Covent Garden"))))
////
////        //onView(isRoot()).perform(waitFor(1000))
////        onView(withId(R.id.homepage_recyclerView)).perform(
////            RecyclerViewActions.actionOnItemAtPosition<StopsAdapter.StopViewHolder>(
////                5,
////                swipeUp()
////            )
////        )
////
////        //onView(isRoot()).perform(waitFor(1000))
////        onView(withId(R.id.homepage_recyclerView))
////            .perform(scrollToPosition<StopsAdapter.StopViewHolder>(4))
////            .check(matches(hasDescendant(withText("Covent Garden"))))
////    }
//
//
//    @Test
//    fun test_first_item_in_recycler_view_is_current_location(){
////        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.homepage_recyclerView))
//            .perform(scrollToPosition<StopsAdapter.StopViewHolder>(0))
//            .check(matches(hasDescendant(withText("Current Location"))))
//    }
//
//    @Test
//    fun test_next_page_button_disabled_when_one_item_in_recyclerView(){
////        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.homepage_recyclerView)).check(matches((hasChildCount(1))))
//        onView(withId(R.id.nextPageButton)).check(matches(isNotEnabled()))
//    }
//
//    //failing
//    @Test
//    fun test_next_page_button_enabled_when_more_than_one_item_in_recyclerView(){
//        addStop("Covent Garden")
////        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.nextPageButton)).check(matches(isEnabled()))
//    }
//
//
//
//    @Test
//    fun bottom_sheet_invoked_show_users_field() {
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        onView(withId(R.id.users)).check(matches(withText("Cyclists:")))
//    }
//
//    @Test
//    fun bottom_sheet_invoked_show_number_of_users_is_one() {
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        onView(withId(R.id.UserNumber)).check(matches(withText("1")))
//    }
//
//    @Test
//    fun bottom_sheet_invoked_show_increment_and_decrement_buttons() {
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        onView(withId(R.id.decrementButton)).check(matches(isDisplayed()))
//        onView(withId(R.id.incrementButton)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun increment_button_clicked_number_of_users_incremented_by_one() {
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        onView(withId(R.id.incrementButton)).perform(click())
//        onView(withId(R.id.UserNumber)).check(matches(withText("2")))
//    }
//
//    @Test
//    fun decrement_button_clicked_number_of_users_decremented_by_one() {
//        onView(withId(R.id.homepage_bottom_sheet_view)).check(matches(isDisplayed()))
//        onView(withId(R.id.incrementButton)).perform(click())
//        onView(withId(R.id.UserNumber)).check(matches(withText("2")))
//        onView(withId(R.id.decrementButton)).perform(click())
//        onView(withId(R.id.UserNumber)).check(matches(withText("1")))
//    }
//
//    @Test
//    fun back_button_from_ChangeEmailOrPasswordActivity_to_HomePageActivity() {
//        onView(withContentDescription(R.string.open)).perform(click())
//        onView(withId(R.id.changePassword)).perform(click())
//        Intents.init()
//        intending(hasComponent(ChangePasswordActivity::class.qualifiedName))
//        Intents.release()
//        pressBack()
//        Intents.init()
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//        Intents.release()
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
//
//    @Test
//    fun test_current_location_button_disabled_when_already_in_recyclerView(){
////        onView(isRoot()).perform(waitFor(1000))
//         onView(withId(R.id.myLocationButton)).check(matches(isNotEnabled()))
//    }
//
//
//    @Test
//    fun test_fail_to_delete_first_item_in_recyclerView() {
////        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.homepage_recyclerView)).perform(
//            RecyclerViewActions.actionOnItemAtPosition<StopsAdapter.StopViewHolder>(
//                0,
//                swipeLeft()
//            )
//        )
//        pressBack()
//        onView(withId(R.id.homepage_recyclerView))
//            .perform(scrollToPosition<StopsAdapter.StopViewHolder>(0))
//            .check(matches(hasDescendant(withText("Current Location"))))
//    }
//
//
//
//    //failing
//    @Test
//    fun test_location_is_changed_when_stop_is_clicked(){
//        onView(isRoot()).perform(waitFor(1000))
//        onView(
//            Matchers.allOf(
//                withId(R.id.homepage_recyclerView),
//                childAtPosition(
//                    withId(R.id.homepage_bottom_sheet_view),
//                    2
//                )
//            )
//        ).perform(
//            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
//                0,
//                click()
//            )
//        )
//
//        onView(isRoot()).perform(waitFor(1000))
//        val locationSearched = onView(
//            Matchers.allOf(
//                withId(R.id.edittext_search),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.searchView),
//                        0
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        ).perform(replaceText("covent garden"), closeSoftKeyboard())
//
//        onView(isRoot()).perform(waitFor(1000))
//        locationSearched.perform(pressKey(KeyEvent.KEYCODE_ENTER),  pressKey(KeyEvent.KEYCODE_ENTER))
//
//        onView(isRoot()).perform(waitFor(1000))
//        onView(
//            Matchers.allOf(
//                withId(R.id.homepage_LocationDataCardName),
//                withParent(withId(R.id.homepage_locationDataCardView))
//            )
//        ).check(matches(withText("Covent Garden")))
//
//    }
//
//    @Test
//    fun test_search_location_is_shown_when_add_stop_button_is_clicked(){
//
//        onView(withId(R.id.addingBtn)).perform(click())
//        onView(
//            allOf(
//                withId(R.id.edittext_search),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.searchView),
//                        0
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        ).check(matches(isDisplayed()))
//    }
//
//    //failing
//    @Test
//    fun test_goBackTo_homepage_when_back_clicked_from_autoCompleteAPI(){
//        onView(withId(R.id.addingBtn)).perform(click())
//        onView(isRoot()).perform(waitFor(1000))
//        onView(
//            allOf(
//                withId(R.id.button_search_back),
//                withContentDescription("Close Search"),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.searchView),
//                        0
//                    ),
//                    2
//                ),
//                isDisplayed()
//            )
//        ).perform(click())
//        onView(isRoot()).perform(waitFor(1000))
//        Intents.init()
//        intending(hasComponent(HomePageActivity::class.qualifiedName))
//        Intents.release()
//        onView(isRoot()).perform(waitFor(1000))
//        onView(withId(R.id.homePageActivity)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
//
//    }
//
////    @Test
////    fun test_fail_to_add_same_stop() {
////        //onView(isRoot()).perform(waitFor(1000))
////        add_stop("covent garden")
////        //onView(withId(R.id.homepage_recyclerView)).check(matches(hasChildCount(3)))
////
////        //onView(isRoot()).perform(waitFor(1000))
////        add_stop("covent garden")
////        onView(withId(R.id.homepage_recyclerView)).check(matches(hasChildCount(3)))
////    }
//
//
//   @Test
//   fun test_Snackbar_location_already_in_list(){
//       addStop("Covent Garden")
//       //onView(isRoot()).perform(waitFor(1000))
//       addStop("Covent Garden")
//
//       onView(withId(com.google.android.material.R.id.snackbar_text))
//           .check(matches(withText("Location already in list")))
//   }
//
//    @Test
//    fun test_Snackbar_cannot_have_more_than_four_users(){
//        for(i in 1..5){
//            onView(withId(R.id.incrementButton)).perform(click())
//        }
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//            .check(matches(withText("Cannot have more than 4 users")))
//    }
//
//    @Test
//    fun  test_Snackbar_cannot_have_less_than_one_user(){
//        onView(withId(R.id.decrementButton)).perform(click())
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//            .check(matches(withText("Cannot have less than one user")))
//    }
//
//    @Test
//    fun test_Snackbar_adding_stop(){
//        addStop("Covent Garden")
//        onView(isRoot()).perform(waitFor(500))
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//            .check(matches(withText("Adding Stop")))
//    }
//
//    @Test
//    fun test_Snackbar_changing_location_of_stop(){
//        test_location_is_changed_when_stop_is_clicked()
//        onView(withId(com.google.android.material.R.id.snackbar_text))
//            .check(matches(withText("Changing Location Of Stop")))
//    }
//
//
//    @Test
//    fun test_bottom_sheet_collapse(){
//        test_location_is_changed_when_stop_is_clicked()
//        onView(withId(R.id.myLocationButton)).perform(click())
//        Matchers.allOf(
//            withId(R.id.homepage_bottom_sheet_constraintLayout),
//            childAtPosition(
//                withId(R.id.homepage_bottom_sheet_linearLayout),
//                0
//            )
//        ).matches(hasBottomSheetBehaviorState(BottomSheetBehavior.STATE_COLLAPSED))
//    }
//
//    private fun waitFor(delay: Long): ViewAction {
//        return object : ViewAction {
//            override fun getConstraints(): Matcher<View> = isRoot()
//            override fun getDescription(): String = "wait for $delay milliseconds"
//            override fun perform(uiController: UiController, v: View?) {
//                uiController.loopMainThreadForAtLeast(delay)
//            }
//        }
//    }
//
//    private fun getFriendlyBottomSheetBehaviorStateDescription(state: Int): String = when (state) {
//        BottomSheetBehavior.STATE_DRAGGING -> "dragging"
//        BottomSheetBehavior.STATE_SETTLING -> "settling"
//        BottomSheetBehavior.STATE_EXPANDED -> "expanded"
//        BottomSheetBehavior.STATE_COLLAPSED -> "collapsed"
//        BottomSheetBehavior.STATE_HIDDEN -> "hidden"
//        BottomSheetBehavior.STATE_HALF_EXPANDED -> "half-expanded"
//        else -> "unknown but the value was $state"
//    }
//
//    private fun hasBottomSheetBehaviorState(expectedState: Int): Matcher<in View>? {
//        return object : BoundedMatcher<View, View>(View::class.java) {
//            override fun describeTo(description: Description) {
//                description.appendText("has BottomSheetBehavior state: ${getFriendlyBottomSheetBehaviorStateDescription(expectedState)}")
//            }
//
//            override fun matchesSafely(view: View): Boolean {
//                val bottomSheetBehavior = BottomSheetBehavior.from(view)
//                return expectedState == bottomSheetBehavior.state
//            }
//        }
//    }
////    private fun setBottomSheetBehaviorState(desiredState: Int): Matcher<in View>? {
////        return object : BoundedMatcher<View, View>(View::class.java) {
////            override fun describeTo(description: Description) {
////                description.appendText("has BottomSheetBehavior state: ${getFriendlyBottomSheetBehaviorStateDescription(desiredState)}")
////            }
////
////            override fun matchesSafely(view: View): Boolean {
////                val bottomSheetBehavior = BottomSheetBehavior.from(view)
////                return bottomSheetBehavior.state = desiredState
////            }
////        }
////    }
////
////    private fun setBottomSheetStateAction(desiredState: Int) {
////            fun getConstraints(): Matcher<View> {
////                return Matchers.any(View::class.java)
////            }
////
////            fun perform(uiController: UiController, view: View) {
////                val bottomSheetBehavior = BottomSheetBehavior.from(view)
////                bottomSheetBehavior.state = desiredState
////            }
////
////             fun getDescription(): String = "Set BottomSheet to state: $desiredState"
////        }
////    }
//
//    private fun addStop(name: String) {
//        onView(isRoot()).perform(waitFor(1000))
//        val addStopButton = onView(
//            Matchers.allOf(
//                withId(R.id.addingBtn), withText("Add Stop"),
//                childAtPosition(
//                    Matchers.allOf(
//                        withId(R.id.homepage_bottom_sheet_constraintLayout),
//                        childAtPosition(
//                            withId(R.id.homepage_bottom_sheet_linearLayout),
//                            0
//                        )
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        ).perform(click())
//
//        onView(isRoot()).perform(waitFor(1000))
//        val location = onView(
//            Matchers.allOf(
//                withId(R.id.edittext_search),
//                childAtPosition(
//                    childAtPosition(
//                        withId(R.id.searchView),
//                        0
//                    ),
//                    0
//                ),
//                isDisplayed()
//            )
//        )
//        onView(isRoot()).perform(waitFor(1000))
//        location.perform(replaceText(name))
//        onView(isRoot()).perform(waitFor(1000))
//        location.perform(pressKey(KeyEvent.KEYCODE_ENTER),  pressKey(KeyEvent.KEYCODE_ENTER))
//
//    }
//
//    private fun childAtPosition(
//        parentMatcher: Matcher<View>, position: Int): Matcher<View> {
//
//        return object : TypeSafeMatcher<View>() {
//            override fun describeTo(description: Description) {
//                description.appendText("Child at position $position in parent ")
//                parentMatcher.describeTo(description)
//            }
//
//            public override fun matchesSafely(view: View): Boolean {
//                val parent = view.parent
//                return parent is ViewGroup && parentMatcher.matches(parent)
//                        && view == parent.getChildAt(position)
//            }
//        }
//    }
//
//    @After
//    fun tearDown(){
//        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
//    }
//
//}