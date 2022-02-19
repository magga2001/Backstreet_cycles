package com.example.backstreet_cycles

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
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
import com.mapbox.mapboxsdk.location.LocationUpdate
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.coroutines.launch


class HomepageActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    private lateinit var viewModel : HomepageViewModel


    private val REQUEST_CODE_AUTOCOMPLETE = 7171
    private var permissionsManager: PermissionsManager? = null
    private var locationUpdate: LocationUpdate? = null
    private var fabLocationSearch: FloatingActionButton?=null
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_homepage)

        mapView = findViewById(R.id.mapView)

        viewModel = ViewModelProviders.of(this).get(HomepageViewModel::class.java)
        viewModel.mapView.observe(this, Observer {
            mapView = it })

        fabLocationSearch = findViewById(R.id.fab_location_search)

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
        mapboxSetStyle(mapboxMap)
    }

    private fun mapboxSetStyle(mapboxMap: MapboxMap) {
        mapboxMap.setStyle( Style.MAPBOX_STREETS ) { style ->
            enableLocationComponent(style)
            displayingDocks(mapboxMap, style)

            initSearchFab()
            viewModel.setUpSource(style)
            viewModel.setUpLayer(style)

            val drawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_location_on_red_24dp,
                null
            )
            val bitmapUtils = BitmapUtils.getBitmapFromDrawable(drawable)
            style.addImage(getString(R.string.SymbolIconId), bitmapUtils!!)
        }
    }

    private fun displayingDocks(mapboxMap: MapboxMap, loadedMapStyle: Style)
    {
        val symbolManager = mapView?.let { SymbolManager(it, mapboxMap, loadedMapStyle) }
        symbolManager?.iconAllowOverlap = true
        val bitmap = viewModel.bitmapFromDrawableRes(this,R.drawable.marker_map) as Bitmap
        loadedMapStyle.addImage(getString(R.string.myMarker), Bitmap.createScaledBitmap(bitmap, 10, 10, false))
        for(dock in Tfl.docks)
        {
            symbolManager?.create(
                SymbolOptions()
                    .withLatLng(LatLng(dock.lat, dock.lon))
                    .withIconImage(getString(R.string.myMarker))
            )
        }
    }

    private fun initSearchFab() {
        fabLocationSearch!!.setOnClickListener {
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

    /**
     * Check if permissions are enabled and if not request
     * @param loadedMapStyle style imported from Mapbox
     */
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            viewModel.setLocationOptions(loadedMapStyle)
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(this)
        }
    }

    /**
     * Applies user's permission to both App and PermissionsManager from Mapbox
     * @param requestCode Request code, obtained from the App
     * @param permissions Permissions, obtained from the App
     * @param grantResults Grants permissions, obtianed from the App
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Shows message to user that permission is required
     * @param permissionsToExplain Permission to Explain, obtained from the App
     */
    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        createToastMessage(R.string.user_location_permission_explanation)
    }

    /**
     * Enables location component if the user grants permission
     * @param granted if true, enable the location component
     */
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            viewModel.mapboxMap.value?.getStyle { style -> enableLocationComponent(style) }
        } else {
            createToastMessage(R.string.user_location_permission_not_granted)
            finish()
        }
    }

    /**
     * Creates a Toast message to be displayed
     * @param stringMessage Insert the string message extracted from the strings.xml
     */
    private fun createToastMessage(stringMessage: Int) {
        Toast.makeText(this, stringMessage, Toast.LENGTH_LONG)
            .show()
    }

}

