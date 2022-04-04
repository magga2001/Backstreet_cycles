package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import com.example.backstreet_cycles.ui.viewModel.ForgotPasswordViewModel
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import io.mockk.mockk
import junit.framework.Assert
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(application = BackstreetApplication::class, manifest = Config.NONE)
@ExperimentalCoroutinesApi
class ForgotPasswordViewModelTest {
    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl

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
        forgotPasswordViewModel = ForgotPasswordViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            context
        )

        signUpViewModel.register("John","Doe","johndoe@example.com","123456")
        Assert.assertEquals(
            "Email verification sent",
            signUpViewModel.getMessage().getOrAwaitValue()
        )
        fakeUserRepoImpl.verifyEmail("johndoe@example.com")
    }

    @Test
    fun test_email_verification_sent_with_correct_email(){
        forgotPasswordViewModel.resetPassword("johndoe@example.com")
        Assert.assertEquals(
            "Password reset link sent to email.",
            forgotPasswordViewModel.getResetPassword().getOrAwaitValue()
        )
    }

    @Test
    fun test_email_verification_not_sent_with_incorrect_email(){
        forgotPasswordViewModel.resetPassword("testuser@random.com")
        Assert.assertEquals(
            "There is no user record corresponding to this identifier. The user may have been deleted.",
            forgotPasswordViewModel.getResetPassword().getOrAwaitValue()
        )
    }

}