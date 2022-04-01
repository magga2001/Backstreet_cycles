package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import dagger.hilt.android.internal.Contexts
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class JourneyViewModelTest {

    private lateinit var journeyViewModel: JourneyViewModel
    private lateinit var homePageViewModel: HomePageViewModel
    lateinit var instrumentationContext: Context

    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl
    private lateinit var fakeLocationRepoImpl: FakeLocationRepoImpl

    private val locations = mutableListOf<Locations>(
        Locations("Current Location",51.5081,-0.0759),
        Locations("Tate Modern",51.5076,-0.0994 ),
        Locations("St Paul's Cathedral",51.5138,-0.0984 )
    )

    @Before
    fun setUp()
    {
        instrumentationContext = ApplicationProvider.getApplicationContext();
        val application = Contexts.getApplication(instrumentationContext)

        fakeTflRepoImpl = FakeTflRepoImpl()
        fakeMapboxRepoImpl = FakeMapboxRepoImpl()
        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
        fakeUserRepoImpl = FakeUserRepoImpl()
        fakeLocationRepoImpl = FakeLocationRepoImpl()

        journeyViewModel = JourneyViewModel(
            FakeTflRepoImpl(),
            FakeMapboxRepoImpl(),
            FakeCyclistRepoImpl(),
            FakeUserRepoImpl(),
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
    }

    @Test
    fun test_get_journey_overview(){
        runBlocking {homePageViewModel.getDock()}
        for(location in locations){
            homePageViewModel.addStop(location)
        }
        homePageViewModel.getRoute()
        homePageViewModel.saveJourney()
        journeyViewModel.calcBicycleRental()
    }
}