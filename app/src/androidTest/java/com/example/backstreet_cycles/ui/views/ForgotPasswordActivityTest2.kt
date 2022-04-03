package com.example.backstreet_cycles.ui.views


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.example.backstreet_cycles.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ForgotPasswordActivityTest2 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashScreenActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        )

    @Test
    fun forgotPasswordActivityTest2() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.log_in_email),
                childAtPosition(
                    allOf(
                        withId(R.id.log_in_LinearLayout),
                        childAtPosition(
                            withId(R.id.logInActivity),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText(""), closeSoftKeyboard())

        val materialTextView = onView(
            allOf(
                withId(R.id.log_in_clickForgotPassword), withText("Forgot Password?"),
                childAtPosition(
                    allOf(
                        withId(R.id.log_in_LinearLayout),
                        childAtPosition(
                            withId(R.id.logInActivity),
                            2
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialTextView.perform(click())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.forgot_password_email),
                childAtPosition(
                    allOf(
                        withId(R.id.forgot_password_constraintLayout),
                        childAtPosition(
                            withId(R.id.forgotPasswordActivity),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("arjunkhanna945@gmail.com"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.forgot_password_SendPasswordReset_button),
                withText("Send Password Reset"),
                childAtPosition(
                    allOf(
                        withId(R.id.forgotPasswordActivity),
                        childAtPosition(
                            withId(android.R.id.content),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val appCompatImageButton = onView(
            allOf(
                withContentDescription("Navigate up"),
                childAtPosition(
                    allOf(
                        withId(R.id.action_bar),
                        childAtPosition(
                            withId(R.id.action_bar_container),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageButton.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
