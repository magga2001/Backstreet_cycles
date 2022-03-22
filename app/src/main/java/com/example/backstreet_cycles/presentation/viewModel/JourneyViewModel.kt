package com.example.backstreet_cycles.presentation.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.domain.model.DTO.Locations
import com.example.backstreet_cycles.domain.model.DTO.Users
import com.example.backstreet_cycles.data.repository.JourneyRepository
import com.example.backstreet_cycles.data.repository.LocationRepository
import com.example.backstreet_cycles.data.repository.MapRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources


class JourneyViewModel(application: Application) : AndroidViewModel(application) {

    private val journeyRepository: JourneyRepository
    private val locationRepository: LocationRepository
    private val isReadyMutableLiveData: MutableLiveData<Boolean>
    private val distanceMutableLiveData: MutableLiveData<String>
    private val durationMutableLiveData: MutableLiveData<String>
    private val priceMutableLiveData: MutableLiveData<String>

    private val firestore = Firebase.firestore

    init {
        journeyRepository = JourneyRepository(application, firestore)
        locationRepository = LocationRepository(application)
        isReadyMutableLiveData = journeyRepository.getIsReadyMutableLiveData()
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

    fun initialiseRoutesObserver(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView, viewportDataSource: MapboxNavigationViewportDataSource): RoutesObserver
    {
        return journeyRepository.initialiseRoutesObserver(mapboxMap, routeLineApi, routeLineView, viewportDataSource)
    }

    fun initialiseRouteProgressObserver(mapboxMap: MapboxMap,
                                        routeLineApi: MapboxRouteLineApi,
                                        routeLineView: MapboxRouteLineView,
                                        routeArrowApi: MapboxRouteArrowApi,
                                        routeArrowView: MapboxRouteArrowView,
                                        viewportDataSource: MapboxNavigationViewportDataSource
    ): RouteProgressObserver
    {
        return journeyRepository.initialiseRouteProgressObserver(mapboxMap, routeLineApi, routeLineView, routeArrowApi, routeArrowView,viewportDataSource)
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
                          routeProgressObserver: RouteProgressObserver)
    {
        journeyRepository.registerObservers(mapboxNavigation,routesObserver,routeProgressObserver)
    }

    fun unregisterObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          routeProgressObserver: RouteProgressObserver)
    {
        journeyRepository.unregisterObservers(mapboxNavigation,routesObserver,routeProgressObserver)
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

    fun addJourneyToJourneyHistory(locations: MutableList<Locations>, userDetails: Users) {
        journeyRepository.addJourneyToJourneyHistory(locations, userDetails)
    }

    fun getJourneyHistory(userDetails: Users) : MutableList<List<Locations>> {
        return journeyRepository.getJourneyHistory(userDetails)
    }

    fun clear()
    {
        MapRepository.wayPoints.clear()
        MapRepository.currentRoute.clear()
    }

}