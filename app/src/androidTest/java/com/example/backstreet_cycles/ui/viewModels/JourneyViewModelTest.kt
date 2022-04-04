package com.example.backstreet_cycles.ui.viewModels
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.ConvertHelper
import com.example.backstreet_cycles.domain.utils.JourneyState
import com.example.backstreet_cycles.domain.utils.MapInfoHelper
import com.example.backstreet_cycles.ui.viewModel.*
import dagger.hilt.android.internal.Contexts
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class JourneyViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var journeyViewModel: JourneyViewModel
    private lateinit var homePageViewModel: HomePageViewModel
    private lateinit var logInViewModel: LogInViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var loadingViewModel: LoadingViewModel
    lateinit var instrumentationContext: Context

    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl
    private lateinit var fakeLocationRepoImpl: FakeLocationRepoImpl

    private val locations = mutableListOf<Locations>(
        Locations("Current Location", 51.5081, -0.0759),
        Locations("Tate Modern", 51.5076, -0.0994),
        Locations("St Paul's Cathedral", 51.5138, -0.0984)
    )

    @Before
    fun setUp() {
        instrumentationContext = ApplicationProvider.getApplicationContext()
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

        logInViewModel = LogInViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            instrumentationContext
        )

        signUpViewModel = SignUpViewModel(
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
        for (location in locations) {
            homePageViewModel.addStop(location)
        }
        homePageViewModel.getRoute()
        runBlocking { loadingViewModel.getDock() }
        loadingViewModel.saveJourney()
    }

    @Test
    fun test_get_journey_locations() {
        journeyViewModel.calcBicycleRental()
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)
        assert(journeyViewModel.getJourneyLocations().size == locations.size)
    }

    @Test
    fun test_map_is_updated() {
        journeyViewModel.calcBicycleRental()
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)
    }

    @Test
    fun test_get_journey_distance() {
        journeyViewModel.calcBicycleRental()
        assert(journeyViewModel.getJourneyLocations().size == locations.size)
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)
        val distances =
            ConvertHelper.convertMToKm(journeyViewModel.getJourneyDistances()).toString()
        assert(
            journeyViewModel.getDistanceData().getOrAwaitValue()
                .equals(distances)
        )
    }

    @Test
    fun test_get_journey_duration() {
        journeyViewModel.calcBicycleRental()
        assert(journeyViewModel.getJourneyLocations().size == locations.size)
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)
        val distances =
            ConvertHelper.convertMToKm(journeyViewModel.getJourneyDistances()).toString()
        val durations =
            ConvertHelper.convertMsToS(journeyViewModel.getJourneyDurations()).toString()
        assert(
            journeyViewModel.getDurationData().getOrAwaitValue()
                .equals(durations)
        )
    }

    @Test
    fun test_get_journey_price() {
        journeyViewModel.calcBicycleRental()
        journeyViewModel.calcBicycleRental()
        assert(journeyViewModel.getJourneyLocations().size == locations.size)
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)
        val distances =
            ConvertHelper.convertMToKm(journeyViewModel.getJourneyDistances()).toString()
        val durations =
            ConvertHelper.convertMsToS(journeyViewModel.getJourneyDurations()).toString()
        val price = MapInfoHelper.getRental(journeyViewModel.getJourneyDurations())
        assert(journeyViewModel.getPriceData().getOrAwaitValue().equals(price.toString()))
    }

    @Test
    fun test_get_journey_overview() {
        journeyViewModel.calcBicycleRental()
        assert(journeyViewModel.getJourneyLocations().size == locations.size)
        journeyViewModel.getJourneyOverview()
        assert(journeyViewModel.getJourneyState() == JourneyState.OVERVIEW)
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)
    }

    @Test
    fun test_get_journey_current_location_to_dock() {
        journeyViewModel.onSelectedJourney(
            locations[0],
            MapboxConstants.WALKING,
            locations,
            JourneyState.START_WALKING
        )
        assert(journeyViewModel.getJourneyState() == JourneyState.START_WALKING)
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)

    }

    @Test
    fun test_get_journey_dock_to_dock() {
        journeyViewModel.onSelectedJourney(
            locations[0],
            MapboxConstants.WALKING,
            locations,
            JourneyState.BIKING
        )
        assert(journeyViewModel.getJourneyState() == JourneyState.BIKING)
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)

    }

    @Test
    fun test_get_journey_dock_to_destination() {
        journeyViewModel.onSelectedJourney(
            locations[0],
            MapboxConstants.WALKING,
            locations,
            JourneyState.END_WALKING
        )
        assert(journeyViewModel.getJourneyState() == JourneyState.END_WALKING)
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == true)

    }

    @Test
    fun test_finish_journey_get_firstName() = runBlocking {
        fakeUserRepoImpl.addMockUser("John", "Doe", "johndoe@example.com", "123456", locations)
        journeyViewModel.getUserDetails()
        journeyViewModel.finishJourney(journeyViewModel.getUserInfo().getOrAwaitValue())
        assert("John"==journeyViewModel.getUserInfo().getOrAwaitValue().firstName)
    }

    @Test
    fun test_finish_journey_get_lastName() = runBlocking {
        fakeUserRepoImpl.addMockUser("John", "Doe", "johndoe@example.com", "123456", locations)
        journeyViewModel.getUserDetails()
        journeyViewModel.finishJourney(journeyViewModel.getUserInfo().getOrAwaitValue())
        assert("Doe" == journeyViewModel.getUserInfo().getOrAwaitValue().lastName)
    }

    @Test
    fun test_finish_journey_get_email() = runBlocking {
        fakeUserRepoImpl.addMockUser("John", "Doe", "johndoe@example.com", "123456", locations)
        journeyViewModel.getUserDetails()
        journeyViewModel.finishJourney(journeyViewModel.getUserInfo().getOrAwaitValue())
        assert("johndoe@example.com"==journeyViewModel.getUserInfo().getOrAwaitValue().email)
    }

    @Test
    fun test_refresh_journey() {
        runBlocking { journeyViewModel.getDock() }
        assert(journeyViewModel.getJourneyLocations().size == locations.size)
        assert(journeyViewModel.getUpdateMap().getOrAwaitValue() == false)
    }
}
