package views
//
//import android.app.Application
//import androidx.test.core.app.ActivityScenario
//import androidx.test.espresso.Espresso
//import androidx.test.espresso.action.ViewActions
//import androidx.test.espresso.assertion.ViewAssertions
//import androidx.test.espresso.matcher.ViewMatchers
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import com.example.backstreet_cycles.R
//import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
//import com.example.backstreet_cycles.views.HomePageActivity
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//
//@RunWith(AndroidJUnit4ClassRunner::class)
//class FAQActivityTest {
//
//    @Before
//    fun setUp() {
//
//        ActivityScenario.launch(HomePageActivity::class.java)
//        Espresso.onView(withId(R.id.nav_view)).perform(ViewActions.click())
//    }
//
//    @Test
//    fun test_about_help_to_FAQActivity() {
//
//        Espresso.onView(withId(R.id.faq)).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.FAQ_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun test_FAQtitle_is_displayed() {
//
//        Espresso.onView(withId(R.id.FAQ_activity)).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.faq_title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun test_title1_is_displayed() {
//        Espresso.onView(withId(R.id.FAQ_activity)).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.title1)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun test_text1_is_displayed() {
//
//        Espresso.onView(ViewMatchers.withId(R.id.FAQ_activity)).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.text1)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun test_title2_is_displayed() {
//
//        Espresso.onView(withId(R.id.FAQ_activity)).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.title2)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun test_text2_is_displayed() {
//
//        Espresso.onView(withId(R.id.FAQ_activity)).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.text2)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun test_title3_is_displayed() {
//
//        Espresso.onView(ViewMatchers.withId(R.id.FAQ_activity)).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.title3)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun test_text3_is_displayed() {
//
//        Espresso.onView(ViewMatchers.withId(R.id.FAQ_activity)).perform(ViewActions.click())
//        Espresso.onView(withId(R.id.text3)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    fun test_on_pressBack_go_to_HomePageActivity() {
//
//        Espresso.onView(ViewMatchers.withId(R.id.help)).perform(ViewActions.click())
//        Espresso.pressBack()
//        Espresso.onView(ViewMatchers.withId(R.id.HomePageActivity))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }
//}