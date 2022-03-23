package com.example.backstreet_cycles.ui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.data.remote.dto.TflHelper
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.adapter.PlanJourneyAdapter
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.useCase.PermissionUseCase
import com.example.backstreet_cycles.domain.useCase.PlannerUseCase
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.interfaces.Planner
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
import com.example.backstreet_cycles.ui.viewModel.HomePageViewModel
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
import com.example.backstreet_cycles.domain.useCase.PermissionUseCase
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.observable.eventdata.MapLoadingErrorEventData
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadErrorListener
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.route.RouteLayerConstants.TOP_LEVEL_ROUTE_LINE_LAYER_ID
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import kotlinx.android.synthetic.main.activity_journey.*
import kotlinx.android.synthetic.main.bottom_sheet_journey.*


class JourneyActivity : AppCompatActivity(), Planner {

    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeOverviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            20.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val landscapeFollowingPadding: EdgeInsets by lazy {
        EdgeInsets(
            30.0 * pixelDensity,
            380.0 * pixelDensity,
            110.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }

    /**
     * Produces the camera frames based on the location and routing data for the [navigationCamera] to execute.
     */
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource

    /**
     * RouteLine: This class is responsible for rendering route line related mutations generated
     * by the [routeLineApi]
     */
    private lateinit var routeLineView: MapboxRouteLineView

    /**
     * RouteLine: This class is responsible for generating route line related data which must be
     * rendered by the [routeLineView] in order to visualize the route line on the map.
     */
    private lateinit var routeLineApi: MapboxRouteLineApi

    /**
     * RouteArrow: This class is responsible for generating data related to maneuver arrows. The
     * data generated must be rendered by the [routeArrowView] in order to apply mutations to
     * the map.
     */
    private val routeArrowApi: MapboxRouteArrowApi = MapboxRouteArrowApi()

    /**
     * RouteArrow: This class is responsible for rendering the arrow related mutations generated
     * by the [routeArrowApi]
     */
    private lateinit var routeArrowView: MapboxRouteArrowView

    /**
     * Used to execute camera transitions based on the data generated by the [viewportDataSource].
     * This includes transitions from route overview to route following and continuously updating the camera as the location changes.
     */
    private lateinit var navigationCamera: NavigationCamera

    private lateinit var routeLineResources: RouteLineResources
    private lateinit var routesObserver: RoutesObserver
    private lateinit var routeProgressObserver: RouteProgressObserver
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var journeyViewModel: JourneyViewModel
    private lateinit var loggedInViewModel: LoggedInViewModel
    private lateinit var homePageViewModel: HomePageViewModel
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var planJourneyAdapter: PlanJourneyAdapter
    private val currentRoute = MapRepository.currentRoute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        journeyViewModel = ViewModelProvider(this)[JourneyViewModel::class.java]
        loggedInViewModel = ViewModelProvider(this)[LoggedInViewModel::class.java]
        homePageViewModel = ViewModelProvider(this)[HomePageViewModel::class.java]

        journeyViewModel = ViewModelProvider(this)[JourneyViewModel::class.java]

        journeyViewModel.getIsReadyMutableLiveData().observe(this) { ready ->
            if (ready) {
                updateUI()
                journeyViewModel.getIsReadyMutableLiveData().value = false
            }
        }

        journeyViewModel.getDistanceMutableLiveData().observe(this){ distance ->
            if(distance != null)
            {
                distances.text = getString(R.string.journey_distances, distance)
            }
        }

        journeyViewModel.getDurationMutableLiveData().observe(this){ duration ->
            if(duration != null)
            {
                durations.text =  getString(R.string.journey_durations, duration)
            }
        }

        journeyViewModel.getPriceMutableLiveData().observe(this){ price ->
            if(price != null)
            {
                prices.text = getString(R.string.journey_prices, price)
            }
        }

        PermissionUseCase.checkPermission(context = this, activity = this)

        mapboxMap = mapView.getMapboxMap()
        MapboxNavigationProvider.destroy()
        mapboxNavigation = journeyViewModel.initialiseMapboxNavigation()
        init()
    }

