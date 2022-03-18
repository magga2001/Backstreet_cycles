package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.model.JourneyRepository
import com.example.backstreet_cycles.model.LocationRepository
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

    private val journeyRepository: JourneyRepository
    private val locationRepository: LocationRepository
    private val isReadyMutableLiveData: MutableLiveData<Boolean>
    private val isReadyDockMutableLiveData: MutableLiveData<Boolean>
    private val distanceMutableLiveData: MutableLiveData<String>
    private val durationMutableLiveData: MutableLiveData<String>
    private val priceMutableLiveData: MutableLiveData<String>


    init {
        journeyRepository = JourneyRepository(application)
        locationRepository = LocationRepository(application)
        isReadyMutableLiveData = journeyRepository.getIsReadyMutableLiveData()
        isReadyDockMutableLiveData = locationRepository.getIsReadyMutableLiveData()
        distanceMutableLiveData = journeyRepository.getDistanceMutableLiveData()
        durationMutableLiveData = journeyRepository.getDurationMutableLiveData()
        priceMutableLiveData = journeyRepository.getPriceMutableLiveData()
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return journeyRepository.initialiseMapboxNavigation()
    }

    fun fetchRoute(context: Context, mapboxNavigation: MapboxNavigation, points: MutableList<Point>, profile: String, info: Boolean)
    {
        journeyRepository.fetchRoute(context,mapboxNavigation,points, profile, info)
    }

    fun initialiseRouteLineResources(): RouteLineResources
    {
        return journeyRepository.initialiseRouteLineResources()
    }

    fun initialiseLocationComponent(mapView: MapView) : LocationComponentPlugin
    {
        return journeyRepository.initialiseLocationComponent(mapView)
    }

    fun initialiseLocationObserver(mapView: MapView): LocationObserver
    {
        return journeyRepository.initialiseLocationObserver(mapView)
    }

    fun initialiseOnPositionChangedListener(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView): OnIndicatorPositionChangedListener
    {
        return journeyRepository.initialiseOnPositionChangedListener(mapboxMap, routeLineApi, routeLineView)
    }

    fun initialiseRoutesObserver(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView): RoutesObserver
    {
        return journeyRepository.initialiseRoutesObserver(mapboxMap, routeLineApi, routeLineView)
    }

    fun initialiseRouteProgressObserver(mapboxMap: MapboxMap,
                                        routeLineApi: MapboxRouteLineApi,
                                        routeLineView: MapboxRouteLineView,
                                        routeArrowApi: MapboxRouteArrowApi,
                                        routeArrowView: MapboxRouteArrowView
    ): RouteProgressObserver
    {
        return journeyRepository.initialiseRouteProgressObserver(mapboxMap, routeLineApi, routeLineView, routeArrowApi, routeArrowView)
    }

    fun checkPermission(context: Context, activity: Activity)
    {
        journeyRepository.checkPermission(context, activity)
    }

    fun updateCamera(point: Point, bearing: Double?, zoomLevel:Double, mapView: MapView)
    {
        journeyRepository.updateCamera(point, bearing, zoomLevel, mapView)
    }

    fun addAnnotationToMap(context: Context, mapView: MapView)
    {
        journeyRepository.addAnnotationToMap(context,mapView)
    }


    fun removeAnnotations()
    {
        journeyRepository.removeAnnotations()
    }

    fun registerObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          locationObserver: LocationObserver,
                          routeProgressObserver: RouteProgressObserver)
    {
        journeyRepository.registerObservers(mapboxNavigation,routesObserver,locationObserver,routeProgressObserver)
    }

    fun unregisterObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          locationObserver: LocationObserver,
                          routeProgressObserver: RouteProgressObserver)
    {
        journeyRepository.unregisterObservers(mapboxNavigation,routesObserver,locationObserver,routeProgressObserver)
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

    fun getDistanceMutableLiveData(): MutableLiveData<String>
    {
        return distanceMutableLiveData
    }

    fun getDurationMutableLiveData(): MutableLiveData<String>
    {
        return durationMutableLiveData
    }

    fun getPriceMutableLiveData(): MutableLiveData<String>
    {
        return priceMutableLiveData
    }

    fun addLocationSharedPreferences(locations: MutableList<Locations>): Boolean {
        return journeyRepository.addLocationSharedPreferences(locations)
    }

    fun getListLocations(): List<Locations> {
        return journeyRepository.getListLocations()
    }

    fun overrideListLocation(locations: MutableList<Locations>){
        journeyRepository.overrideListLocation(locations)
    }

    fun clearListLocations(){
        journeyRepository.clearListLocations()
    }

    fun getIsReadyDockMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyDockMutableLiveData
    }

    fun getDocks()
    {
        locationRepository.getDocks()
    }

}