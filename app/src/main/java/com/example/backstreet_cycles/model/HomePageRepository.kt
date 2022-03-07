package com.example.backstreet_cycles.model

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.Tfl
import com.example.backstreet_cycles.utils.BitmapHelper
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions

class HomePageRepository(private val application: Application) {

    companion object
    {
        lateinit var DepartPoint: Point
        lateinit var DestinationPoint: Point
    }

    fun initialiseLocationComponent(mapboxMap: MapboxMap): LocationComponent
    {
        return mapboxMap.locationComponent
    }

    @SuppressLint("MissingPermission")
    fun initialiseCurrentLocation(loadedMapStyle: Style, locationComponent: LocationComponent)
    {
        // Enable the most basic pulsing styling by ONLY using
        // the `.pulseEnabled()` method
        val customLocationComponentOptions = LocationComponentOptions.builder(application)
            .pulseEnabled(true)
            .build()

        // Activate with options
        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(application, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()
        )

        // Enable to make component visible
        locationComponent.isLocationComponentEnabled = true

        // Set the component's camera mode
        locationComponent.cameraMode = CameraMode.TRACKING

        // Set the component's render mode
        locationComponent.renderMode = RenderMode.COMPASS
    }

    fun displayingDocks(mapView: MapView, mapboxMap: MapboxMap, loadedMapStyle: Style, data:MutableList<MutableList<String>>)
    {
        val doube : Double? = data[1][1].toDoubleOrNull()

        val textSize = 10.0F
        val textColor = "black"

        val symbolManager = SymbolManager(mapView, mapboxMap, loadedMapStyle)
        symbolManager.iconAllowOverlap = true
        val bitmap = BitmapHelper.bitmapFromDrawableRes(application, R.drawable.marker_background) as Bitmap
        loadedMapStyle.addImage("myMarker", Bitmap.createScaledBitmap(bitmap, 10, 15, false))
        for (dock in data) {
            if(dock[1].toDoubleOrNull()!=null){
                symbolManager.create(
                    SymbolOptions()
                        .withLatLng(LatLng(dock[1].toDouble()!!, dock[2].toDouble()!!))
                        .withIconImage("myMarker")
                        .withTextField(dock[0])
                        .withTextSize(textSize)
                        .withTextColor(textColor)
                )
            }
        }
    }

    fun getCurrentLocation(locationComponent: LocationComponent): Location?
    {
        return locationComponent.lastKnownLocation
    }

    fun initialisePlaceAutoComplete(activity: Activity): Intent
    {
        return PlaceAutocomplete.IntentBuilder()
            .accessToken(application.getString(R.string.mapbox_access_token)).
            placeOptions(
                PlaceOptions.builder()
                    .backgroundColor(Color.parseColor("#EEEEEE"))
                    .limit(10)
                    .build(PlaceOptions.MODE_CARDS)
            )
            .build(activity)
    }

    fun updateCamera(mapboxMap: MapboxMap, latitude: Double, longitude: Double)
    {
        mapboxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(
                        LatLng(
                            latitude,
                            longitude
                        )
                    )
                    .zoom(14.0)
                    .build()
            ), 4000
        )
    }
}