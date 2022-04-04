package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import io.mockk.mockk
import junit.framework.Assert.assertEquals
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

class SignUpViewModelTest {

    private lateinit var signUpViewModel: SignUpViewModel

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

        signUpViewModel = SignUpViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            context
        )
    }

    @Test
    fun test_sign_up_user_success_with_valid_email_and_password() = runBlocking{
        signUpViewModel.register("John","Doe","johndoe@example.com","123456")
        assertEquals("Email verification sent", signUpViewModel.getMessage().getOrAwaitValue())
    }

    @Test
    fun test_sign_up_user_fail_with_invalid_email_and_password() = runBlocking {
        signUpViewModel.register("John","Doe","johndoe@abc.com","12345")
        assertEquals("Password is too short", signUpViewModel.getMessage().getOrAwaitValue())
    }

    @Test
    fun test_sign_up_user_fail_with_valid_email_and_invalid_password() = runBlocking {
        signUpViewModel.register("John","Doe","johndoe@example.com","12345")
        assertEquals("Password is too short", signUpViewModel.getMessage().getOrAwaitValue())
    }

    @Test
    fun test_sign_up_user_fail_with_invalid_email_and_valid_password() = runBlocking {
        signUpViewModel.register("John","Doe","johndoe@abc.com","123456")
        assertEquals("Invalid email, cannot send email", signUpViewModel.getMessage().getOrAwaitValue())
    }

    @Test
    fun test_sign_up_with_existed_account() = runBlocking {
        signUpViewModel.register("John","Doe","johndoe@example.com","123456")
        signUpViewModel.register("John","Doe","johndoe@example.com","123456")
        assertEquals("Email already existed", signUpViewModel.getMessage().getOrAwaitValue())
    }

    @Test
    fun test_sign_up_two_accounts() = runBlocking {
        signUpViewModel.register("John","Doe","johndoe@example.com","123456")
        assertEquals("Email verification sent", signUpViewModel.getMessage().getOrAwaitValue())
        signUpViewModel.register("Jane","Doe","janedoe@example.com","123456")
        assertEquals("Email verification sent", signUpViewModel.getMessage().getOrAwaitValue())
    }
}