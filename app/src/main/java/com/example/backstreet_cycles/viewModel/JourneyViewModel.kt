package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.backstreet_cycles.model.JourneyHelper
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources

class JourneyViewModel(application: Application) : AndroidViewModel(application) {

    private val mapRepository: JourneyHelper

    init {
        mapRepository = JourneyHelper(application)
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

    fun initialiseRouteLineResources(): RouteLineResources
    {
        return mapRepository.initialiseRouteLineResources()
    }

    fun initialiseLocationComponent(mapView: MapView) : LocationComponentPlugin
    {
        return mapRepository.initialiseLocationComponent(mapView)
    }

    fun initialiseLocationObserver(mapView: MapView): LocationObserver
    {
        return mapRepository.initialiseLocationObserver(mapView)
    }

    fun initialiseOnPositionChangedListener(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView): OnIndicatorPositionChangedListener
    {
        return mapRepository.initialiseOnPositionChangedListener(mapboxMap, routeLineApi, routeLineView)
    }

    fun initialiseRoutesObserver(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView): RoutesObserver
    {
        return mapRepository.initialiseRoutesObserver(mapboxMap, routeLineApi, routeLineView)
    }

    fun initialiseRouteProgressObserver(mapboxMap: MapboxMap,
                                        routeLineApi: MapboxRouteLineApi,
                                        routeLineView: MapboxRouteLineView,
                                        routeArrowApi: MapboxRouteArrowApi,
                                        routeArrowView: MapboxRouteArrowView
    ): RouteProgressObserver
    {
        return mapRepository.initialiseRouteProgressObserver(mapboxMap, routeLineApi, routeLineView, routeArrowApi, routeArrowView)
    }

    fun checkPermission(context: Context, activity: Activity)
    {
        mapRepository.checkPermission(context, activity)
    }

    fun updateCamera(point: Point, bearing: Double?, mapView: MapView)
    {
        mapRepository.updateCamera(point, bearing, mapView)
    }

    fun addAnnotationToMap(context: Context, mapView: MapView)
    {
        mapRepository.addAnnotationToMap(context,mapView)
    }

    fun registerObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          locationObserver: LocationObserver,
                          routeProgressObserver: RouteProgressObserver)
    {
        mapRepository.registerObservers(mapboxNavigation,routesObserver,locationObserver,routeProgressObserver)
    }

    fun unregisterObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          locationObserver: LocationObserver,
                          routeProgressObserver: RouteProgressObserver)
    {
        mapRepository.unregisterObservers(mapboxNavigation,routesObserver,locationObserver,routeProgressObserver)
    }

}