package com.example.backstreet_cycles.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.util.HomepageHelper
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class HomepageViewModel(app: Application): AndroidViewModel(app){

    val mapView = MutableLiveData<MapView>()
    val mapboxMap = MutableLiveData<MapboxMap>()
    private val locationComponent = MutableLiveData<LocationComponent>()
    private val context = app

    init {
        locationComponent.value = HomepageHelper.getLocationComponent()
    }

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

    fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(
            SymbolLayer(fetchResourceString(R.string.SYMBOL_LAYER_ID),
                fetchResourceString(R.string.GeoJsonSourceLayerId)).withProperties(
            PropertyFactory.iconImage(fetchResourceString(R.string.SymbolIconId)),
            PropertyFactory.iconOffset(arrayOf(0f, -8f))
        ))

    }

    fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(fetchResourceString(R.string.GeoJsonSourceLayerId)))
    }

    private fun fetchResourceString(stringMessage: Int): String {
        return getApplication<Application>().resources.getString(stringMessage)
    }

    fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

}