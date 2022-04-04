package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.domain.utils.PermissionHelper
import com.example.backstreet_cycles.ui.viewModel.NavigationViewModel
import com.mapbox.bindgen.Expected
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.*
import com.mapbox.navigation.ui.tripprogress.view.MapboxTripProgressView
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement
import com.mapbox.navigation.ui.voice.model.SpeechError
import com.mapbox.navigation.ui.voice.model.SpeechValue
import com.mapbox.navigation.ui.voice.model.SpeechVolume
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_navigation.*
import java.util.*

/**
 * This activity launches Navigation page that is responsible live GPS navigation
 */
@AndroidEntryPoint
class NavigationActivity : AppCompatActivity() {

    private val navigationLocationProvider = NavigationLocationProvider()

    private val locationComponent: LocationComponentPlugin by lazy {
        navigation_mapView.location.apply {
            this.locationPuck = LocationPuck2D(
                bearingImage = ContextCompat.getDrawable(
                    applicationContext,
                    com.mapbox.services.android.navigation.ui.v5.R.drawable.ic_arrow_head
                )
            )
            setLocationProvider(navigationLocationProvider)
            enabled = true
        }
    }

    private val onPositionChangedListener: OnIndicatorPositionChangedListener by lazy {

        OnIndicatorPositionChangedListener { point ->
            val result = routeLineApi.updateTraveledRouteLine(point)
            mapboxMap.getStyle()?.apply {
                // Render the result to update the map.
                routeLineView.renderRouteLineUpdate(this, result)
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

    private val locationObserver: LocationObserver by lazy {

        object : LocationObserver {
            var firstLocationUpdateReceived = false

            override fun onNewRawLocation(rawLocation: Location) {
                // not handled
            }

            override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
                val enhancedLocation = locationMatcherResult.enhancedLocation
                // update location puck's position on the map
                navigationLocationProvider.changePosition(
                    location = enhancedLocation,
                    keyPoints = locationMatcherResult.keyPoints,
                )

                // update camera position to account for new location
                viewportDataSource.onLocationChanged(enhancedLocation)
                viewportDataSource.evaluate()

                // if this is the first location update the activity has received,
                // it's best to immediately move the camera to the current user location
                if (!firstLocationUpdateReceived) {
                    firstLocationUpdateReceived = true
                    navigationCamera.requestNavigationCameraToOverview(
                        stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                            .maxDuration(0) // instant transition
                            .build()
                    )
                }
            }
        }
    }

    private val routeProgressObserver by lazy {
        RouteProgressObserver { routeProgress ->
            // update the camera position to account for the progressed fragment of the route
            viewportDataSource.onRouteProgressChanged(routeProgress)
            viewportDataSource.evaluate()

            // draw the upcoming maneuver arrow on the map
            val style = mapboxMap.getStyle()
            if (style != null) {
                val maneuverArrowResult = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
                routeArrowView.renderManeuverUpdate(style, maneuverArrowResult)
            }

            // update top banner with maneuver instructions
            val maneuvers = maneuverApi.getManeuvers(routeProgress)
            maneuvers.fold({},
                {
                    maneuverView.visibility = View.VISIBLE
                    maneuverView.renderManeuvers(maneuvers)
                }
            )

            // update bottom trip progress summary
            tripProgressView.render(
                tripProgressApi.getTripProgress(routeProgress)
            )

            //Update route
            routeLineApi.updateWithRouteProgress(routeProgress) { result ->
                mapboxMap.getStyle()?.apply {
                    routeLineView.renderRouteLineUpdate(this, result)
                }
            }
        }
    }

    private val routesObserver by lazy {

        RoutesObserver { routeUpdateResult ->
            if (routeUpdateResult.routes.isNotEmpty()) {
                // generate route geometries asynchronously and render them
                val routeLines = routeUpdateResult.routes.map { RouteLine(it, null) }

                routeLineApi.setRoutes(
                    routeLines
                ) { value ->
                    mapboxMap.getStyle()?.apply {
                        routeLineView.renderRouteDrawData(this, value)
                    }
                }

                // update the camera position to account for the new route
                viewportDataSource.onRouteChanged(routeUpdateResult.routes.first())
                viewportDataSource.evaluate()
            } else {
                // remove the route line and route arrow from the map
                val style = mapboxMap.getStyle()
                if (style != null) {
                    routeLineApi.clearRouteLine { value ->
                        routeLineView.renderClearRouteLineValue(
                            style,
                            value
                        )
                    }
                    routeArrowView.render(style, routeArrowApi.clearArrows())
                }

                // remove the route reference from camera position evaluations
                viewportDataSource.clearRouteData()
                viewportDataSource.evaluate()
            }
        }
    }

    private val voiceInstructionsObserver: VoiceInstructionsObserver by lazy {
        VoiceInstructionsObserver { voiceInstructions ->
            speechApi.generate(voiceInstructions, speechCallback)
        }
    }

    private val speechCallback =
        MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> { expected ->
            expected.fold(
                { error ->
                    // play the instruction via fallback text-to-speech engine
                    voiceInstructionsPlayer.play(
                        error.fallback,
                        voiceInstructionsPlayerCallback
                    )
                },
                { value ->
                    // play the sound file from the external generator
                    voiceInstructionsPlayer.play(
                        value.announcement,
                        voiceInstructionsPlayerCallback
                    )
                }
            )
        }

    private val voiceInstructionsPlayerCallback =
        MapboxNavigationConsumer<SpeechAnnouncement> { value ->
            // remove already consumed file to free-up space
            speechApi.clean(value)
        }

    /**
     * Mapbox Maps entry point obtained from the [MapView].
     * You need to get a new reference to this object whenever the [MapView] is recreated.
     */
    private lateinit var mapboxMap: MapboxMap

    /**
     * Used to execute camera transitions based on the data generated by the [viewportDataSource].
     * This includes transitions from route overview to route following and continuously updating the camera as the location changes.
     */
    private lateinit var navigationCamera: NavigationCamera

    /**
     * Produces the camera frames based on the location and routing data for the [navigationCamera] to execute.
     */
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource

    /**
     * Generates updates for the [MapboxManeuverView] to display the upcoming maneuver instructions
     * and remaining distance to the maneuver point.
     */
    private lateinit var maneuverApi: MapboxManeuverApi

    /**
     * Generates updates for the [MapboxTripProgressView] that include remaining time and distance to the destination.
     */
    private lateinit var tripProgressApi: MapboxTripProgressApi

    /**
     * Generates updates for the [routeLineView] with the geometries and properties of the routes that should be drawn on the map.
     */
    private lateinit var routeLineApi: MapboxRouteLineApi

    /**
     * Draws route lines on the map based on the data from the [routeLineApi]
     */
    private lateinit var routeLineView: MapboxRouteLineView

    /**
     * Generates updates for the [routeArrowView] with the geometries and properties of maneuver arrows that should be drawn on the map.
     */
    private val routeArrowApi: MapboxRouteArrowApi = MapboxRouteArrowApi()

    /**
     * Draws maneuver arrows on the map based on the data [routeArrowApi].
     */
    private lateinit var routeArrowView: MapboxRouteArrowView

    /**
     * Stores and updates the state of whether the voice instructions should be played as they come or muted.
     */
    private var isVoiceInstructionsMuted = false
        set(value) {
            field = value
            if (value) {
                soundButton.muteAndExtend(Constants.BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer.volume(SpeechVolume(0f))
            } else {
                soundButton.unmuteAndExtend(Constants.BUTTON_ANIMATION_DURATION)
                voiceInstructionsPlayer.volume(SpeechVolume(1f))
            }
        }

    /**
     * Extracts message that should be communicated to the driver about the upcoming maneuver.
     * When possible, downloads a synthesized audio file that can be played back to the driver.
     */
    private lateinit var speechApi: MapboxSpeechApi

    /**
     * Plays the synthesized audio files with upcoming maneuver instructions
     * or uses an on-device Text-To-Speech engine to communicate the message to the driver.
     */
    private lateinit var voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer
    private lateinit var mapboxNavigation: MapboxNavigation

    private val navigationViewModel: NavigationViewModel by viewModels()

    /**
     * Initialise the contents within the display of the Navigation activity
     * @param savedInstanceState used to restore a saved state so activity can be recreated
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        supportActionBar?.hide()
        MapboxNavigationProvider.destroy()

//        navigationViewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)
        PermissionHelper.checkPermission(context = this, activity = this)

        mapboxMap = navigation_mapView.getMapboxMap()
//        mapboxNavigation = navigationViewModel.initialiseMapboxNavigation()
        mapboxNavigation = navigationViewModel.getMapBoxNavigation()

        init()

        // start the trip session to being receiving location updates in free drive
        // and later when a route is set also receiving route progress updates
        mapboxNavigation.startTripSession()
    }

    /**
     * Initialise navigation
     */
    private fun init() {
        initNavigationUI()
        initLocationPuck()
        initCamera()
        initPadding()
        initViewListener()
        initStyle()
    }

    /**
     * Initialise navigation user interface
     */
    private fun initNavigationUI() {
        initialiseProgressUI()
        initialiseVoiceUI()
        initialiseRouteLineUI()
    }

    /**
     * Initialise location symbol
     */
    private fun initLocationPuck() {
        // initialize the location puck
        locationComponent.addOnIndicatorPositionChangedListener(onPositionChangedListener)
    }

    /**
     * Initialise navigation camera
     */
    private fun initCamera() {
        // initialize Navigation Camera
        viewportDataSource = MapboxNavigationViewportDataSource(mapboxMap)
        navigationCamera = NavigationCamera(
            mapboxMap,
            navigation_mapView.camera,
            viewportDataSource
        )
        // set the animations lifecycle listener to ensure the NavigationCamera stops
        // automatically following the user location when the map is interacted with
        navigation_mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )
        navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->
            // shows/hide the recenter button depending on the camera state
            when (navigationCameraState) {
                NavigationCameraState.TRANSITION_TO_FOLLOWING,
                NavigationCameraState.FOLLOWING -> recenter.visibility = View.INVISIBLE
                NavigationCameraState.TRANSITION_TO_OVERVIEW,
                NavigationCameraState.OVERVIEW,
                NavigationCameraState.IDLE -> recenter.visibility = View.VISIBLE
            }
        }
    }

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

    private fun initialiseProgressUI() {
        // make sure to use the same DistanceFormatterOptions across different features
        val distanceFormatterOptions = mapboxNavigation.navigationOptions.distanceFormatterOptions

        // initialize maneuver api that feeds the data to the top banner maneuver view
        maneuverApi = MapboxManeuverApi(
            MapboxDistanceFormatter(distanceFormatterOptions)
        )

        // initialize bottom progress view
        tripProgressApi = MapboxTripProgressApi(
            TripProgressUpdateFormatter.Builder(this)
                .distanceRemainingFormatter(
                    DistanceRemainingFormatter(distanceFormatterOptions)
                )
                .timeRemainingFormatter(
                    TimeRemainingFormatter(this)
                )
                .percentRouteTraveledFormatter(
                    PercentDistanceTraveledFormatter()
                )
                .estimatedTimeToArrivalFormatter(
                    EstimatedTimeToArrivalFormatter(this, TimeFormat.NONE_SPECIFIED)
                )
                .build()
        )
    }

    private fun initialiseVoiceUI() {
        // initialize voice instructions api and the voice instruction player
        speechApi = MapboxSpeechApi(
            this,
            getString(R.string.mapbox_access_token),
            Locale.US.language
        )
        voiceInstructionsPlayer = MapboxVoiceInstructionsPlayer(
            this,
            getString(R.string.mapbox_access_token),
            Locale.US.language
        )
    }

    private fun initialiseRouteLineUI() {
        // initialize route line, the withRouteLineBelowLayerId is specified to place
        // the route line below road labels layer on the map
        // the value of this option will depend on the style that you are using
        // and under which layer the route line should be placed on the map layers stack
        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withVanishingRouteLineEnabled(true)
            .withRouteLineResources(routeLineResources)
            .withRouteLineBelowLayerId(getString(R.string.road_label))
            .build()
        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        // initialize maneuver arrow view to draw arrows on the map
        val routeArrowOptions = RouteArrowOptions.Builder(this).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)
    }

