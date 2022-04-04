package com.example.backstreet_cycles.ui.views

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.adapter.PlanJourneyAdapter
import com.example.backstreet_cycles.domain.utils.PermissionHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.domain.utils.SnackBarHelper
import com.example.backstreet_cycles.ui.viewModel.JourneyViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.observable.eventdata.MapLoadingErrorEventData
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadErrorListener
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
import kotlinx.android.synthetic.main.journey_bottom_sheet.*

/**
 * This activity launches Journey page which is responsible for displaying a journey's plan
 */
@AndroidEntryPoint
class JourneyActivity : AppCompatActivity() {

    private val routesObserver: RoutesObserver by lazy {
        RoutesObserver { routeUpdateResult ->
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

    private val routeProgressObserver: RouteProgressObserver by lazy {
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
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var planJourneyAdapter: PlanJourneyAdapter
    private lateinit var annotationApi: AnnotationPlugin
    private val journeyViewModel: JourneyViewModel by viewModels()

    /**
     * Initialise the contents within the display of the JourneyPage
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        PermissionHelper.checkPermission(context = this, activity = this)

        annotationApi = journey_mapView.annotations
        mapboxMap = journey_mapView.getMapboxMap()
        MapboxNavigationProvider.destroy()
        init()
    }

    /**
     * Initialise contents and calculate bike fare
     */
    override fun onStart() {
        super.onStart()
        journeyViewModel.calcBicycleRental()
    }

    /**
     *
     */
    override fun onStop() {
        super.onStop()
        journeyViewModel.clearInfo()
    }

    /**
     * Initialisation
     */
    private fun init() {
        initCamera()
        initPadding()
        initRouteLineUI()
        initNavigation()
        initStyle()
        initListeners()
        initBottomSheet()
        initObservers()
        initInfo()
    }

    private fun initObservers() {
        journeyViewModel.getUpdateMap().observe(this) { update ->
            if (update) {
                updateUI()
            } else {
                finish()
                startActivity(intent)
            }
        }

        journeyViewModel.getDistanceData().observe(this) { distance ->
            if (distance != null) {
                distances.text = getString(R.string.journey_distances, distance)
            }
        }

        journeyViewModel.getDurationData().observe(this) { duration ->
            if (duration != null) {
                durations.text = getString(R.string.journey_durations, duration)
            }
        }

        journeyViewModel.getPriceData().observe(this) { price ->
            if (price != null) {
                prices.text = getString(R.string.journey_prices, price)
            }
        }

        journeyViewModel.getIsReady().observe(this){
            onFinishJourney()
        }

        journeyViewModel.getMessage().observe(this){
            SnackBarHelper.displaySnackBar(journeyActivity, it)
        }

        planJourneyAdapter.getAllBoxesCheckedMutableLiveData()
            .observe(this) { allBoxesChecked ->
                finish_journey.isEnabled = allBoxesChecked
            }

        planJourneyAdapter.getCollapseBottomSheet()
            .observe(this) {
                updateCheckBoxSharedPref()
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
    }

    /**
     * Initialise the style of the map
     */
    private fun initStyle() {
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS,
            {
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

    /**
     * Initialise the navigation
     */
    private fun initNavigation() {
        journeyViewModel.setRoute()
        journeyViewModel.registerObservers(
            routesObserver,
            routeProgressObserver
        )
    }

    /**
     * Initialise listeners
     */
    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        start_navigation.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            updateCheckBoxSharedPref()
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        overview_journey.setOnClickListener {
            planJourneyAdapter.getCollapseBottomSheet()
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            journeyViewModel.clearView()
            journeyViewModel.getJourneyOverview()
            start_navigation.isEnabled = false
            SnackBarHelper.displaySnackBar(it, getString(R.string.set_nav_to_start_journey))
        }

        santander_link.setOnClickListener {

            var intent = packageManager.getLaunchIntentForPackage(Constants.CYCLE_APP_ID)

            if (intent == null) {
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

            journeyViewModel.getUserDetails()
            journeyViewModel.getUserInfo().observe(this) { userDetails ->

                if (userDetails != null) {
                    journeyViewModel.clearView()
                    journeyViewModel.clearJourneyLocations()
                    journeyViewModel.finishJourney(userDetails)
                    journeyViewModel.resetNumCyclists()
                }
            }
        }
    }

    /**
     * Initialise the line between the stops to complete the journey
     */
    private fun initRouteLineUI() {

        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withVanishingRouteLineEnabled(true)
            .withRouteLineResources(routeLineResources)
            .withRouteLineBelowLayerId(getString(R.string.road_label))
            .build()

        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        val routeArrowOptions = RouteArrowOptions.Builder(this)
            .withAboveLayerId(TOP_LEVEL_ROUTE_LINE_LAYER_ID)
            .build()

        routeArrowView = MapboxRouteArrowView(routeArrowOptions)
    }

    /**
     * Initialise the camera for the navigation camera
     */
    private fun initCamera() {
        // initialize Navigation Camera
        viewportDataSource = MapboxNavigationViewportDataSource(mapboxMap)
        navigationCamera = NavigationCamera(
            mapboxMap,
            journey_mapView.camera,
            viewportDataSource
        )

        // set the animations lifecycle listener to ensure the NavigationCamera stops
        // automatically following the user location when the map is interacted with
        journey_mapView.camera.addCameraAnimationsLifecycleListener(
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

    /**
     * Restricts area of camera view within map
     */
    private fun initPadding() {
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

    /**
     * Initialise the bottom sheet with its con
     */
    private fun initBottomSheet() {

        sheetBehavior = BottomSheetBehavior.from(journey_bottom_sheet_view)
        val tvFrom = journeyViewModel.getTheCheckedBoxes()
        planJourneyAdapter = PlanJourneyAdapter(
            this,
            journeyViewModel.getCurrentDocks(),
            journeyViewModel.getJourneyLocations(),
            planner = journeyViewModel.getPlannerInterface(),
            tvFrom
        )

        plan_journey_recycling_view.layoutManager = LinearLayoutManager(this)
        plan_journey_recycling_view.adapter = planJourneyAdapter
    }

    /**
     * Display the singular and plural word for bike depending on the number of cyclists selected
     */
    @SuppressLint("StringFormatMatches")
    private fun initInfo() {
        val numCyclists = journeyViewModel.getNumCyclists()

        if (numCyclists < 2) {
            santander_link.text = getString(R.string.hire_bike)
        } else {
            santander_link.text = getString(R.string.hire_bikes, numCyclists)
        }
    }
    private fun updateCheckBoxSharedPref(){
        val checkedBoxes = planJourneyAdapter.getCheckedBoxesToStoreInSharedPref()
        journeyViewModel.storeCheckedBoxesSharedPref(checkedBoxes)
    }

    /**
     * UI is updated as journey progresses
     */
    private fun updateUI() {
        journeyViewModel.setRoute()
        navigationCamera.requestNavigationCameraToOverview()
        journeyViewModel.updateMapMarkerAnnotation(annotationApi)
    }

    private fun onFinishJourney(){

        Handler(Looper.getMainLooper()).postDelayed({

            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        }, Constants.INFORM_SPLASH_TIME)
    }

    /**
     * Initialises the menu
     * @param menu
     * @return true or false
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.journey_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Sets up the menu
     * @param item
     * @return true or false
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.refresh_button -> {
                updateCheckBoxSharedPref()
                journeyViewModel.clearView()
                journeyViewModel.clearInfo()
                val intent = Intent(this, LoadingActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onPause() {
        super.onPause()
        updateCheckBoxSharedPref()
    }

    // Termination of JourneyPage
    override fun onDestroy() {
        super.onDestroy()
        journeyViewModel.unregisterObservers(
            routesObserver,
            routeProgressObserver
        )
        journeyViewModel.clearRoute()
        routeLineView.cancel()
        routeLineApi.cancel()
    }
    /**
     * Terminate JourneyPage when back button is clicked
     */
    override fun onBackPressed() {
        super.onBackPressed()
        updateCheckBoxSharedPref()
        journeyViewModel.clearView()
//        journeyViewModel.clearJourneyLocations()
        finish()
    }

    /**
     * Terminates JourneyPage
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}