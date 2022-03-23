package com.example.backstreet_cycles.ui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.data.remote.TflHelper
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.adapter.PlanJourneyAdapter
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.useCase.MapAnnotationUseCase
import com.example.backstreet_cycles.domain.useCase.PermissionUseCase
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_journey.*
import kotlinx.android.synthetic.main.bottom_sheet_journey.*

@AndroidEntryPoint
class JourneyActivity : AppCompatActivity() {

    private val routesObserver: RoutesObserver by lazy { RoutesObserver { routeUpdateResult ->
        // RouteLine: wrap the DirectionRoute objects and pass them
        // to the MapboxRouteLineApi to generate the data necessary to draw the route(s)
        // on the map.
        val routeLines = routeUpdateResult.routes.map { RouteLine(it, null) }

        routeLineApi.setRoutes(
            routeLines
        ) { value ->
            // RouteLine: The MapboxRouteLineView expects a non-null reference to the map style.
            // the data generated by the call to the MapboxRouteLineApi above must be rendered
            // by the MapboxRouteLineView in order to visualize the changes on the map.
            mapboxMap.getStyle()?.apply {
                routeLineView.renderRouteDrawData(this, value)
                }
            }

        viewportDataSource.onRouteChanged(routeUpdateResult.routes.first())
        viewportDataSource.evaluate()
        }
    }

    private val routeProgressObserver : RouteProgressObserver by lazy {
        RouteProgressObserver { routeProgress ->

            viewportDataSource.onRouteProgressChanged(routeProgress)
            viewportDataSource.evaluate()
            // RouteLine: This line is only necessary if the vanishing route line feature
            // is enabled.
            routeLineApi.updateWithRouteProgress(routeProgress) { result ->
                mapboxMap.getStyle()?.apply {
                    routeLineView.renderRouteLineUpdate(this, result)
                }
            }

            // RouteArrow: The next maneuver arrows are driven by route progress events.
            // Generate the next maneuver arrow update data and pass it to the view class
            // to visualize the updates on the map.
            val arrowUpdate = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
            mapboxMap.getStyle()?.apply {
                // Render the result to update the map.
                routeArrowView.renderManeuverUpdate(this, arrowUpdate)
            }
        }
    }

    private val routeLineResources: RouteLineResources by lazy {
        RouteLineResources.Builder()
            /**
             * Route line related colors can be customized via the [RouteLineColorResources]. If using the
             * default colors the [RouteLineColorResources] does not need to be set as seen here, the
             * defaults will be used internally by the builder.
             */
            .routeLineColorResources(RouteLineColorResources.Builder().build())
            .build()
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
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var loggedInViewModel: LoggedInViewModel
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var planJourneyAdapter: PlanJourneyAdapter
    private val currentRoute = MapRepository.currentRoute
    private val journeyViewModel : JourneyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        journeyViewModel = ViewModelProvider(this)[JourneyViewModel::class.java]
        loggedInViewModel = ViewModelProvider(this)[LoggedInViewModel::class.java]

        PermissionUseCase.checkPermission(context = this, activity = this)

        mapboxMap = mapView.getMapboxMap()
        MapboxNavigationProvider.destroy()
        mapboxNavigation = journeyViewModel.initialiseMapboxNavigation()
        init()
    }

    override fun onStart() {
        super.onStart()
        val users = intent.getIntExtra("NUM_USERS",1)
        journeyViewModel.calcBicycleRental(users)
//        PlannerUseCase.calcBicycleRental(application, users, plannerInterface = this)
        mapboxNavigation = journeyViewModel.initialiseMapboxNavigation()
    }

    override fun onStop() {
        super.onStop()
        journeyViewModel.clearInfo()
    }

    private fun init() {
        initObservers()
        initCamera()
        initPadding()
        initRouteLineUI()
        initNavigation()
        initStyle()
        initListeners()
        initBottomSheet()
    }

    private fun initObservers() {
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
    }

    private fun initStyle() {
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS,
            {
//                MapAnnotationUseCase.addAnnotationToMap(this, mapView)
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
            val points = PlannerHelper.setPoints(MapRepository.location)

            journeyViewModel.fetchRoute(mapboxNavigation, points, "cycling", false)
        }

        santander_link.setOnClickListener {

            var intent = packageManager.getLaunchIntentForPackage(Constants.CYCLE_APP_ID)

            if(intent == null) {
                intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        Constants.GOOGLE_PLAY_CYCLE_ID
                    )
                    setPackage(Constants.ANDROID_VENDING)
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
                    SharedPrefHelper.initialiseSharedPref(application,Constants.LOCATIONS)
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
            viewportDataSource.overviewPadding = MapboxConstants.landscapeOverviewPadding
        } else {
            viewportDataSource.overviewPadding = MapboxConstants.overviewPadding
        }
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewportDataSource.followingPadding = MapboxConstants.landscapeFollowingPadding
        } else {
            viewportDataSource.followingPadding = MapboxConstants.followingPadding
        }
    }

    private fun initBottomSheet() {

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_view_journey)
        finish_journey.isEnabled = false
        planJourneyAdapter = PlanJourneyAdapter(this, MapRepository.location, planner = journeyViewModel.getPlannerInterface())
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
                journeyViewModel.getDock()

                TflHelper.getDock(context = applicationContext,
                    object :
                        CallbackResource<MutableList<Dock>> {
                        override fun getResult(objects: MutableList<Dock>) {
//                            val points = PlannerHelper.setPoints(MapRepository.location)
//                            journeyViewModel.fetchRoute(mapboxNavigation, points, "cycling", false)

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
        MapAnnotationUseCase.removeAnnotations()
        MapAnnotationUseCase.addAnnotationToMap(context = this, mapView)
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

    override fun onBackPressed() {
        super.onBackPressed()
        journeyViewModel.clear()
        MapRepository.location.clear()
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)

    }
}