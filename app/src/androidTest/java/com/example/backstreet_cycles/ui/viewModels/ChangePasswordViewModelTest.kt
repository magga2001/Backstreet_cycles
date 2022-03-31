package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.FakeCyclistRepoImpl
import com.example.backstreet_cycles.FakeMapboxRepoImpl
import com.example.backstreet_cycles.FakeTflRepoImpl
import com.example.backstreet_cycles.FakeUserRepoImpl
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import dagger.hilt.android.internal.Contexts
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config

//@RunWith(RobolectricTestRunner::class)
//@Config(application = BackstreetApplication::class, manifest = Config.NONE)
@RunWith(AndroidJUnit4ClassRunner::class)

class ChangePasswordViewModelTest {

    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var instrumentationContext: Context
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)

    @Before
     fun setUp()
    {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        val application = Contexts.getApplication(instrumentationContext)
        changePasswordViewModel = ChangePasswordViewModel(
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(),
            FakeCyclistRepoImpl(),
            FakeUserRepoImpl(),
            application,
            instrumentationContext
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