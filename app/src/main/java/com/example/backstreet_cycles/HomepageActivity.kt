package com.example.backstreet_cycles

//---------------------------------

//import android.R
import android.content.Context
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.DrawableRes
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.util.HomepageHelper
import com.example.backstreet_cycles.viewModel.HomepageViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.LocationUpdate
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.coroutines.launch


class HomepageActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    private lateinit var viewModel : HomepageViewModel


    private val REQUEST_CODE_AUTOCOMPLETE = 7171
    var mapView: MapView? = null


//    private var locationComponent: LocationComponent? = null
    private var permissionsManager: PermissionsManager? = null
    private var locationUpdate: LocationUpdate? = null
    private var fabLocationSearch: FloatingActionButton?=null
    //var docks= Tfl.docks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_homepage)

        viewModel = ViewModelProviders.of(this).get(HomepageViewModel::class.java)

        fabLocationSearch = findViewById(R.id.fab_location_search)
        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        //mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        lifecycleScope.launch {Log.i("Retrieve one data", Tfl.readDock("BikePoints_617").toString()) }
        Log.i("Retrieve data", Tfl.docks.size.toString())
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        //Check again for refactoring
        viewModel.mapboxMap.value = mapboxMap
//        mapboxMap.addOnMapClickListener(this)
        mapboxMap.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->
            enableLocationComponent(style)
            displayingDocks(mapboxMap,style)

            initSearchFab()
            setUpSource(style!!)
            setUpLayer(style!!)

            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_location_on_red_24dp, null)
            val bitmapUtils = BitmapUtils.getBitmapFromDrawable(drawable)
            style!!.addImage(getString(R.string.SymbolIconId), bitmapUtils!!)
        }
    }

    private fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(SymbolLayer("SYMBOL_LAYER_ID", getString(R.string.GeoJsonSourceLayerId)).withProperties(
            PropertyFactory.iconImage(getString(R.string.SymbolIconId)),
            PropertyFactory.iconOffset(arrayOf(0f, -8f))
        ))

    }

    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(getString(R.string.GeoJsonSourceLayerId)))

    }

    private fun initSearchFab() {
        fabLocationSearch!!.setOnClickListener { v: View? ->
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(
                    (if (Mapbox.getAccessToken() != null) Mapbox.getAccessToken() else getString(R.string.mapbox_access_token))!!
                ).placeOptions(
                    PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS)
                )
                .build(this@HomepageActivity)
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            if(viewModel.mapboxMap.value!=null){
                val style= viewModel.mapboxMap.value!!.style
                if(style!=null){
                    val source = style.getSourceAs<GeoJsonSource>(getString(R.string.GeoJsonSourceLayerId))
                    source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson()))))
                    viewModel.mapboxMap.value!!.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                        .target(LatLng((selectedCarmenFeature.geometry() as Point?)!!.latitude(),
                            (selectedCarmenFeature.geometry() as Point?)!!.longitude()))
                            .zoom(14.0)
                            .build()), 4000)
                }
            }

        }
    }

    private fun displayingDocks(mapboxMap: MapboxMap, loadedMapStyle: Style)
    {
        val symbolManager = mapView?.let { SymbolManager(it, mapboxMap, loadedMapStyle) }
        symbolManager?.iconAllowOverlap = true
        val bitmap = bitmapFromDrawableRes(this,R.drawable.marker_map) as Bitmap
        loadedMapStyle.addImage("myMarker", Bitmap.createScaledBitmap(bitmap, 10, 10, false))
        for(dock in Tfl.docks)
        {
            symbolManager?.create(
                SymbolOptions()
                    .withLatLng(LatLng(dock.lat, dock.lon))
                    .withIconImage("myMarker")
            )
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
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

    private fun enableLocationComponent(loadedMapStyle: Style) { // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Enable the most basic pulsing styling by ONLY using
            // the `.pulseEnabled()` method
            viewModel.setLocationOptions(loadedMapStyle)

        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(this)
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        createToastMessage(R.string.user_location_permission_explanation)
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            viewModel.mapboxMap.value?.getStyle { style -> enableLocationComponent(style) }
        } else {
            createToastMessage(R.string.user_location_permission_not_granted)
            finish()
        }
    }

    private fun createToastMessage(stringMessage: Int) {
        Toast.makeText(this, stringMessage, Toast.LENGTH_LONG)
            .show()
    }

//    private fun getCurrentLocation(): Location?

//    {

//        //Can call lat and lon from this function
//        return locationComponent!!.lastKnownLocation
//    }

//    private fun getRadiusDocks(radius: Double): MutableList<Dock>
//    {
//        val currentLat = getCurrentLocation()?.latitude as Double
//        val currentLon = getCurrentLocation()?.longitude as Double
//
//        return Tfl.docks.filter { dock ->
//            val dockLat = dock.lat
//            val dockLon = dock.lon
//            abs(dockLat - currentLat) <= radius && abs(dockLon - currentLon) <= radius
//        }.toMutableList()
//    }

//    private fun getClosestDocks(numberOfDock: Int, radius: Double): MutableList<Dock>
//    {
//        val closestDocks = getRadiusDocks(radius)
//
//        closestDocks.sortBy {it.lat.pow(2.0) + it.lon.pow(2.0)}
//
//        return closestDocks.subList(0, numberOfDock)
//    }
}