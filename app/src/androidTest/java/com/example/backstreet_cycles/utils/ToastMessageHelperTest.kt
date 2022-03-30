package com.example.backstreet_cycles.utils
//
//import android.app.Application
//import androidx.test.core.app.ActivityScenario
//import com.example.backstreet_cycles.domain.utils.ToastMessageHelper
//import com.example.backstreet_cycles.ui.views.LogInActivity
//import kotlinx.android.synthetic.main.activity_log_in.*
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.junit.runners.JUnit4
//
//
//@RunWith(JUnit4::class)
//class ToastMessageHelperTest {
//
//    @Test
//    fun test() {
//        ActivityScenario.launch(LogInActivity::class.java).onActivity { activity ->
//            activity.log_in_button.performClick()
//            assertThat(
//                ToastMessageHelper.createToastMessage(Application(), "Test").toString(),
//                equalTo("Test")
//            )
//        }
//    }
//}