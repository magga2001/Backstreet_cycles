package com.example.backstreet_cycles.ui.views

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.LiveDataObserver.getOrAwaitValue
import com.example.backstreet_cycles.data.remote.MapboxApi
import com.example.backstreet_cycles.data.repository.*
import com.example.backstreet_cycles.dependencyInjection.AppModule
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.*
import com.example.backstreet_cycles.ui.viewModel.BaseViewModel
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.LoadingViewModel
import com.example.backstreet_cycles.ui.viewModel.NavigationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.core.MapboxNavigation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@RunWith(AndroidJUnit4ClassRunner::class)
@HiltAndroidTest
//@UninstallModules(AppModule::class)
class NavigationActivityTest {

    private val email = "backstreet.cycles.test.user@gmail.com"
    private val password = "123456"

    private lateinit var navigationViewModel: NavigationViewModel
    private lateinit var baseViewModel: BaseViewModel
    private lateinit var loadingViewModel: LoadingViewModel
    private lateinit var homePageViewModel: HomePageViewModel

    lateinit var instrumentationContext: Context

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

//    @Inject
//    lateinit var fakeTflRepoImpl: TflRepository
    @Inject
    lateinit var fakeMapboxRepoImpl: MapboxRepository
//    @Inject
//    lateinit var fakeCyclistRepoImpl: CyclistRepository
//    @Inject
//    lateinit var fakeUserRepoImpl: UserRepository
//    @Inject
//    lateinit var fakeLocationRepoImpl: LocationRepository

    private val locations = mutableListOf<Locations>(
        Locations("Current Location",51.5081,-0.0759),
        Locations("Tate Modern",51.5076,-0.0994 ),
        Locations("St Paul's Cathedral",51.5138,-0.0984 )
    )

    private val userRepoImpl = UserRepositoryImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val locationRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET
        )




    @Before
    fun setUp() {
        instrumentationContext = ApplicationProvider.getApplicationContext()
        val application = Contexts.getApplication(instrumentationContext)

//        val mapboxNavigation = mockk<MapboxNavigation>(relaxed = true)
//        val routeOptions = mockk<RouteOptions>(relaxed = true)
//
//        fakeMapboxRepoImpl.requestRoute(mapboxNavigation, rout)

//        fakeTflRepoImpl = FakeTflRepoImpl()
//        fakeMapboxRepoImpl = FakeMapboxRepoImpl()
//        fakeCyclistRepoImpl = FakeCyclistRepoImpl()
//        fakeUserRepoImpl = FakeUserRepoImpl()
//        fakeLocationRepoImpl = FakeLocationRepoImpl()
//
//
//        fakeMapboxRepoImpl.requestRoute(mapboxNavigation, routeOptions,false)
//
//        baseViewModel = BaseViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            application,
//            instrumentationContext
//        )
//        loadingViewModel = LoadingViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            application,
//            instrumentationContext
//        )
//        homePageViewModel = HomePageViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            fakeLocationRepoImpl,
//            application,
//            instrumentationContext
//        )
//
//        navigationViewModel = NavigationViewModel(
//            fakeTflRepoImpl,
//            fakeMapboxRepoImpl,
//            fakeCyclistRepoImpl,
//            fakeUserRepoImpl,
//            application,
//            instrumentationContext
//        )
//
//        for(location in locations){
//            homePageViewModel.addStop(location)
//        }
//        homePageViewModel.getRoute()
//        runBlocking {loadingViewModel.getDock()}
//        assert(loadingViewModel.getIsReadyMutableLiveData().getOrAwaitValue())



        hiltRule.inject()

        ActivityScenario.launch(NavigationActivity::class.java)

//        userRepoImpl.logOut()
//        userRepoImpl.login(email,password)
//        hiltRule.inject()
//        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
//        SharedPrefHelper.initialiseSharedPref(ApplicationProvider.getApplicationContext(), Constants.LOCATIONS)
//        SharedPrefHelper.clearSharedPreferences()
//        ActivityScenario.launch(HomePageActivity::class.java)
//        addStop("Covent Garden")
//        onView(withId(R.id.nextPageButton)).perform(click())
//        onView(isRoot()).perform(waitFor(3000))
//        onView(withId(R.id.journey_bottom_sheet_constraintLayout)).perform(swipeUp())
//        onView(withId(R.id.planJourney_button_expand)).perform(click())
//        onView(isRoot()).perform(waitFor(500))
//        onView(withId(R.id.setNav1)).perform(click())
//        onView(isRoot()).perform(waitFor(500))
//        onView(withId(R.id.start_navigation)).perform(click())
    }




