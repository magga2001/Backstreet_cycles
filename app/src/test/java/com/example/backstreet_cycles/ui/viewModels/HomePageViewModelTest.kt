package com.example.backstreet_cycles.ui.viewModels

import android.app.Application
import android.content.Context
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.ui.viewModel.*
import com.mapbox.mapboxsdk.location.LocationComponent
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = BackstreetApplication::class, manifest = Config.NONE)
@ExperimentalCoroutinesApi
class HomePageViewModelTest{
    private lateinit var homepageViewModel: HomePageViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var logInViewModel: LogInViewModel
    private lateinit var loadingViewModel: LoadingViewModel

    private lateinit var fakeTflRepoImpl: FakeTflRepoImpl
    private lateinit var fakeMapboxRepoImpl: FakeMapboxRepoImpl
    private lateinit var fakeCyclistRepoImpl: FakeCyclistRepoImpl
    private lateinit var fakeUserRepoImpl: FakeUserRepoImpl
    private lateinit var fakeLocationRepoImpl: FakeLocationRepoImpl


    val randomLocation1 = Locations("Random Location1", 51.15,0.03)
    val randomLocation2 = Locations("Random Location2", 51.15,0.03)
    val randomLocation3 = Locations("Random Location3", 51.15,0.03)

    @Before
    fun setUp() {
        val context = mockk<Context>(relaxed = true)
        val application = mockk<BackstreetApplication>(relaxed = true)

        fakeTflRepoImpl = FakeTflRepoImpl()
        fakeMapboxRepoImpl = FakeMapboxRepoImpl()
        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
        fakeUserRepoImpl = FakeUserRepoImpl()
        fakeLocationRepoImpl = FakeLocationRepoImpl()

        signUpViewModel = SignUpViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            context
        )
        logInViewModel = LogInViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            context
        )
        homepageViewModel = HomePageViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            fakeLocationRepoImpl,
            application,
            context
        )

        loadingViewModel = LoadingViewModel(
            fakeTflRepoImpl,
            fakeMapboxRepoImpl,
            fakeCyclistRepoImpl,
            fakeUserRepoImpl,
            application,
            context
        )

        signUpViewModel.register("Test", "User","testuser@random.com","123456")
        fakeUserRepoImpl.verifyEmail("testuser@random.com")
        logInViewModel.login("testuser@random.com", "123456")
    }

    @Test
    fun test_add_stop(){
        val size = homepageViewModel.getStops().size
        homepageViewModel.addStop(Locations("Random Location", 51.15,0.03))
        assertEquals(
            size+1,
            homepageViewModel.getStops().size
        )
    }

    @Test
    fun test_remove_stop_that_exists(){
        homepageViewModel.addStop(Locations("Random Location", 51.15,0.03))
        val size = homepageViewModel.getStops().size
        homepageViewModel.removeStop(Locations("Random Location", 51.15,0.03))
        assertEquals(
            size-1,
            homepageViewModel.getStops().size
        )
    }

    @Test
    fun test_remove_stop_that_doesnot_exists(){
        homepageViewModel.addStop(Locations("Random Location", 51.15,0.03))
        val size = homepageViewModel.getStops().size
        homepageViewModel.removeStop(Locations("Not in List", 51.15,0.03))
        assertEquals(
            size,
            homepageViewModel.getStops().size
        )
    }

    @Test
    fun test_add_stop_at_location_2(){
        add_3_stops()
        val locationToCheck = Locations("Random Location to check", 51.15,0.03)
        homepageViewModel.addStop(2,locationToCheck)
        assertEquals(
            locationToCheck,
            homepageViewModel.getStops()[2]
        )
    }

    @Test
    fun test_check_if_previous_location_at_position_2_is_incremented(){
        add_3_stops()
        val locationToCheck = Locations("Random Location to check", 51.15,0.03)
        homepageViewModel.addStop(2,locationToCheck)
        assertEquals(
            randomLocation3,
            homepageViewModel.getStops()[3]
        )
    }

    @Test
    fun test_remove_at_location_2(){
        add_3_stops()
        homepageViewModel.removeStopAt(1)
        assertEquals(
            homepageViewModel.getStops().contains(randomLocation2),false
        )
    }

    @Test
    fun test_removing_at_location_2_decrements_the_next_location(){
        add_3_stops()
        homepageViewModel.removeStopAt(1)
        assertEquals(
            homepageViewModel.getStops().contains(randomLocation2),false
        )
        assertEquals(
            randomLocation3,
            homepageViewModel.getStops()[1]
        )
    }

    @Test
    fun test_location_already_in_the_list(){
        add_3_stops()
        assertEquals(
            true,
            homepageViewModel.checkIfAlreadyInStops(randomLocation1)
        )
    }

    @Test
    fun test_location_not_in_the_list(){
        add_3_stops()
        val randomLocation4 = Locations("Random Location4", 51.15,0.03)
        assertEquals(
            false,
            homepageViewModel.checkIfAlreadyInStops(randomLocation4)
        )
    }

    @Test
    fun test_check_if_current_location_exists_without_adding_it(){
        val currentLocation = Locations("Current Location", 0.0,0.0)
        assertEquals(
            false,
            homepageViewModel.checkIfAlreadyInStops(currentLocation)
        )
    }

    @Test
    fun test_check_if_current_location_exists_after_adding_it(){
        val currentLocation = Locations("Current Location", 0.0,0.0)
        homepageViewModel.addStop(currentLocation)
        assertEquals(
            true,
            homepageViewModel.checkIfAlreadyInStops(currentLocation)
        )
    }

    @Test
    fun test_alert_status_before_posting_it(){
        assertEquals(
            false,
            homepageViewModel.getShowAlertMutableLiveData().value
        )
    }

    @Test
    fun test_alert_status_after_posting_it(){
        homepageViewModel.setShowAlert(true)
        assertEquals(
            true,
            homepageViewModel.getShowAlertMutableLiveData().value
        )
    }

    @Test
    fun test_get_tourist_locations_without_loading(){
        //homepageViewModel.getTouristAttractions()
        val emptyList: MutableList<Locations> = mutableListOf()
        assertEquals(
            emptyList,
            homepageViewModel.getTouristAttractions()
        )
    }

    @Test
    fun test_get_tourist_locations_after_loading(){
        fakeLocationRepoImpl.loadLocations(Application())
        assertEquals(
            10,
            homepageViewModel.getTouristAttractions().size
        )
    }

    @Test
    fun test_update_info_status_before_posting_it(){
        assertEquals(
            false,
            homepageViewModel.getUpdateInfo()
        )
    }

    @Test
    fun test_update_info_status_after_posting_it(){
        homepageViewModel.setUpdateInfo(true)
        assertEquals(
            true,
            homepageViewModel.getUpdateInfo()
        )
    }


    @Test
    fun test_logOut_value_before_logging_out(){
        assertEquals(
            false,
            homepageViewModel.getLogout().getOrAwaitValue()
        )
    }

    @Test
    fun test_logOut_value_after_logging_out(){
        homepageViewModel.logOut()
        assertEquals(
            true,
            homepageViewModel.getLogout().getOrAwaitValue()
        )
    }

    fun add_3_stops(){
        homepageViewModel.addStop(randomLocation1)
        homepageViewModel.addStop(randomLocation2)
        homepageViewModel.addStop(randomLocation3)
    }
}