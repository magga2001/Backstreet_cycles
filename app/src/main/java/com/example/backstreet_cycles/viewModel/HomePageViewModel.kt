package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.example.backstreet_cycles.model.HomePageRepository
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    private val homePageRepository: HomePageRepository

    init {
        homePageRepository = HomePageRepository(application)
    }

    fun initialiseLocationComponent(mapboxMap: MapboxMap): LocationComponent
    {
        return homePageRepository.initialiseLocationComponent(mapboxMap)
    }

    fun initialiseCurrentLocation(loadedMapStyle: Style, locationComponent: LocationComponent)
    {
        homePageRepository.initialiseCurrentLocation(loadedMapStyle, locationComponent)
    }

    fun displayingDocks(mapView: MapView, mapboxMap: MapboxMap, loadedMapStyle: Style, data: MutableList<MutableList<String>>)
    {
        homePageRepository.displayingDocks(mapView, mapboxMap, loadedMapStyle,data)
    }

    fun getCurrentLocation(locationComponent: LocationComponent): Location?
    {
        return homePageRepository.getCurrentLocation(locationComponent)
    }

    fun initialisePlaceAutoComplete(activity: Activity): Intent
    {
        return homePageRepository.initialisePlaceAutoComplete(activity)
    }

    fun updateCamera(mapboxMap: MapboxMap, latitude: Double, longitude: Double)
    {
        homePageRepository.updateCamera(mapboxMap, latitude, longitude)
    }
}