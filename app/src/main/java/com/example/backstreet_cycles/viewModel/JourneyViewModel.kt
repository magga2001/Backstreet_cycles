package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.dto.Users
import com.example.backstreet_cycles.model.JourneyRepository
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
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources

class JourneyViewModel(application: Application) : AndroidViewModel(application) {

    private val journeyRepository: JourneyRepository
    private val isReadyMutableLiveData: MutableLiveData<Boolean>
    private val firestore = Firebase.firestore

    init {
        journeyRepository = JourneyRepository(application, firestore)
        isReadyMutableLiveData = journeyRepository.getIsReadyMutableLiveData()
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return journeyRepository.initialiseMapboxNavigation()
    }

    fun fetchRoute(context: Context, mapboxNavigation: MapboxNavigation, points: MutableList<Point>, profile: String, overview: Boolean)
    {
        journeyRepository.fetchRoute(context,mapboxNavigation,points, profile, overview)
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

}