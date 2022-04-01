package com.example.backstreet_cycles.ui.viewModels


import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import app.cash.turbine.test
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.ui.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.ui.viewModel.BaseViewModel
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import com.google.common.truth.Truth
import dagger.hilt.android.internal.Contexts
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.security.auth.login.LoginException

@RunWith(RobolectricTestRunner::class)
@Config(application = BackstreetApplication::class, manifest = Config.NONE)
@ExperimentalCoroutinesApi
class ChangePasswordViewModelTest {

    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var logInViewModel: LogInViewModel

    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl
    val password = "123456"
    val newPassword = "654321"
    val wrongCurrentPassword = "wrongPassword"
    val wrongNewPassword = "12345"

    @Before
    fun setUp() {
        val context = mockk<Context>(relaxed = true)
        val application = mockk<BackstreetApplication>(relaxed = true)

        fakeTflRepoImpl = FakeTflRepoImpl()
        fakeMapboxRepoImpl = FakeMapboxRepoImpl()
        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
        fakeUserRepoImpl = FakeUserRepoImpl()

        changePasswordViewModel = ChangePasswordViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            context
        )
        signUpViewModel = SignUpViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            context
        )
        logInViewModel = LogInViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            context
        )
    }

    @Test
    fun test_password_not_updated_if_currentPassword_is_wrong_and_newPassword_less_than_6()= runBlocking{
        signUpViewModel.register("Test", "User","testuser@random.com",password)
        fakeUserRepoImpl.verifyEmail("testuser@random.com")
        logInViewModel.login("testuser@random.com", password)
        changePasswordViewModel.updatePassword(wrongCurrentPassword, wrongNewPassword)
        assertEquals("Fail to update password", changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
    }

    @Test
    fun test_password_not_updated_if_wrong_currentPassword()= runBlocking{
        signUpViewModel.register("Test", "User","testuser@random.com",password)
        fakeUserRepoImpl.verifyEmail("testuser@random.com")
        logInViewModel.login("testuser@random.com", password)
        changePasswordViewModel.updatePassword(wrongCurrentPassword, newPassword)
        assertEquals("Fail to update password", changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
 }

    @Test
    fun test_password_not_updated_if_newPassword_less_than_6()= runBlocking{
        signUpViewModel.register("Test", "User","testuser@random.com",password)
        fakeUserRepoImpl.verifyEmail("testuser@random.com")
        logInViewModel.login("testuser@random.com", password)
        changePasswordViewModel.updatePassword(password, wrongNewPassword)
        assertEquals("Fail to update password", changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
    }
    @Test
    fun test_password_not_updated_if_currentPassword_is_empty()= runBlocking{
        signUpViewModel.register("Test", "User","testuser@random.com",password)
        fakeUserRepoImpl.verifyEmail("testuser@random.com")
        logInViewModel.login("testuser@random.com", password)
        changePasswordViewModel.updatePassword("", wrongNewPassword)
        assertEquals("Fail to update password", changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
    }

    @Test
    fun test_password_not_updated_if_newPassword_is_empty()= runBlocking{
        signUpViewModel.register("Test", "User","testuser@random.com",password)
        fakeUserRepoImpl.verifyEmail("testuser@random.com")
        logInViewModel.login("testuser@random.com", password)
        changePasswordViewModel.updatePassword(password, "")
        assertEquals("Fail to update password", changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
    }

    @Test
    fun test_password_updated_if_newPassword_is_correct()= runBlocking{
        signUpViewModel.register("Test", "User","testuser@random.com",password)
        fakeUserRepoImpl.verifyEmail("testuser@random.com")
        logInViewModel.login("testuser@random.com", password)
        changePasswordViewModel.updatePassword(password, "1213123213213123")
        assertEquals("Password updated Successfully", changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
    }
}