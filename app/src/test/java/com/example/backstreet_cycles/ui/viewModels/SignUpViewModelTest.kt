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
import com.example.backstreet_cycles.ui.viewModel.BaseViewModel
import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
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
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(),
            FakeCyclistRepoImpl(),
            FakeUserRepoImpl(),
            application,
            context
        )
    }

    @Test
    fun test_sign_up_user() = runBlocking {

//        flowOf("one", "two").test {
//            assertEquals("one", awaitItem())
//            assertEquals("two", awaitItem())
//            awaitComplete()
//        }

        signUpViewModel.register("John","Doe","johndoe@example.com","123456")

        fakeUserRepoImpl.register("John","Doe","johndoe@example.com","123456").onEach { Log.i("Flow..", it.toString()) }.test {
            assert(awaitItem() is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun tests() = runBlocking{

        val size= fakeUserRepoImpl.getUsersDb().size

        signUpViewModel.register("John","Doe","johndoee@example.com","123456")


//        fakeUserRepoImpl.register("John","Doe","johndoe@example.com","123456").onEach { Log.i("Flow..", it.toString()) }.test {
//            assert(awaitItem() is Resource.Success)
//            assert(fakeUserRepoImpl.getUsersDb().size == size + 1)
//            awaitComplete()
//        }
    }
}