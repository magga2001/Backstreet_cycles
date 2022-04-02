package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.ConvertHelper
import com.example.backstreet_cycles.domain.utils.MapInfoHelper
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import dagger.hilt.android.internal.Contexts
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4ClassRunner::class)
class JourneyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
    }

    @Test
    fun test_get_journey_info(){
        for(location in locations){
            homePageViewModel.addStop(location)
        }
        runBlocking {homePageViewModel.getDock()}
        homePageViewModel.saveJourney()
        journeyViewModel.calcBicycleRental()
        assert(journeyViewModel.getJourneyLocations().size == locations.size)
        assert(journeyViewModel.getIsReadyMutableLiveData().getOrAwaitValue().equals("UPDATE"))
        val distances = ConvertHelper.convertMToKm(journeyViewModel.getJourneyDistances()).toString()
        assert(journeyViewModel.getDistanceMutableLiveData().getOrAwaitValue()
            .equals(distances))
        val durations = ConvertHelper.convertMsToS(journeyViewModel.getJourneyDurations()).toString()
        assert(journeyViewModel.getDurationMutableLiveData().getOrAwaitValue()
            .equals(durations))
        val price = MapInfoHelper.getRental(journeyViewModel.getJourneyDurations())
        assert(journeyViewModel.getPriceMutableLiveData().getOrAwaitValue().equals(price.toString()))
    }

}