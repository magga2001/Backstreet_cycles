package com.example.backstreet_cycles.ui.viewModels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.common.TestAppModule
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.model.dto.Users
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyHistoryViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import com.example.backstreet_cycles.ui.viewModel.LoadingViewModel
import com.mapbox.api.directions.v5.models.DirectionsRoute
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
class JourneyHistoryViewModelTest {

    private lateinit var journeyHistoryViewModel: JourneyHistoryViewModel
    private lateinit var homePageViewModel: HomePageViewModel
    private lateinit var journeyViewModel: JourneyViewModel
    private lateinit var loadingViewModel: LoadingViewModel
    lateinit var instrumentationContext: Context

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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

    private val newLocations= mutableListOf<Locations>(
        Locations("Current Location",51.5009,-0.1774 ),
        Locations("Harrods",51.4994,-0.1632 ),
        Locations("Selfridges",51.5144,-0.1528 )
    )

    @Before
    fun setUp()
    {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        val application = Contexts.getApplication(instrumentationContext)

        fakeTflRepoImpl = FakeTflRepoImpl()
        fakeMapboxRepoImpl = FakeMapboxRepoImpl(TestAppModule.provideRoute())
        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
        fakeUserRepoImpl = FakeUserRepoImpl(
            TestAppModule.provideFirstName(),
            TestAppModule.provideLastName(),
            TestAppModule.provideEmail(),
            TestAppModule.providePassword()
        )
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

        journeyViewModel = JourneyViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
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
    fun test_get_a_journey_without_any_current_journey(){
        journeyHistoryViewModel.addAllStops(locations)
        journeyHistoryViewModel.getRoute()
        assert(journeyHistoryViewModel.getIsReadyMutableLiveData().getOrAwaitValue())
    }

    @Test
    fun test_check_alert_if_there_is_current_journey(){
        get_and_save_journey()
        journeyHistoryViewModel.addAllStops(newLocations)
        journeyHistoryViewModel.getRoute()
        assert(journeyHistoryViewModel.getShowAlertMutableLiveData().getOrAwaitValue())
    }

    @Test
    fun test_continue_with_current_journey(){
        get_and_save_journey()
        journeyHistoryViewModel.addAllStops(newLocations)
        journeyHistoryViewModel.getRoute()
        assert(journeyHistoryViewModel.getShowAlertMutableLiveData().getOrAwaitValue())
        journeyHistoryViewModel.continueWithCurrentJourney()
        assert(journeyHistoryViewModel.getIsReadyMutableLiveData().getOrAwaitValue())
        assert(journeyHistoryViewModel.getJourneyLocations() == locations)
    }

    @Test
    fun test_continue_with_new_journey(){
        get_and_save_journey()
        journeyHistoryViewModel.clearJourneyLocations()
        journeyHistoryViewModel.addAllStops(newLocations)
        journeyHistoryViewModel.getRoute()
        assert(journeyHistoryViewModel.getShowAlertMutableLiveData().getOrAwaitValue())
        journeyHistoryViewModel.continueWithNewJourney(newLocations)
        assert(journeyHistoryViewModel.getIsReadyMutableLiveData().getOrAwaitValue())
        assert(journeyHistoryViewModel.getJourneyLocations() == newLocations)
    }

    @Test
    fun test_get_journey_history(){
        fakeUserRepoImpl.addMockUser("John","Doe","johndoe@example.com","123456")
        get_and_save_journey()
        journeyViewModel.getUserDetails()
        val user = journeyViewModel.getUserInfo().getOrAwaitValue()
        assert(user.equals(Users("John","Doe","johndoe@example.com")))
        journeyViewModel.finishJourney(journeyViewModel.getUserInfo().getOrAwaitValue())
        assert(journeyViewModel.getMessage().getOrAwaitValue() == "Record added")
        assert(journeyHistoryViewModel.getJourneyHistory(user).size == 1)
    }

    fun get_and_save_journey(){
        for(location in locations){
            homePageViewModel.addStop(location)
        }
        homePageViewModel.getRoute()
        runBlocking {loadingViewModel.getDock()}
        loadingViewModel.saveJourney()
    }

    @After
    fun tearDown(){
        journeyHistoryViewModel.clearSaveLocations()
    }
}