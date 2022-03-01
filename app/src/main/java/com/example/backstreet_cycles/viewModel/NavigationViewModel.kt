package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.backstreet_cycles.model.JourneyHelper
import com.example.backstreet_cycles.model.NavigationHelper
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.view.MapboxTripProgressView
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer

class NavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val navigationRepository: NavigationHelper

    init {
        navigationRepository = NavigationHelper(application)
    }

    fun checkPermission(context: Context, activity: Activity)
    {
        navigationRepository.checkPermission(context, activity)
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return navigationRepository.initialiseMapboxNavigation()
    }

    fun initialiseLocationComponent(mapView: MapView) : LocationComponentPlugin
    {
        return navigationRepository.initialiseLocationComponent(mapView)
    }

    fun initialiseLocationObserver(navigationCamera: NavigationCamera, viewportDataSource: MapboxNavigationViewportDataSource): LocationObserver
    {
        return navigationRepository.initialiseLocationObserver(navigationCamera,viewportDataSource)
    }

    fun initialiseRouteProgressObserver(mapboxMap: MapboxMap,
                                        routeLineApi: MapboxRouteLineApi,
                                        routeLineView: MapboxRouteLineView,
                                        routeArrowApi: MapboxRouteArrowApi,
                                        routeArrowView: MapboxRouteArrowView,
                                        maneuverApi: MapboxManeuverApi,
                                        maneuverView: MapboxManeuverView,
                                        tripProgressApi: MapboxTripProgressApi,
                                        tripProgressView: MapboxTripProgressView,
                                        viewportDataSource: MapboxNavigationViewportDataSource): RouteProgressObserver
    {
        return navigationRepository.initialiseRouteProgressObserver(mapboxMap, routeLineApi, routeLineView, routeArrowApi, routeArrowView, maneuverApi,
        maneuverView, tripProgressApi, tripProgressView, viewportDataSource)
    }

    fun initialiseRoutesObserver(mapboxMap: MapboxMap,
                                 routeLineApi:MapboxRouteLineApi ,
                                 routeLineView: MapboxRouteLineView,
                                 routeArrowApi: MapboxRouteArrowApi,
                                 routeArrowView: MapboxRouteArrowView,
                                 viewportDataSource: MapboxNavigationViewportDataSource) : RoutesObserver
    {
        return navigationRepository.initialiseRoutesObserver(mapboxMap, routeLineApi, routeLineView, routeArrowApi, routeArrowView, viewportDataSource)
    }

    fun initialiseOnPositionChangedListener(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView): OnIndicatorPositionChangedListener
    {
        return navigationRepository.initialiseOnPositionChangedListener(mapboxMap, routeLineApi, routeLineView)
    }

    fun initialiseVoiceInstructionsObserver(speechApi: MapboxSpeechApi, voiceInstructionsPlayer: MapboxVoiceInstructionsPlayer): VoiceInstructionsObserver
    {
        return navigationRepository.initialiseVoiceInstructionsObserver(speechApi,voiceInstructionsPlayer)
    }

    fun initialiseRouteLineResources(): RouteLineResources
    {
        return navigationRepository.initialiseRouteLineResources()
    }

    fun setRouteAndStartNavigation(routes: List<DirectionsRoute>, mapboxNavigation: MapboxNavigation, navigationCamera: NavigationCamera) {
        navigationRepository.setRouteAndStartNavigation(routes,mapboxNavigation, navigationCamera)
    }

    fun clearRouteAndStopNavigation(mapboxNavigation: MapboxNavigation) {
        navigationRepository.clearRouteAndStopNavigation(mapboxNavigation)
    }

    fun registerObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          routeProgressObserver: RouteProgressObserver,
                          locationObserver: LocationObserver,
                          voiceInstructionsObserver: VoiceInstructionsObserver)
    {
        navigationRepository.registerObservers(mapboxNavigation, routesObserver, routeProgressObserver,
        locationObserver, voiceInstructionsObserver)
    }

    fun unregisterObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          routeProgressObserver: RouteProgressObserver,
                          locationObserver: LocationObserver,
                          voiceInstructionsObserver: VoiceInstructionsObserver)
    {
        navigationRepository.unregisterObservers(mapboxNavigation, routesObserver, routeProgressObserver,
            locationObserver, voiceInstructionsObserver)
    }

    fun finishMapboxReplayer()
    {
        navigationRepository.finishMapboxReplayer()
    }

    fun getReplayProgressObserver(): ReplayProgressObserver
    {
        return navigationRepository.getReplayProgressObserver()
    }

}