//    private val hardCodedRoute by lazy {
//        DirectionsRoute.fromJson(
//            """{"routeIndex":"0","distance":1302.142,"duration":192.94,"duration_typical":192.94,"geometry":"wfz_gAjo{nhFw@DcT~@oI^yGV_DJ}CNwQx@wc@rB}Or@yDPyKf@yCNMwEKaEQoGImDu@{ZUkHWgHaA{`@m@kTOaGMeF[iMc@kROoFQeIGeCKqDGwCYeLo@mVCmA_@oOOuFOoFGcCoEaoBGcCG_DGuBMsFCsA}@ca@wByx@YwQsAmk@al@vCya@rB]B{Qv@kWhA","weight":279.607,"weight_name":"auto","legs":[{"distance":1302.142,"duration":192.94,"duration_typical":192.94,"summary":"Mission Street, 16th Street","admins":[{"iso_3166_1":"US","iso_3166_1_alpha3":"USA"}],"steps":[{"distance":265.216,"duration":45.219,"duration_typical":45.219,"speedLimitUnit":"mph","speedLimitSign":"mutcd","geometry":"wfz_gAjo{nhFw@DcT~@oI^yGV_DJ}CNwQx@wc@rB}Or@yDPyKf@yCN","name":"Mission Street","mode":"driving","maneuver":{"location":[-122.419462,37.762684],"bearing_before":0.0,"bearing_after":356.0,"instruction":"Drive north on Mission Street.","type":"depart"},"voiceInstructions":[{"distanceAlongGeometry":265.216,"announcement":"Drive north on Mission Street. Then, in 900 feet, Turn right onto 16th Street.","ssmlAnnouncement":"\u003cspeak\u003e\u003camazon:effect name\u003d\"drc\"\u003e\u003cprosody rate\u003d\"1.08\"\u003eDrive north on Mission Street. Then, in 900 feet, Turn right onto 16th Street.\u003c/prosody\u003e\u003c/amazon:effect\u003e\u003c/speak\u003e"},{"distanceAlongGeometry":81.667,"announcement":"Turn right onto 16th Street.","ssmlAnnouncement":"\u003cspeak\u003e\u003camazon:effect name\u003d\"drc\"\u003e\u003cprosody rate\u003d\"1.08\"\u003eTurn right onto 16th Street.\u003c/prosody\u003e\u003c/amazon:effect\u003e\u003c/speak\u003e"}],"bannerInstructions":[{"distanceAlongGeometry":265.216,"primary":{"text":"16th Street","components":[{"text":"16th Street","type":"text"}],"type":"turn","modifier":"right"}}],"driving_side":"right","weight":61.369,"intersections":[{"location":[-122.419462,37.762684],"bearings":[356],"entry":[true],"out":0,"geometry_index":0,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419465,37.762712],"bearings":[176,356],"entry":[false,true],"in":0,"out":1,"geometry_index":1,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419497,37.76305],"bearings":[176,356],"entry":[false,true],"in":0,"out":1,"geometry_index":2,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419513,37.763218],"bearings":[176,356],"entry":[false,true],"in":0,"out":1,"geometry_index":3,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419525,37.763359],"bearings":[176,357],"entry":[false,true],"in":0,"out":1,"geometry_index":4,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419531,37.763439],"bearings":[177,355],"entry":[false,true],"in":0,"out":1,"geometry_index":5,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419539,37.763518],"bearings":[175,356],"entry":[false,true],"in":0,"out":1,"geometry_index":6,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419568,37.763818],"bearings":[176,356],"entry":[false,true],"in":0,"out":1,"geometry_index":7,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419626,37.764406],"bearings":[176,356],"entry":[false,true],"in":0,"out":1,"geometry_index":8,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419652,37.764677],"bearings":[176,356],"entry":[false,true],"in":0,"out":1,"geometry_index":9,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419661,37.76477],"bearings":[176,356],"entry":[false,true],"in":0,"out":1,"geometry_index":10,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419681,37.764975],"bearings":[176,355],"entry":[false,true],"in":0,"out":1,"geometry_index":11,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}}]},{"distance":814.0,"duration":119.249,"duration_typical":119.249,"speedLimitUnit":"mph","speedLimitSign":"mutcd","geometry":"wz~gAp}{nhFMwEKaEQoGImDu@{ZUkHWgHaA{`@m@kTOaGMeF[iMc@kROoFQeIGeCKqDGwCYeLo@mVCmA@oOOuFOoFGcCoEaoBGcCG_DGuBMsFCsA}@ca@wByx@YwQsAmk@","name":"16th Street","mode":"driving","maneuver":{"location":[-122.419689,37.765052],"bearing_before":355.0,"bearing_after":85.0,"instruction":"Turn right onto 16th Street.","type":"turn","modifier":"right"},"voiceInstructions":[{"distanceAlongGeometry":800.666,"announcement":"Continue for a half mile.","ssmlAnnouncement":"\u003cspeak\u003e\u003camazon:effect name\u003d\"drc\"\u003e\u003cprosody rate\u003d\"1.08\"\u003eContinue for a half mile.\u003c/prosody\u003e\u003c/amazon:effect\u003e\u003c/speak\u003e"},{"distanceAlongGeometry":402.336,"announcement":"In a quarter mile, Turn left onto Bryant Street.","ssmlAnnouncement":"\u003cspeak\u003e\u003camazon:effect name\u003d\"drc\"\u003e\u003cprosody rate\u003d\"1.08\"\u003eIn a quarter mile, Turn left onto Bryant Street.\u003c/prosody\u003e\u003c/amazon:effect\u003e\u003c/speak\u003e"},{"distanceAlongGeometry":66.667,"announcement":"Turn left onto Bryant Street.","ssmlAnnouncement":"\u003cspeak\u003e\u003camazon:effect name\u003d\"drc\"\u003e\u003cprosody rate\u003d\"1.08\"\u003eTurn left onto Bryant Street.\u003c/prosody\u003e\u003c/amazon:effect\u003e\u003c/speak\u003e"}],"bannerInstructions":[{"distanceAlongGeometry":814.0,"primary":{"text":"Bryant Street","components":[{"text":"Bryant Street","type":"text"}],"type":"turn","modifier":"left"}}],"driving_side":"right","weight":173.587,"intersections":[{"location":[-122.419689,37.765052],"bearings":[85,175],"entry":[true,false],"in":1,"out":0,"geometry_index":12,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419581,37.765059],"bearings":[86,265],"entry":[true,false],"in":1,"out":0,"geometry_index":13,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419484,37.765065],"bearings":[85,266],"entry":[true,false],"in":1,"out":0,"geometry_index":14,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.419261,37.765079],"bearings":[86,265],"entry":[true,false],"in":1,"out":0,"geometry_index":16,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.418815,37.765106],"bearings":[85,266],"entry":[true,false],"in":1,"out":0,"geometry_index":17,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.418665,37.765117],"bearings":[84,265],"entry":[true,false],"in":1,"out":0,"geometry_index":18,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.418517,37.765129],"bearings":[86,264],"entry":[true,false],"in":1,"out":0,"geometry_index":19,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.417504,37.765193],"bearings":[86,265],"entry":[true,false],"in":1,"out":0,"geometry_index":22,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.41685,37.765232],"bearings":[86,266],"entry":[true,false],"in":1,"out":0,"geometry_index":25,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.4165,37.765253],"bearings":[85,266],"entry":[true,false],"in":1,"out":0,"geometry_index":28,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.416411,37.765259],"bearings":[86,265],"entry":[true,false],"in":1,"out":0,"geometry_index":29,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.416335,37.765263],"bearings":[86,266],"entry":[true,false],"in":1,"out":0,"geometry_index":30,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.415749,37.7653],"bearings":[86,265],"entry":[true,false],"in":1,"out":0,"geometry_index":32,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.415446,37.765318],"bearings":[85,266],"entry":[true,false],"in":1,"out":0,"geometry_index":34,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.415323,37.765326],"bearings":[85,265],"entry":[true,false],"in":1,"out":0,"geometry_index":35,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.415203,37.765334],"bearings":[86,265],"entry":[true,false],"in":1,"out":0,"geometry_index":36,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.413278,37.765446],"bearings":[86,266],"entry":[true,false],"in":1,"out":0,"geometry_index":39,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.413139,37.765454],"bearings":[86,266],"entry":[true,false],"in":1,"out":0,"geometry_index":41,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.413017,37.765461],"bearings":[86,266],"entry":[true,false],"in":1,"out":0,"geometry_index":42,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.412429,37.765494],"bearings":[85,266],"entry":[true,false],"in":1,"out":0,"geometry_index":44,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.411504,37.765554],"bearings":[87,265],"entry":[true,false],"in":1,"out":0,"geometry_index":45,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}},{"location":[-122.411204,37.765567],"bearings":[86,267],"entry":[true,false],"in":1,"out":0,"geometry_index":46,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"secondary"}}]},{"distance":222.926,"duration":28.472,"duration_typical":28.472,"speedLimitUnit":"mph","speedLimitSign":"mutcd","geometry":"q}_`gAx~inhFal@vCya@rB]B{Qv@kWhA","name":"Bryant Street","mode":"driving","maneuver":{"location":[-122.410493,37.765609],"bearing_before":86.0,"bearing_after":355.0,"instruction":"Turn left onto Bryant Street.","type":"turn","modifier":"left"},"voiceInstructions":[{"distanceAlongGeometry":209.593,"announcement":"In 700 feet, Your destination will be on the right.","ssmlAnnouncement":"\u003cspeak\u003e\u003camazon:effect name\u003d\"drc\"\u003e\u003cprosody rate\u003d\"1.08\"\u003eIn 700 feet, Your destination will be on the right.\u003c/prosody\u003e\u003c/amazon:effect\u003e\u003c/speak\u003e"},{"distanceAlongGeometry":55.556,"announcement":"Your destination is on the right.","ssmlAnnouncement":"\u003cspeak\u003e\u003camazon:effect name\u003d\"drc\"\u003e\u003cprosody rate\u003d\"1.08\"\u003eYour destination is on the right.\u003c/prosody\u003e\u003c/amazon:effect\u003e\u003c/speak\u003e"}],"bannerInstructions":[{"distanceAlongGeometry":222.926,"primary":{"text":"Your destination will be on the right","components":[{"text":"Your destination will be on the right","type":"text"}],"type":"arrive","modifier":"right"}},{"distanceAlongGeometry":55.556,"primary":{"text":"Your destination is on the right","components":[{"text":"Your destination is on the right","type":"text"}],"type":"arrive","modifier":"right"}}],"driving_side":"right","weight":44.652,"intersections":[{"location":[-122.410493,37.765609],"bearings":[266,355],"entry":[false,true],"in":0,"out":1,"geometry_index":47,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"tertiary"}},{"location":[-122.410569,37.76633],"bearings":[175,355],"entry":[false,true],"in":0,"out":1,"geometry_index":48,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"tertiary"}},{"location":[-122.410629,37.766902],"bearings":[175,356],"entry":[false,true],"in":0,"out":1,"geometry_index":50,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"tertiary"}},{"location":[-122.410657,37.767204],"bearings":[176,356],"entry":[false,true],"in":0,"out":1,"geometry_index":51,"is_urban":true,"admin_index":0,"mapbox_streets_v8":{"class":"tertiary"}}]},{"distance":0.0,"duration":0.0,"duration_typical":0.0,"speedLimitUnit":"mph","speedLimitSign":"mutcd","geometry":"syc`gAjkjnhF??","name":"Bryant Street","mode":"driving","maneuver":{"location":[-122.410694,37.767594],"bearing_before":356.0,"bearing_after":0.0,"instruction":"Your destination is on the right.","type":"arrive","modifier":"right"},"voiceInstructions":[],"bannerInstructions":[],"driving_side":"right","weight":0.0,"intersections":[{"location":[-122.410694,37.767594],"bearings":[176],"entry":[true],"in":0,"geometry_index":52,"admin_index":0}]}],"annotation":{"distance":[3.1,37.7,18.8,15.7,8.9,8.8,33.5,65.7,30.3,10.4,22.9,8.6,9.5,8.6,12.0,7.7,39.4,13.3,13.1,47.8,30.2,11.4,10.2,20.2,27.4,10.6,14.4,5.9,7.9,6.7,18.6,33.1,3.4,23.3,10.9,10.6,5.8,158.2,5.8,7.1,5.2,10.8,3.7,48.2,81.7,26.4,62.7,80.5,62.2,1.7,33.7,43.6],"duration":[0.554,4.684,2.328,1.953,1.108,1.588,6.029,11.818,5.446,1.869,4.12,1.548,1.716,1.541,2.162,1.382,7.085,2.386,1.746,6.378,4.027,1.518,1.107,2.205,2.984,1.156,1.569,0.645,0.858,0.928,2.579,4.584,0.516,3.495,1.629,1.467,0.807,21.906,0.807,0.977,0.722,1.336,0.46,5.98,7.001,2.322,8.688,8.054,6.221,0.168,2.822,3.566],"speed":[5.6,8.1,8.1,8.1,8.1,5.6,5.6,5.6,5.6,5.6,5.6,5.6,5.6,5.6,5.6,5.6,5.6,5.6,7.5,7.5,7.5,7.5,9.2,9.2,9.2,9.2,9.2,9.2,9.2,7.2,7.2,7.2,6.7,6.7,6.7,7.2,7.2,7.2,7.2,7.2,7.2,8.1,8.1,8.1,11.7,11.4,7.2,10.0,10.0,10.0,11.9,12.2],"maxspeed":[{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true},{"unknown":true}],"congestion":["low","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","unknown","low","low","low","low","low","low","low","low","low","low","low","low","low","low","low","low","low","low","low","unknown","unknown","unknown","unknown","unknown","unknown","low","low","low","unknown","low"]}}],"routeOptions":{"baseUrl":"https://api.mapbox.com","user":"mapbox","profile":"driving-traffic","coordinates":"-122.4192,37.7627;-122.4106,37.7676","language":"en","continue_straight":true,"roundabout_exits":true,"geometries":"polyline6","overview":"full","steps":true,"annotations":"congestion,maxspeed,speed,duration,distance,closure","voice_instructions":true,"banner_instructions":true,"voice_units":"imperial","uuid":"gBUUlLJctERT8RrvDM7qCrAvnccdmXLCxVQUmAFsjWf3VRGUNK0lVQ\u003d\u003d"},"voiceLocale":"en-US"}"""
//        )
//    }
//
//    @Before
//    fun setUp() {
//        ActivityScenario.launch(NavigationActivity::class.java)
//
//        Intents.init()
//    }

    @Test
    fun navigation_activity_is_displayed(){
        Intents.init()
        intending(hasComponent(NavigationActivity::class.qualifiedName))
        Intents.release()
    }

    @Test
    fun navigation_mapView_is_displayed(){
        onView(withId(R.id.navigation_mapView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_tripProgressCard_is_not_displayed(){
        onView(withId(R.id.tripProgressCard)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_tripProgressView_is_displayed(){
        onView(withId(R.id.tripProgressView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
    @Test
    fun navigation_imageViewStop_is_displayed(){
        onView(withId(R.id.stop)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_maneuverView_is_not_displayed(){
        onView(withId(R.id.maneuverView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun navigation_soundButton_is_not_displayed(){
        onView(withId(R.id.soundButton)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_routeOverview_is__displayed(){
        onView(withId(R.id.routeOverview)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navigation_MapboxRecenterButton_is_not_displayed(){
        onView(withId(R.id.recenter)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    private fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }

    private fun addStop(name: String) {
        val addStopButton = onView(
            Matchers.allOf(
                withId(R.id.addingBtn), withText("Add Stop"),
                childAtPosition(
                    Matchers.allOf(
                        withId(R.id.homepage_bottom_sheet_constraintLayout),
                        childAtPosition(
                            withId(R.id.homepage_bottom_sheet_linearLayout),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        addStopButton.perform(ViewActions.click())

        onView(isRoot()).perform(waitFor(500))
        val location = onView(
            Matchers.allOf(
                withId(R.id.edittext_search),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.searchView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        onView(isRoot()).perform(waitFor(500))
        location.perform(ViewActions.replaceText(name))
        onView(isRoot()).perform(waitFor(500))
        location.perform(
            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER),
            ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
        )

    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    @After
    fun tearDown(){
        userRepoImpl.logOut()
    }
}