package com.example.backstreet_cycles.ui.views

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.MapboxConstants
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.ui.viewModel.NavigationViewModel
import com.example.backstreet_cycles.domain.useCase.PermissionUseCase
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.*
import com.mapbox.navigation.ui.tripprogress.view.MapboxTripProgressView
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechVolume
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.activity_navigation.mapView
import java.util.*

class NavigationActivity : AppCompatActivity() {

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

    private lateinit var navigationViewModel: NavigationViewModel
    private lateinit var locationComponent:LocationComponentPlugin
    private lateinit var locationObserver: LocationObserver
    private lateinit var routeProgressObserver: RouteProgressObserver
    private lateinit var routesObserver: RoutesObserver
    private lateinit var voiceInstructionsObserver: VoiceInstructionsObserver
    private lateinit var replayProgressObserver: ReplayProgressObserver
    private lateinit var onPositionChangedListener: OnIndicatorPositionChangedListener
    private lateinit var mapboxNavigation: MapboxNavigation
    private val currentRoute = MapRepository.currentRoute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        MapboxNavigationProvider.destroy()

        navigationViewModel = ViewModelProvider(this).get(NavigationViewModel::class.java)
        PermissionUseCase.checkPermission(context = this, activity = this)

        mapboxMap = mapView.getMapboxMap()
        mapboxNavigation = navigationViewModel.initialiseMapboxNavigation()

        initialisation()

        // load map style
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS
        )

        // start the trip session to being receiving location updates in free drive
        // and later when a route is set also receiving route progress updates
        mapboxNavigation.startTripSession()
    }

    private fun initialisation()
    {
        initialiseNavigationUI()
        initialiseLocationPuck()
        initialiseCamera()
        initialiseObservers()
        initialisePadding()
        initialiseViewListener()
    }

    private fun initialiseObservers()
    {
        locationObserver = navigationViewModel.initialiseLocationObserver(
            navigationCamera,
            viewportDataSource)

        routeProgressObserver = navigationViewModel.initialiseRouteProgressObserver(
            mapboxMap,
            routeLineApi,
            routeLineView,
            routeArrowApi,
            routeArrowView,
            maneuverApi,
            maneuverView,
            tripProgressApi,
            tripProgressView,
            viewportDataSource)

        routesObserver = navigationViewModel.initialiseRoutesObserver(
            mapboxMap,
            routeLineApi,
            routeLineView,
            routeArrowApi,
            routeArrowView,
            viewportDataSource)

        replayProgressObserver = navigationViewModel.getReplayProgressObserver()
    }

    private fun initialiseNavigationUI()
    {
        initialiseProgressUI()
        initialiseVoiceUI()
        initialiseRouteLineUI()
    }

    private fun initialiseLocationPuck()
    {
        // initialize the location puck
        locationComponent = navigationViewModel.initialiseLocationComponent(mapView)
        onPositionChangedListener = navigationViewModel.initialiseOnPositionChangedListener(mapboxMap, routeLineApi, routeLineView)
        locationComponent.addOnIndicatorPositionChangedListener(onPositionChangedListener)
    }

    private fun initialiseCamera()
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
                NavigationCameraState.FOLLOWING -> recenter.visibility = View.INVISIBLE
                NavigationCameraState.TRANSITION_TO_OVERVIEW,
                NavigationCameraState.OVERVIEW,
                NavigationCameraState.IDLE -> recenter.visibility = View.VISIBLE
            }
        }
    }

    private fun initialisePadding()
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

    private fun initialiseProgressUI()
    {
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

    private fun initialiseVoiceUI()
    {
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

        voiceInstructionsObserver = navigationViewModel.initialiseVoiceInstructionsObserver(speechApi,voiceInstructionsPlayer)
    }

    private fun initialiseRouteLineUI()
    {
        // initialize route line, the withRouteLineBelowLayerId is specified to place
        // the route line below road labels layer on the map
        // the value of this option will depend on the style that you are using
        // and under which layer the route line should be placed on the map layers stack
        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withVanishingRouteLineEnabled(true)
            .withRouteLineResources(routeLineResources)
            .withRouteLineBelowLayerId("road-label")
            .build()
        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        // initialize maneuver arrow view to draw arrows on the map
        val routeArrowOptions = RouteArrowOptions.Builder(this).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)
    }

    private fun initialiseViewListener()
    {
        // initialize view interactions
        stop.setOnClickListener {
            val intent = Intent(this, JourneyActivity::class.java)
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

    override fun onStart() {
        super.onStart()

        // register event listeners
        navigationViewModel.registerObservers(
            mapboxNavigation,
            routesObserver,
            routeProgressObserver,
            locationObserver,
            voiceInstructionsObserver)

        //Pass in route here
        setRouteAndStartNavigation(currentRoute)
    }

    private fun setRouteAndStartNavigation(routes: List<DirectionsRoute>) {
        // show UI elements
        soundButton.visibility = View.VISIBLE
        routeOverview.visibility = View.VISIBLE
        tripProgressCard.visibility = View.VISIBLE

        navigationViewModel.setRouteAndStartNavigation(routes,mapboxNavigation, navigationCamera)
    }

    private fun clearRouteAndStopNavigation() {

        navigationViewModel.clearRouteAndStopNavigation(mapboxNavigation)

        // hide UI elements
        soundButton.visibility = View.INVISIBLE
        maneuverView.visibility = View.INVISIBLE
        routeOverview.visibility = View.INVISIBLE
        tripProgressCard.visibility = View.INVISIBLE
    }

    override fun onStop() {
        super.onStop()

        // unregister event listeners to prevent leaks or unnecessary resource consumption
        navigationViewModel.unregisterObservers(
            mapboxNavigation,
            routesObserver,
            routeProgressObserver,
            locationObserver,
            voiceInstructionsObserver)
    }

    override fun onDestroy() {
        super.onDestroy()

        locationComponent.removeOnIndicatorPositionChangedListener(onPositionChangedListener)
//        MapboxNavigationProvider.destroy()
        navigationViewModel.finishMapboxReplayer()
//        mapboxReplayer.finish()
        maneuverApi.cancel()
        routeLineApi.cancel()
        routeLineView.cancel()
        speechApi.cancel()
        voiceInstructionsPlayer.shutdown()
        mapboxNavigation.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, JourneyActivity::class.java)
        startActivity(intent)
        clearRouteAndStopNavigation()
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
    }
}
