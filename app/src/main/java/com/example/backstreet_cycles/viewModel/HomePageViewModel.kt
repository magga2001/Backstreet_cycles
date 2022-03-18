package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.model.HomePageRepository
import com.example.backstreet_cycles.model.JourneyRepository
import com.example.backstreet_cycles.model.LocationRepository
import com.example.backstreet_cycles.views.HomePageActivity
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.navigation.core.MapboxNavigation

class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    private val homePageRepository: HomePageRepository = HomePageRepository(application)
    private val locationRepository: LocationRepository = LocationRepository(application)
    var stops: MutableLiveData<MutableList<Locations>> = MutableLiveData(locationRepository.getStops())

    private val mapRepository: JourneyRepository = JourneyRepository(application)
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = mapRepository.getIsReadyMutableLiveData()
    private val isReadyDockMutableLiveData: MutableLiveData<Boolean> = locationRepository.getIsReadyMutableLiveData()

    fun initialiseLocationComponent(mapboxMap: MapboxMap): LocationComponent
    {
        return homePageRepository.initialiseLocationComponent(mapboxMap)
    }

    fun initialiseCurrentLocation(loadedMapStyle: Style, locationComponent: LocationComponent)
    {
        homePageRepository.initialiseCurrentLocation(loadedMapStyle, locationComponent)
    }

    fun displayingAttractions(mapView: MapView, mapboxMap: MapboxMap, loadedMapStyle: Style, data: List<Locations>)
    {
        homePageRepository.displayingAttractions(mapView, mapboxMap, loadedMapStyle,data)
    }

    fun getCurrentLocation(locationComponent: LocationComponent): Location?
    {
        return homePageRepository.getCurrentLocation(locationComponent)
    }

    fun addStop(stop: Locations){
        locationRepository.addStop(stop)
    }

    fun addStop(index: Int, stop: Locations){
        locationRepository.addStop(index, stop)
    }

    fun removeStop(stop: Locations){
        locationRepository.removeStop(stop)
    }

    fun removeStopAt(index: Int){
        locationRepository.removeStopAt(index)
    }

    fun getTouristAttractions(): List<Locations> {
        return locationRepository.getTouristLocations()
    }

    fun initialisePlaceAutoComplete(activity: HomePageActivity): Intent
    {
        return homePageRepository.initialisePlaceAutoComplete(activity)
    }

    fun updateCamera(mapboxMap: MapboxMap, latitude: Double, longitude: Double)
    {
        homePageRepository.updateCamera(mapboxMap, latitude, longitude)
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

    fun checkPermission(context: Context, activity: Activity)
    {
        mapRepository.checkPermission(context, activity)
    }

    fun fetchRoute(context: Context, mapboxNavigation: MapboxNavigation, wayPoints: MutableList<Point>, profile: String, info:Boolean) {

        mapRepository.fetchRoute(context,mapboxNavigation, wayPoints, profile, info)
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

    fun getIsDockReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyDockMutableLiveData
    }

    fun getDocks()
    {
        locationRepository.getDocks()
    }
}