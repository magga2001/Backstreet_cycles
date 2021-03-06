package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyHistoryViewModel
import com.example.backstreet_cycles.ui.viewModel.LoadingViewModel
import dagger.hilt.android.internal.Contexts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
@ExperimentalCoroutinesApi
class LoadingViewModelTest {

    private lateinit var journeyHistoryViewModel: JourneyHistoryViewModel
    private lateinit var homePageViewModel: HomePageViewModel
    private lateinit var loadingViewModel: LoadingViewModel
    private lateinit var instrumentationContext: Context

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl
    private lateinit var fakeLocationRepoImpl: FakeLocationRepoImpl

    private val locations = mutableListOf(
        Locations("Current Location",51.5081,-0.0759),
        Locations("Tate Modern",51.5076,-0.0994 ),
        Locations("St Paul's Cathedral",51.5138,-0.0984 )
    )

    @Before
    fun setUp()
    {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        val application = Contexts.getApplication(instrumentationContext)

        fakeTflRepoImpl = FakeTflRepoImpl()
        fakeMapboxRepoImpl = FakeMapboxRepoImpl()
        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
        fakeUserRepoImpl = FakeUserRepoImpl()
        fakeLocationRepoImpl = FakeLocationRepoImpl()

        journeyHistoryViewModel = JourneyHistoryViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            instrumentationContext
        )

        homePageViewModel = HomePageViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            fakeLocationRepoImpl,
            application,
            instrumentationContext
        )

        loadingViewModel = LoadingViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            instrumentationContext
        )
    }

    @Test
    fun test_get_journey_route(){
        addStops()
        homePageViewModel.getRoute()
        runBlocking {loadingViewModel.getDock()}
        assert(loadingViewModel.getIsReady().getOrAwaitValue())
    }

    @Test
    fun test_get_journey_route_with_no_connection(){
        fakeMapboxRepoImpl.setConnection(false)
        addStops()
        homePageViewModel.getRoute()
        runBlocking {loadingViewModel.getDock()}
        assert(!loadingViewModel.getIsReady().getOrAwaitValue())
        assert(loadingViewModel.getMessage().getOrAwaitValue() == "Fail to retrieve route")
    }

    private fun addStops(){
        for(location in locations){
            homePageViewModel.addStop(location)
        }
    }

    @After
    fun tearDown(){
        journeyHistoryViewModel.clearSaveLocations()
    }
}