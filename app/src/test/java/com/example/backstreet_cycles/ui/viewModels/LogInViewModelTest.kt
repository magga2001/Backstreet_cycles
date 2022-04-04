package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
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
class LogInViewModelTest {

    private lateinit var logInViewModel: LogInViewModel
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

        logInViewModel = LogInViewModel(
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
    }

    @Test
    fun test_log_in_with_verified_email() = runBlocking {
        register()
        fakeUserRepoImpl.verifyEmail("johndoe@example.com")
        logInViewModel.login("johndoe@example.com", "123456")
        assertEquals(true, logInViewModel.getFirebaseUserData().getOrAwaitValue())
    }

    @Test
    fun test_log_in_with_unverified_email() = runBlocking {
        register()
        logInViewModel.login("johndoe@example.com", "123456")
        assertEquals("Please verify your email", logInViewModel.getErrorMessageData().getOrAwaitValue())
    }

    @Test
    fun test_log_in_with_unregistered_email() = runBlocking {
        logInViewModel.login("johndoe@example.com", "123456")
        assertEquals("No User", logInViewModel.getErrorMessageData().getOrAwaitValue())
    }

    @Test
    fun test_check_if_the_user_has_logged_in() = runBlocking {
        register()
        fakeUserRepoImpl.verifyEmail("johndoe@example.com")
        logInViewModel.login("johndoe@example.com", "123456")
        assertEquals(true, logInViewModel.getFirebaseUserData().getOrAwaitValue())
        assert(fakeUserRepoImpl.getCurrentUser() != null)
    }

    fun register(){
        signUpViewModel.register("John","Doe","johndoe@example.com","123456")
        assertEquals("Email verification sent", signUpViewModel.getMessage().getOrAwaitValue())
    }
}