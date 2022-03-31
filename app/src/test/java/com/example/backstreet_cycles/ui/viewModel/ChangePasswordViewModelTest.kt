package com.example.backstreet_cycles.ui.viewModel

import android.content.Context
import com.example.backstreet_cycles.FakeCyclistRepoImpl
import com.example.backstreet_cycles.FakeMapboxRepoImpl
import com.example.backstreet_cycles.FakeTflRepoImpl
import com.example.backstreet_cycles.FakeUserRepoImpl
import com.example.backstreet_cycles.common.BackstreetApplication
import com.google.common.truth.Truth
import io.mockk.mockk
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = BackstreetApplication::class, manifest = Config.NONE)
class ChangePasswordViewModelTest {

    private lateinit var changePasswordViewModel: ChangePasswordViewModel
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)

    @Before
     fun setUp()
    {
//        hiltRule.inject()
        val context = mockk<Context>(relaxed = true)
        val application = mockk<BackstreetApplication>(relaxed = true)
        changePasswordViewModel = ChangePasswordViewModel(
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(),
            FakeCyclistRepoImpl(),
            FakeUserRepoImpl(),
            application,
            context
        )
    }

    @Test
    fun test_password_not_updated_if_wrong_currentPassword(){

    }

    @Test
    fun test_password_updated_if_right_currentPassword(){

    }

    @Test
    fun test_password_not_updated_if_newPassword_less_than_6(){

    }
    @Test
    fun test_password_not_updated_if_newPassword_is_correct(){

    }

    fun tearDown() {

    }
}