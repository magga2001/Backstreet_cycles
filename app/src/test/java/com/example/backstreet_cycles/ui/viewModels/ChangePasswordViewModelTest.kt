package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

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
        signUpViewModel.register("John","Doe","johndoe@example.com","123456")
        assert("Email verification sent"== signUpViewModel.getMessage().getOrAwaitValue())
        fakeUserRepoImpl.verifyEmail("johndoe@example.com")
        logInViewModel.login("johndoe@example.com", "123456")
        assert(logInViewModel.getFirebaseUserData().getOrAwaitValue())
        assert(fakeUserRepoImpl.getCurrentUser() != null)
    }

    @Test
    fun test_password_not_updated_if_currentPassword_is_wrong_and_newPassword_less_than_6()= runBlocking{
        changePasswordViewModel.updatePassword(wrongCurrentPassword, wrongNewPassword)
        assert("Password update fail"== changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
    }

    @Test
    fun test_password_not_updated_if_wrong_currentPassword()= runBlocking{
        changePasswordViewModel.updatePassword(wrongCurrentPassword, newPassword)
        assert("Password update fail"== changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
 }

    @Test
    fun test_password_not_updated_if_newPassword_less_than_6()= runBlocking{
        changePasswordViewModel.updatePassword(password, wrongNewPassword)
        assert("Password update fail"== changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
    }

    @Test
    fun test_password_not_updated_if_currentPassword_is_empty()= runBlocking{
        changePasswordViewModel.updatePassword("", wrongNewPassword)
        assert("Password update fail"==changePasswordViewModel.getUpdateDetail().getOrAwaitValue())
    }

    @Test
    fun test_password_not_updated_if_newPassword_is_empty()= runBlocking{
        changePasswordViewModel.updatePassword(password, "")
        assert("Password update fail" == changePasswordViewModel.getUpdateDetail().getOrAwaitValue().toString())
    }


    @Test
    fun test_password_updated_if_newPassword_is_correct()= runBlocking{
        changePasswordViewModel.updatePassword(password, "1213123213213123")
        assert("Password successfully updated."== changePasswordViewModel.getUpdateDetail().getOrAwaitValue())

    }
}