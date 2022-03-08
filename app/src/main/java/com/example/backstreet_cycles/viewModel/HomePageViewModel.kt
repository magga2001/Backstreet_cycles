package com.example.backstreet_cycles.viewModel

import android.app.Application
import android.content.Intent
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.model.HomePageRepository
import com.example.backstreet_cycles.model.LocationRepository
import com.example.backstreet_cycles.view.HomePageActivity
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    private val homePageRepository: HomePageRepository

    private val locationRepository: LocationRepository

    init {
        homePageRepository = HomePageRepository(application)
        locationRepository = LocationRepository(application)
    }

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

    fun getTouristAttraction(): List<Locations> {
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
}