    private fun initViewListener() {
        // initialize view interactions
        stop.setOnClickListener {
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)
            clearRouteAndStopNavigation()
            finish()
        }
        recenter.setOnClickListener {
            navigationCamera.requestNavigationCameraToFollowing()
            routeOverview.showTextAndExtend(Constants.BUTTON_ANIMATION_DURATION)
        }
        routeOverview.setOnClickListener {
            navigationCamera.requestNavigationCameraToOverview()
            recenter.showTextAndExtend(Constants.BUTTON_ANIMATION_DURATION)
        }
        soundButton.setOnClickListener {
            // mute/unmute voice instructions
            isVoiceInstructionsMuted = !isVoiceInstructionsMuted
        }

        // set initial sounds button state
        soundButton.unmute()
    }

    private fun initStyle() {
        // load map style
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS
        )
    }

    /**
     * Start activity
     */
    override fun onStart() {
        super.onStart()

        // register event listeners
        navigationViewModel.registerObservers(
            routesObserver,
            routeProgressObserver,
            locationObserver,
            voiceInstructionsObserver
        )

        setRouteAndStartNavigation()
    }

    private fun setRouteAndStartNavigation() {
        // show UI elements
        soundButton.visibility = View.VISIBLE
        routeOverview.visibility = View.VISIBLE
        tripProgressCard.visibility = View.VISIBLE

        navigationViewModel.setRouteAndStartNavigation()
        // move the camera to overview when new route is available
        navigationCamera.requestNavigationCameraToOverview()
    }

    private fun clearRouteAndStopNavigation() {

        navigationViewModel.clearRouteAndStopNavigation()

        // hide UI elements
        soundButton.visibility = View.INVISIBLE
        maneuverView.visibility = View.INVISIBLE
        routeOverview.visibility = View.INVISIBLE
        tripProgressCard.visibility = View.INVISIBLE
    }

    /**
     * Called before termination of activity
     */
    override fun onStop() {
        super.onStop()

        // unregister event listeners to prevent leaks or unnecessary resource consumption
        navigationViewModel.unregisterObservers(
            routesObserver,
            routeProgressObserver,
            locationObserver,
            voiceInstructionsObserver
        )
    }

    /**
     * Terminate the Navigation
     */
    override fun onDestroy() {
        super.onDestroy()

        locationComponent.removeOnIndicatorPositionChangedListener(onPositionChangedListener)
        navigationViewModel.finishMapboxReplayer()
        maneuverApi.cancel()
        routeLineApi.cancel()
        routeLineView.cancel()
        speechApi.cancel()
        voiceInstructionsPlayer.shutdown()
        navigationViewModel.destroyMapboxNavigation()
    }

    /**
     * Terminate Navigation activity when back button is clicked and go to Loading activity
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)
        clearRouteAndStopNavigation()
        finish()
    }

    /**
     * Terminate the Navigation
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