    override fun onStart() {
        super.onStart()
        val users = intent.getIntExtra("NUM_USERS",1)
        PlannerUseCase.calcBicycleRental(application, users, plannerInterface = this)
        mapboxNavigation = journeyViewModel.initialiseMapboxNavigation()
    }

    override fun onStop() {
        super.onStop()
        MapRepository.distances.clear()
        MapRepository.durations.clear()
    }

    private fun init() {
        initCamera()
        initPadding()
        initRouteLineUI()
        initObservers()
        initNavigation()
        initStyle()
        initListeners()
        initBottomSheet()
    }

    private fun initObservers() {
        routesObserver = journeyViewModel.initialiseRoutesObserver(
            mapboxMap,
            routeLineApi,
            routeLineView,
            viewportDataSource
        )

        routeProgressObserver = journeyViewModel.initialiseRouteProgressObserver(
            mapboxMap,
            routeLineApi,
            routeLineView,
            routeArrowApi,
            routeArrowView,
            viewportDataSource
        )
    }

    private fun initStyle() {
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS,
            {
                journeyViewModel.addAnnotationToMap(this, mapView)
                updateUI()
            },
            object : OnMapLoadErrorListener {
                @SuppressLint("LogNotTimber")
                override fun onMapLoadError(eventData: MapLoadingErrorEventData) {
                    Log.e(
                        JourneyActivity::class.java.simpleName,
                        "Error loading map: " + eventData.message
                    )
                }
            }
        )
    }

    private fun initNavigation() {
        mapboxNavigation.setRoutes(currentRoute)
        journeyViewModel.registerObservers(
            mapboxNavigation,
            routesObserver,
            routeProgressObserver
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {

        start_navigation.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }

        overview_journey.setOnClickListener {

            journeyViewModel.clear()
            val points = setPoints(MapRepository.location)

            journeyViewModel.fetchRoute(context = this, mapboxNavigation, points, "cycling", false)
        }

        santander_link.setOnClickListener {

            var intent = packageManager.getLaunchIntentForPackage("uk.gov.tfl.cyclehire")

            if(intent == null) {
                intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "https://play.google.com/store/apps/details?id=uk.gov.tfl.cyclehire"
                    )
                    setPackage("com.android.vending")
                }
            }

            startActivity(intent)
        }

        finish_journey.setOnClickListener {
            loggedInViewModel.getUserDetails()
            loggedInViewModel.getUserDetailsMutableLiveData().observe(this) { userDetails ->
                if (userDetails != null){
                    journeyViewModel.clear()
                    MapRepository.location.clear()
                    SharedPrefHelper.initialiseSharedPref(application,"LOCATIONS")
                    journeyViewModel.addJourneyToJourneyHistory(SharedPrefHelper.getSharedPref(Locations::class.java),userDetails)

//                    journeyViewModel.addJourneyToJourneyHistory(journeyViewModel.getListLocations().toMutableList(),userDetails)
//                    journeyViewModel.clearListLocations()
                    SharedPrefHelper.clearListLocations()
                    val intent = Intent(this, HomePageActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
                }
            }

        }
    }

    private fun initRouteLineUI() {
        routeLineResources = journeyViewModel.initialiseRouteLineResources()

        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withVanishingRouteLineEnabled(true)
            .withRouteLineResources(routeLineResources)
            .withRouteLineBelowLayerId("road-label")
            .build()

        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        val routeArrowOptions = RouteArrowOptions.Builder(this)
            .withAboveLayerId(TOP_LEVEL_ROUTE_LINE_LAYER_ID)
            .build()

        routeArrowView = MapboxRouteArrowView(routeArrowOptions)
    }

    private fun initCamera()
    {
        // initialize Navigation Camera
        viewportDataSource = MapboxNavigationViewportDataSource(mapboxMap)
        navigationCamera = NavigationCamera(
            mapboxMap,
            mapView.camera,
            viewportDataSource
        )

        // set the animations lifecycle listener to ensure the NavigationCamera stops
        // automatically following the user location when the map is interacted with
        mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )
        navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->
            // shows/hide the recenter button depending on the camera state
            when (navigationCameraState) {
                NavigationCameraState.TRANSITION_TO_FOLLOWING,
                NavigationCameraState.FOLLOWING,
                NavigationCameraState.TRANSITION_TO_OVERVIEW,
                NavigationCameraState.OVERVIEW,
                NavigationCameraState.IDLE -> Log.i("Success", "Nav")
            }
        }
    }

    private fun initPadding()
    {
        //set the padding values depending on screen orientation and visible view layout
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.overviewPadding = landscapeOverviewPadding
        } else {
            viewportDataSource.overviewPadding = overviewPadding
        }
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.followingPadding = landscapeFollowingPadding
        } else {
            viewportDataSource.followingPadding = followingPadding
        }
    }

    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_view_journey)
        finish_journey.isEnabled = false
        planJourneyAdapter = PlanJourneyAdapter(this, MapRepository.location, this)
        plan_journey_recycling_view.layoutManager = LinearLayoutManager(this)
        plan_journey_recycling_view.adapter = planJourneyAdapter
        planJourneyAdapter.getAllBoxesCheckedMutableLiveData()
            .observe(this) {allBoxesChecked ->
                finish_journey.isEnabled = allBoxesChecked
            }
        planJourneyAdapter.getCollapseBottomSheet()
            .observe(this) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.journey_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.reload_button -> {
                journeyViewModel.clear()
                MapRepository.distances.clear()
                MapRepository.durations.clear()
                TflHelper.getDock(context = applicationContext,
                    object :
                        com.example.backstreet_cycles.interfaces.Assests<MutableList<Dock>> {
                        override fun getResult(objects: MutableList<Dock>) {
                            val points = setPoints(MapRepository.location)
                            journeyViewModel.fetchRoute(applicationContext, mapboxNavigation, points, "cycling", false)

                            //Problem with not loading fast enough...
                            startActivity(intent)
                            finish()
                        }
                    }
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI() {

        mapboxNavigation.setRoutes(currentRoute)
        navigationCamera.requestNavigationCameraToOverview()
        journeyViewModel.removeAnnotations()
        journeyViewModel.addAnnotationToMap(context = this, mapView)
    }

    override fun onDestroy() {
        super.onDestroy()
        journeyViewModel.unregisterObservers(
            mapboxNavigation,
            routesObserver,
            routeProgressObserver
        )

        //To destroy and not let it overlap to next mapbox activity
        mapboxNavigation.setRoutes(listOf())
        routeLineView.cancel()
        routeLineApi.cancel()
    }

    override fun onSelectedJourney(location: Locations, profile: String, points : MutableList<Point>) {
        journeyViewModel.clear()
        journeyViewModel.fetchRoute(context = this, mapboxNavigation, points, profile, false)
    }

    override fun onFetchJourney(points : MutableList<Point>) {
        Log.i("fetching distance", "Success")
        journeyViewModel.fetchRoute(context = this, mapboxNavigation, points, "cycling", true)
    }

    private fun setPoints(newStops: MutableList<Locations>): MutableList<Point> {
        val listPoints = emptyList<Point>().toMutableList()
        for (i in 0 until newStops.size){
            listPoints.add(Point.fromLngLat(MapRepository.location[i].lon, MapRepository.location[i].lat))
        }
        return listPoints
    }

    override fun onBackPressed() {
        super.onBackPressed()
        journeyViewModel.clear()
        MapRepository.location.clear()
        finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }
}