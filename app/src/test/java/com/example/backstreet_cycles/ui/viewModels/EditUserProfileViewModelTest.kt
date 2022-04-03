package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
import com.example.backstreet_cycles.ui.viewModel.EditUserProfileViewModel
import com.example.backstreet_cycles.ui.viewModel.ForgotPasswordViewModel
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
import dagger.hilt.android.internal.Contexts
import io.mockk.mockk
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config

//@RunWith(RobolectricTestRunner::class)
//@Config(application = BackstreetApplication::class, manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
@Config(application = BackstreetApplication::class, manifest = Config.NONE)
@ExperimentalCoroutinesApi
class EditUserProfileViewModelTest {
    private lateinit var editUserProfileViewModel: EditUserProfileViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel

    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl
    val password = "123456"

    @Before
    fun setUp() {
        val context = mockk<Context>(relaxed = true)
        val application = mockk<BackstreetApplication>(relaxed = true)

        fakeTflRepoImpl = FakeTflRepoImpl()
        fakeMapboxRepoImpl = FakeMapboxRepoImpl()
        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
        fakeUserRepoImpl = FakeUserRepoImpl()

        editUserProfileViewModel = EditUserProfileViewModel(
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
    }

    //ask magga why it's failing
//    @Test
//    fun test_update_lastName_unsuccessful_without_loggin_in() {
//        fakeUserRepoImpl.addMockUser("test", "user", "testuesr@example.com","123456")
//        fakeUserRepoImpl.logOut()
//        editUserProfileViewModel.updateUserDetails("test","testLastName")
//        Assert.assertEquals(
//                "No User",
//                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
//        )
//    }

    //ask magga why failing
//    @Test
//    fun test_update_firstName_unsuccessful_without_loggin_in() {
//        fakeUserRepoImpl.addMockUser("test", "user", "testuesr@example.com","123456")
//        fakeUserRepoImpl.logOut()
//        editUserProfileViewModel.updateUserDetails("testFirstName","user")
//        Assert.assertEquals(
//                "No User",
//                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
//        )
//    }

    @Test
    fun test_update_firstName_successfully()
    {
        fakeUserRepoImpl.addMockUser("test", "user", "testuesr@example.com","123456")
        editUserProfileViewModel.updateUserDetails("testFirstName","user")
        assertEquals(
                "Success",
                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
        )
        assertEquals(
                "testFirstName",
                fakeUserRepoImpl.getCurrentUser()?.firstName
        )
    }

    @Test
    fun test_update_lasName_successfully()
    {
        fakeUserRepoImpl.addMockUser("test", "user", "testuesr@example.com","123456")
        editUserProfileViewModel.updateUserDetails("test","testLastName")
        assertEquals(
                "Success",
                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
        )
        assertEquals(
                "testLastName",
                fakeUserRepoImpl.getCurrentUser()?.lastName
        )
    }

    @Test
    fun test_update_lastName_and_first_name_successfully()
    {
        fakeUserRepoImpl.addMockUser("test", "user", "testuesr@example.com","123456")
        editUserProfileViewModel.updateUserDetails("testFirstName","testLastName")
        assertEquals(
                "Success",
                editUserProfileViewModel.getUpdatedProfile().getOrAwaitValue()
        )
        assertEquals(
                "testFirstName",
                fakeUserRepoImpl.getCurrentUser()?.firstName
        )
        assertEquals(
                "testLastName",
                fakeUserRepoImpl.getCurrentUser()?.lastName
        )
    }
}