package com.example.backstreet_cycles.model

import android.app.Application
import android.location.Location
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.backstreet_cycles.R
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.bindgen.Expected
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.replay.route.ReplayRouteMapper
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.view.MapboxTripProgressView
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement
import com.mapbox.navigation.ui.voice.model.SpeechError
import com.mapbox.navigation.ui.voice.model.SpeechValue

class NavigationHelper(private val application: Application): MapHelper(application) {

    /**
     * Debug tool used to play, pause and seek route progress events that can be used to produce mocked location updates along the route.
     */
    private val mapboxReplayer = MapboxReplayer()

    /**
     * Debug tool that mocks location updates with an input from the [mapboxReplayer].
     */
    private val replayLocationEngine = ReplayLocationEngine(mapboxReplayer)

    /**
     * Debug observer that makes sure the replayer has always an up-to-date information to generate mock updates.
     */
    private val replayProgressObserver = ReplayProgressObserver(mapboxReplayer)

    override fun initialiseLocationComponent(mapView: MapView): LocationComponentPlugin
    {
        return mapView.location.apply {
            this.locationPuck = LocationPuck2D(
                bearingImage = ContextCompat.getDrawable(
                    application,
                    com.mapbox.services.android.navigation.ui.v5.R.drawable.ic_arrow_head
                )
            )
            setLocationProvider(navigationLocationProvider)
            enabled = true
        }
    }

    override fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            MapboxNavigationProvider.create(
                NavigationOptions.Builder(application)
                    .accessToken(application.getString(R.string.mapbox_access_token))
                    // comment out the location engine setting block to disable simulation
                    .locationEngine(replayLocationEngine)
                    .build()
            )
        }
    }

    fun initialiseLocationObserver(navigationCamera: NavigationCamera, viewportDataSource: MapboxNavigationViewportDataSource): LocationObserver
    {
        return object : LocationObserver {
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

    fun initialiseRouteProgressObserver(mapboxMap: MapboxMap,
                                        routeLineApi:MapboxRouteLineApi ,
                                        routeLineView: MapboxRouteLineView,
                                        routeArrowApi: MapboxRouteArrowApi,
                                        routeArrowView: MapboxRouteArrowView,
                                        maneuverApi: MapboxManeuverApi,
                                        maneuverView: MapboxManeuverView,
                                        tripProgressApi: MapboxTripProgressApi,
                                        tripProgressView: MapboxTripProgressView,
                                        viewportDataSource: MapboxNavigationViewportDataSource): RouteProgressObserver
    {
        return RouteProgressObserver { routeProgress ->
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
            maneuvers.fold(
                { error ->
                    Toast.makeText(
                        application,
                        error.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                },
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

    fun initialiseRoutesObserver(mapboxMap: MapboxMap,
                                 routeLineApi:MapboxRouteLineApi ,
                                 routeLineView: MapboxRouteLineView,
                                 routeArrowApi: MapboxRouteArrowApi,
                                 routeArrowView: MapboxRouteArrowView,
                                 viewportDataSource: MapboxNavigationViewportDataSource) : RoutesObserver
    {
        return RoutesObserver { routeUpdateResult ->
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

    fun initialiseVoiceInstructionsObserver(speechApi: MapboxSpeechApi,voiceInstructionsPlayer:MapboxVoiceInstructionsPlayer): VoiceInstructionsObserver
    {
        return VoiceInstructionsObserver { voiceInstructions ->
            speechApi.generate(voiceInstructions, speechCallback(speechApi,voiceInstructionsPlayer))
        }
    }

    private fun speechCallback(speechApi: MapboxSpeechApi, voiceInstructionsPlayer:MapboxVoiceInstructionsPlayer): MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>>
    {
        return MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> { expected ->
            expected.fold(
                { error ->
                    // play the instruction via fallback text-to-speech engine
                    voiceInstructionsPlayer.play(
                        error.fallback,
                        voiceInstructionsPlayerCallback(speechApi)
                    )
                },
                { value ->
                    // play the sound file from the external generator
                    voiceInstructionsPlayer.play(
                        value.announcement,
                        voiceInstructionsPlayerCallback(speechApi)
                    )
                }
            )
        }
    }

    private fun voiceInstructionsPlayerCallback(speechApi: MapboxSpeechApi): MapboxNavigationConsumer<SpeechAnnouncement>
    {
        return MapboxNavigationConsumer<SpeechAnnouncement> { value ->
            // remove already consumed file to free-up space
            speechApi.clean(value)
        }
    }

    fun setRouteAndStartNavigation(routes: List<DirectionsRoute>, mapboxNavigation: MapboxNavigation, navigationCamera: NavigationCamera) {
        // set routes, where the first route in the list is the primary route that
        // will be used for active guidance
        mapboxNavigation.setRoutes(routes)

        // start location simulation along the primary route
        startSimulation(routes.first())

        // move the camera to overview when new route is available
        navigationCamera.requestNavigationCameraToOverview()
    }

    fun clearRouteAndStopNavigation(mapboxNavigation: MapboxNavigation) {
        // clear
        mapboxNavigation.setRoutes(listOf())

        // stop simulation
        mapboxReplayer.stop()
    }

    private fun startSimulation(route: DirectionsRoute) {
        mapboxReplayer.run {
            stop()
            clearEvents()
            val replayEvents = ReplayRouteMapper().mapDirectionsRouteGeometry(route)
            pushEvents(replayEvents)
            seekTo(replayEvents.first())
            play()
        }
    }

    fun registerObservers(mapboxNavigation: MapboxNavigation,
                         routesObserver: RoutesObserver,
                         routeProgressObserver: RouteProgressObserver,
                         locationObserver: LocationObserver,
                         voiceInstructionsObserver: VoiceInstructionsObserver)
    {
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.registerLocationObserver(locationObserver)
        mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver)
        mapboxNavigation.registerRouteProgressObserver(replayProgressObserver)
    }

    fun unregisterObservers(mapboxNavigation: MapboxNavigation,
                           routesObserver: RoutesObserver,
                           routeProgressObserver: RouteProgressObserver,
                           locationObserver: LocationObserver,
                           voiceInstructionsObserver: VoiceInstructionsObserver)
    {
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.unregisterVoiceInstructionsObserver(voiceInstructionsObserver)
        mapboxNavigation.unregisterRouteProgressObserver(replayProgressObserver)
    }

    fun finishMapboxReplayer()
    {
        mapboxReplayer.finish()
    }

    fun getReplayProgressObserver(): ReplayProgressObserver
    {
        return replayProgressObserver
    }
}