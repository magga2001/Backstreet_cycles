package com.example.backstreet_cycles.ui.viewModels
//
//import android.content.Context
//import com.example.backstreet_cycles.common.BackstreetApplication
//import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
//import com.example.backstreet_cycles.data.repository.*
//import com.example.backstreet_cycles.domain.model.dto.Locations
//import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
//import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
//import com.example.backstreet_cycles.ui.viewModel.LogInViewModel
//import com.example.backstreet_cycles.ui.viewModel.SignUpViewModel
//import io.mockk.mockk
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.runBlocking
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config
//
//@RunWith(RobolectricTestRunner::class)
//@Config(application = BackstreetApplication::class, manifest = Config.NONE)
//@ExperimentalCoroutinesApi
//class JourneyViewModelUnitTest {
//
//    private lateinit var journeyViewModel: JourneyViewModel
//    private lateinit var homePageViewModel: HomePageViewModel
//    private lateinit var logInViewModel: LogInViewModel
//    private lateinit var signUpViewModel: SignUpViewModel
//
//    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
//    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
//    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
//    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl
//    private lateinit var fakeLocationRepoImpl: FakeLocationRepoImpl
//
//    private val locations = mutableListOf<Locations>(
//        Locations("Current Location",51.5081,-0.0759),
//        Locations("Tate Modern",51.5076,-0.0994 ),
//        Locations("St Paul's Cathedral",51.5138,-0.0984 )
//    )
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
//        fakeLocationRepoImpl = FakeLocationRepoImpl()
//
//        journeyViewModel = JourneyViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            application,
//            context
//        )
//
//        homePageViewModel = HomePageViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            fakeLocationRepoImpl,
//            application,
//            context
//        )
//
//        logInViewModel = LogInViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            application,
//            context
//        )
//
//        signUpViewModel = SignUpViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            application,
//            context
//        )
//    }
//
//
//}