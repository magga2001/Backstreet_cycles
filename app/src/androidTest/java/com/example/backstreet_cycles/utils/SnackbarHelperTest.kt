package com.example.backstreet_cycles.utils

import android.view.View
import androidx.constraintlayout.utils.widget.MockView
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import com.example.backstreet_cycles.domain.utils.SnackbarHelper
import com.example.backstreet_cycles.ui.viewModel.BaseViewModel
import com.example.backstreet_cycles.ui.views.HomePageActivity
import com.google.android.material.snackbar.Snackbar
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock

//
//@RunWith(JUnit4::class)
//class SnackbarHelperTest {
//
//    private val myView: MockView = mock()
//    /*@Test
//    fun test() {
//        ActivityScenario.launch(HomePageActivity::class.java).onActivity { activity ->
//            activity.addingBtn.performClick()
//            assertThat(
//                SnackbarHelper.displaySnackbar(HomePageActivi,"Profile Updated Successfully").toString(),
//                        equalTo("Profile Updated Successfully")
//            )
//        }
//    }*/
//
//    /*@Test
//    fun test() {
//
//        assertEquals(SnackbarHelper.displaySnackbar(myView,"Test"),
//            myView?.let { Snackbar.make(it, "Test", Snackbar.LENGTH_LONG) })
//    }*/
//
//    @Test
//    fun test() {
//
//        val text = SnackbarHelper.displaySnackbar(myView, "Test").toString()
//        val textExpected = "Test"
//        assertEquals(textExpected, text)
//    }
//
//    /*@Test
//    fun test2() {
//        ActivityScenario.launch(HomePageActivity::class.java)
//        val textToType = SnackbarHelper.displaySnackbar(HomePageActivity.findViewById(R.string.profile_updated), "Profile updated successfully").toString()
//        onView(
//            allOf(
//                withId(R.string.profile_updated),
//                withText(textToType)
//            )
//        ).check(matches(isDisplayed()))
//    }*/
//}
