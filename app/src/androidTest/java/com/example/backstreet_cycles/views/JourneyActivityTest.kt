package com.example.backstreet_cycles.views

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.viewModel.LogInRegisterViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class JourneyActivityTest{

    lateinit var logInRegisterViewModel:LogInRegisterViewModel

    @Before
    fun setUp() {
        logInRegisterViewModel= LogInRegisterViewModel(Application())
        logInRegisterViewModel.login("msjanbey@gmail.com","123456")
        ActivityScenario.launch(JourneyActivity::class.java)
        onView(ViewMatchers.withContentDescription(R.string.open)).perform(ViewActions.click())

    }
//    @Test
//    fun test_journey_activity_is_visible(){
//        val activityScenario= ActivityScenario.launch(JourneyActivity::class.java)
//        onView(withId(R.id)).check(matches(isDisplayed()))
//    }
}