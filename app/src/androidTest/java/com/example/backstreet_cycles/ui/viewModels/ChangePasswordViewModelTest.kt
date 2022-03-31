package com.example.backstreet_cycles.ui.viewModels
//
//import android.content.Context
//import android.util.Log
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
//import com.example.backstreet_cycles.data.repository.FakeCyclistRepoImpl
//import com.example.backstreet_cycles.data.repository.FakeMapboxRepoImpl
//import com.example.backstreet_cycles.data.repository.FakeTflRepoImpl
//import com.example.backstreet_cycles.data.repository.FakeUserRepoImpl
//import com.example.backstreet_cycles.common.BackstreetApplication
//import com.example.backstreet_cycles.ui.viewModel.ChangePasswordViewModel
//import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
//import dagger.hilt.android.internal.Contexts
//import io.mockk.mockk
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
////import org.robolectric.RobolectricTestRunner
////import org.robolectric.annotation.Config
//
////@RunWith(RobolectricTestRunner::class)
////@Config(application = BackstreetApplication::class, manifest = Config.NONE)
//@RunWith(AndroidJUnit4ClassRunner::class)
//
//class ChangePasswordViewModelTest {
//
//    private lateinit var changePasswordViewModel: ChangePasswordViewModel
//
//    private lateinit var signUpViewModel: SignUpViewModel
////
////    @get:Rule
////    var hiltRule = HiltAndroidRule(this)
//
//    @Before
//    fun setUp()
//    {
////        hiltRule.inject()
//        val context = mockk<Context>(relaxed = true)
//        val application = mockk<BackstreetApplication>(relaxed = true)
//        changePasswordViewModel = ChangePasswordViewModel(
//            FakeTflRepoImpl(),
//            FakeMapboxRepoImpl(),
//            FakeCyclistRepoImpl(),
//            FakeUserRepoImpl(),
//            application,
//            context
//        )
//        signUpViewModel = SignUpViewModel(
//            FakeTflRepoImpl(),
//            FakeMapboxRepoImpl(),
//            FakeCyclistRepoImpl(),
//            FakeUserRepoImpl(),
//            application,
//            context
//        )
//    }
//
//    @Test
//    suspend fun test_password_not_updated_if_wrong_currentPassword(){
//        val password = "123456"
//        val newPassword = "654321"
//        val wrongCurrentPassword = "wrongPassword"
//        signUpViewModel.register("Test", "User","testuser@random.com",password)
//        Log.i("asdas",changePasswordViewModel.getUpdateDetail().toString())
//        changePasswordViewModel.updatePassword(wrongCurrentPassword, newPassword)
//        Log.i("asdas",changePasswordViewModel.getUpdateDetail().toString())
////        Truth.assertThat(changePasswordViewModel.getUpdateDetail()).isEqualTo(false)
//
//
//    }
//
//    @Test
//    suspend fun test_password_not_updated_if_newPassword_less_than_6(){
//        val password = "123456"
//        val newPassword = "654321"
//        val wrongNewPassword = "12345"
//
//        signUpViewModel.register("Test", "User","testuser@random.com",password)
//        Log.i("asdas",changePasswordViewModel.getUpdateDetail().toString())
//        changePasswordViewModel.updatePassword(password, wrongNewPassword)
//        Log.i("asdas",changePasswordViewModel.getUpdateDetail().toString())
////        Truth.assertThat(changePasswordViewModel.getUpdateDetail()).isEqualTo(false)
//    }
//    @Test
//    suspend fun test_password_updated_if_newPassword_is_correct(){
//        val password = "123456"
//        val newPassword = "654321"
//
//        signUpViewModel.register("Test", "User","testuser@random.com",password)
//        Log.i("asdas",changePasswordViewModel.getUpdateDetail().toString())
//        changePasswordViewModel.updatePassword(password, newPassword)
//        Log.i("asdas",changePasswordViewModel.getUpdateDetail().toString())
////        Truth.assertThat(changePasswordViewModel.getUpdateDetail()).isEqualTo(false)
//    }
//
//
//    fun tearDown() {
//
//    }
//}