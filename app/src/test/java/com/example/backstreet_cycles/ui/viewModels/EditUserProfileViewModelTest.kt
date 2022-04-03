package com.example.backstreet_cycles.ui.viewModels
//
//import android.content.Context
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import com.example.backstreet_cycles.common.BackstreetApplication
//import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
//import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
//import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
//import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
//import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
//import com.example.backstreet_cycles.ui.viewModel.*
//import dagger.hilt.android.internal.Contexts
//import io.mockk.mockk
//import junit.framework.Assert
//import junit.framework.Assert.assertEquals
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config
//
//@RunWith(RobolectricTestRunner::class)
//@Config(application = BackstreetApplication::class, manifest = Config.NONE)
//@ExperimentalCoroutinesApi
//class EditUserProfileViewModelTest {
//    private lateinit var editUserProfileViewModel: EditUserProfileViewModel
//    private lateinit var signUpViewModel: SignUpViewModel
//    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
//    private lateinit var logInViewModel: LogInViewModel
//
//    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
//    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
//    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
//    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl
//
//    @Before
//    fun setUp() {
//        val context = mockk<Context>(relaxed = true)
//        val application = mockk<BackstreetApplication>(relaxed = true)
//
//        fakeTflRepoImpl = FakeTflRepoImpl()
//        fakeMapboxRepoImpl = FakeMapboxRepoImpl()
//        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
//        fakeUserRepoImpl = FakeUserRepoImpl()
//
//        editUserProfileViewModel = EditUserProfileViewModel(
//                fakeTflRepoImpl,
//                fakeMapboxRepoImpl,
//                fakeCyclistRepoImpl,
//                fakeUserRepoImpl,
//                application,
//                context
//        )
//
//        signUpViewModel = SignUpViewModel(
//                fakeTflRepoImpl,
//                fakeMapboxRepoImpl,
//                fakeCyclistRepoImpl,
//                fakeUserRepoImpl,
//                application,
//                context
//        )
//        forgotPasswordViewModel = ForgotPasswordViewModel(
//                fakeTflRepoImpl,
//                fakeMapboxRepoImpl,
//                fakeCyclistRepoImpl,
//                fakeUserRepoImpl,
//                application,
//                context
//        )
//        logInViewModel = LogInViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            application,
//            context
//        )
//        signUpViewModel.register("John","Doe","johndoe@example.com","123456")
//        assertEquals("Email verification sent", signUpViewModel.getMessage().getOrAwaitValue())
//        fakeUserRepoImpl.verifyEmail("johndoe@example.com")
//    }
//
//    @Test
//    fun test_update_lastName_unsuccessful_without_logging_in() {
//        fakeUserRepoImpl.logOut()
//        editUserProfileViewModel.updateUserDetails("test","testLastName")
//        assertEquals(
//                "No User",
//                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
//        )
//    }
//
//    @Test
//    fun test_update_firstName_unsuccessful_without_logging_in() {
//        fakeUserRepoImpl.logOut()
//        editUserProfileViewModel.updateUserDetails("testFirstName","user")
//        assertEquals(
//                "No User",
//                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
//        )
//    }
//    @Test
//    fun test_update_lastName_unsuccessful_without_loggin_in() {
//        fakeUserRepoImpl.logOut()
//        editUserProfileViewModel.updateUserDetails("test","testLastName")
//        assertEquals(
//                "No User",
//                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
//        )
//    }
//
//    @Test
//    fun test_update_firstName_successfully()
//    {
//        logInViewModel.login("johndoe@example.com", "123456")
//        assertEquals(true, logInViewModel.getFirebaseUserMutableLiveData().getOrAwaitValue())
//        assert(fakeUserRepoImpl.getCurrentUser() != null)
//        editUserProfileViewModel.updateUserDetails("testFirstName","user")
//        assertEquals(
//                "Success",
//                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
//        )
//        assertEquals(
//                "testFirstName",
//                fakeUserRepoImpl.getCurrentUser()?.firstName
//        )
//    }
//
//    @Test
//    fun test_update_lastName_successfully()
//    {
//        logInViewModel.login("johndoe@example.com", "123456")
//        assertEquals(true, logInViewModel.getFirebaseUserMutableLiveData().getOrAwaitValue())
//        assert(fakeUserRepoImpl.getCurrentUser() != null)
//        editUserProfileViewModel.updateUserDetails("test","testLastName")
//        assertEquals(
//                "Success",
//                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
//        )
//        Assert.assertEquals(
//                "testLastName",
//                fakeUserRepoImpl.getCurrentUser()?.lastName
//        )
//    }
//
//    @Test
//    fun test_update_lastName_and_first_name_successfully()
//    {
//        logInViewModel.login("johndoe@example.com", "123456")
//        assertEquals(true, logInViewModel.getFirebaseUserMutableLiveData().getOrAwaitValue())
//        assert(fakeUserRepoImpl.getCurrentUser() != null)
//        editUserProfileViewModel.updateUserDetails("testFirstName","testLastName")
//        assertEquals(
//                "Success",
//                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
//        )
//        assertEquals(
//                "testFirstName",
//                fakeUserRepoImpl.getCurrentUser()?.firstName
//        )
//        assertEquals(
//                "testLastName",
//                fakeUserRepoImpl.getCurrentUser()?.lastName
//        )
//    }
//}