package com.example.backstreet_cycles.viewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.util.HomepageHelper
import com.mapbox.mapboxsdk.location.LocationComponent
//import com.mapbox.android.core.permissions.PermissionsListener

import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

class HomepageViewModel(app: Application): AndroidViewModel(app){


    val mapboxMap = MutableLiveData<MapboxMap>()
    val locationComponent = MutableLiveData<LocationComponent>()
    //private var locationComponent: LocationComponent? = null

    private val context = app

    init {
        locationComponent.value = HomepageHelper.getLocationComponent()
    }

//    fun setLocationComponent(locationComponentIn: LocationComponent?) {
//        locationComponent.value = HomepageHelper.setLocationComponent(locationComponentIn)
//    }

    @SuppressLint("MissingPermission")
    fun setLocationOptions(loadedMapStyle: Style) {
        val customLocationComponentOptions = LocationComponentOptions.builder(context)
            .pulseEnabled(true)
            .build()

        // Get an instance of the component
        //val locationComponent: LocationComponent? = mapboxMap?.locationComponent

        //Refactor this part later
        locationComponent.value = mapboxMap.value?.locationComponent

        // Activate with options
        locationComponent.value?.activateLocationComponent(
            LocationComponentActivationOptions.builder(context, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()
        )

        // Enable to make component visible
        locationComponent.value?.isLocationComponentEnabled = true

        // Set the component's camera mode
        locationComponent.value?.cameraMode = CameraMode.TRACKING

        // Set the component's render mode
        locationComponent.value?.renderMode = RenderMode.COMPASS

        //            Log.i("Retrieve closest dock", getClosestDocks(10,0.01).size.toString())
        //Update to current location
        //locationComponent?.forceLocationUpdate(LocationUpdate.Builder().build())
    }



}