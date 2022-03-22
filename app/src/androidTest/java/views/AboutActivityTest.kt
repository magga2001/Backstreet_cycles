package views
//
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.Espresso.pressBack
//import androidx.test.espresso.action.ViewActions.click
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.views.HomePageActivity
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//class AboutActivityTest {
//
//    @Before
//    fun setUp() {
//        ActivityScenario.launch(HomePageActivity::class.java)
//        onView(withId(R.id.nav_view)).perform(click())
//    }
//    @Test
//    fun test_about_button_to_AboutActivity() {
//
//        onView(withId(R.id.about)).perform(click())
//        onView(withId(R.id.aboutActivity)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_title_is_displayed() {
//
//        onView(withId(R.id.about)).perform(click())
//        onView(withId(R.id.aboutTitle)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_description_is_displayed() {
//
//        onView(withId(R.id.about)).perform(click())
//        onView(withId(R.id.aboutDescription)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun test_on_pressBack_go_to_HomePageActivity() {
//
//        onView(withId(R.id.about)).perform(click())
//        pressBack()
//        onView(withId(R.id.HomePageActivity)).check(matches(isDisplayed()))
//    }
//}