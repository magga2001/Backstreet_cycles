package com.example.backstreet_cycles

//---------------------------------

//import android.R
import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.DTO.Dock
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
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
import kotlin.math.abs
import kotlin.math.pow


class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {
    private lateinit var toggle : ActionBarDrawerToggle
    private val REQUESTCODEAUTOCOMPLETE = 7171
    var mapView: MapView? = null
    private var permissionsManager: PermissionsManager? = null
    private var mapboxMap: MapboxMap? = null
    private var locationComponent: LocationComponent? = null
    private var locationUpdate: LocationUpdate? = null
    var docks= Tfl.docks
    private val geoJsonSourceLayerId="GeoJsonSourceLayerId"
    private val symbolIconId = "SymbolIconId"
    // Public variables to store captured location
    var searchedLocationLat: Double = 0.0
    var searchedLocationLon: Double = 0.0
    private var toButton: Button? = null
    private var fromButton: Button? = null
    private var toTextView: TextView? = null
    private var fromTextView: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)
        //mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)

        lifecycleScope.launch {Log.i("Retrieve one data", Tfl.readDock("BikePoints_617").toString()) }
        Log.i("Retrieve data", Tfl.docks.size.toString())

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        toggle= ActionBarDrawerToggle(this, drawerLayout,R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.nav_home -> Toast.makeText(applicationContext, "clicked home", Toast.LENGTH_SHORT).show()
                R.id.profile -> Toast.makeText(applicationContext, "clicked view", Toast.LENGTH_SHORT).show()
                R.id.plan_journey -> Toast.makeText(applicationContext, "clicked sync", Toast.LENGTH_SHORT).show()
                R.id.about-> Toast.makeText(applicationContext, "clicked settings", Toast.LENGTH_SHORT).show()
                R.id.help -> Toast.makeText(applicationContext, "clicked trash", Toast.LENGTH_SHORT).show()
                R.id.logout-> Toast.makeText(applicationContext, "clicked settings", Toast.LENGTH_SHORT).show()

            }
            true
        }

        toButton = findViewById(R.id.toButton)
        fromButton= findViewById(R.id.fromButton)
        toTextView= findViewById(R.id.toTextView)
        fromTextView= findViewById(R.id.fromTextView)
        val button:Button?=findViewById(R.id.button)
        button!!.setOnClickListener{
            fromButton!!.setVisibility(View.VISIBLE)
            toButton!!.setVisibility(View.VISIBLE)
            fromTextView!!.setVisibility(View.VISIBLE)
            toTextView!!.setVisibility(View.VISIBLE)

            button.visibility = View.GONE
            val title : TextView?= findViewById(R.id.textView)
            title!!.visibility = View.GONE
        }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return true
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
        this.mapboxMap = mapboxMap
//        mapboxMap.addOnMapClickListener(this)
        mapboxMap.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->
            enableLocationComponent(style)
            displayingDocks(mapboxMap,style)

            initSearchFab()
            setUpSource(style)
            setUpLayer(style)

            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_location_on_red_24dp, null)
            val bitmapUtils = BitmapUtils.getBitmapFromDrawable(drawable)
            style.addImage(symbolIconId, bitmapUtils!!)
        }
    }

    private fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(SymbolLayer("SYMBOL_LAYER_ID", geoJsonSourceLayerId).withProperties(
            PropertyFactory.iconImage(symbolIconId),
            PropertyFactory.iconOffset(arrayOf(0f, -8f))
        ))

    }

    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(geoJsonSourceLayerId))

    }

    private fun initSearchFab() {
        toButton!!.setOnClickListener { _: View? ->
            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(
                    (if (Mapbox.getAccessToken() != null) Mapbox.getAccessToken() else getString(R.string.mapbox_access_token))!!
                ).placeOptions(
                    PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS)
                )
                .build(this@MainActivity)
            startActivityForResult(intent, REQUESTCODEAUTOCOMPLETE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUESTCODEAUTOCOMPLETE) {
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            if (mapboxMap != null) {
                val style= mapboxMap!!.style
                if (style!=null) {
                    val source = style.getSourceAs<GeoJsonSource>(geoJsonSourceLayerId)

                    // Save location data of searched location via search bar
                    searchedLocationLat = (selectedCarmenFeature.geometry() as Point?)!!.latitude()
                    searchedLocationLon = (selectedCarmenFeature.geometry() as Point?)!!.longitude()

                    source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson()))))
                    mapboxMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                        .target(LatLng(searchedLocationLat,
                            searchedLocationLon))
                            .zoom(14.0)
                            .build()), 4000)
                }
            }
            val lat = selectedCarmenFeature.center()?.latitude() as Double
            val long = selectedCarmenFeature.center()?.longitude() as Double
            toTextView!!.text = "${selectedCarmenFeature.placeName()}"
        }
    }

    private fun displayingDocks(mapboxMap: MapboxMap, loadedMapStyle: Style)
    {
        val symbolManager = mapView?.let { SymbolManager(it, mapboxMap, loadedMapStyle) }
        symbolManager?.iconAllowOverlap = true
        val bitmap = bitmapFromDrawableRes(this,R.drawable.marker_map) as Bitmap
        loadedMapStyle.addImage("myMarker", Bitmap.createScaledBitmap(bitmap, 10, 10, false))
        for(dock in docks)
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

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) { // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Enable the most basic pulsing styling by ONLY using
            // the `.pulseEnabled()` method
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .pulseEnabled(true)
                .build()

            // Get an instance of the component
            //val locationComponent: LocationComponent? = mapboxMap?.locationComponent
            locationComponent = mapboxMap?.locationComponent


            // Activate with options
            locationComponent?.activateLocationComponent(
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()
            )

            // Enable to make component visible
            locationComponent?.isLocationComponentEnabled = true

            // Set the component's camera mode
            locationComponent?.cameraMode = CameraMode.TRACKING

            // Set the component's render mode
            locationComponent?.renderMode = RenderMode.COMPASS

            //Log.i("Retrieve closest dock", getClosestDocks(10,0.01).size.toString())
            //Update to current location
            //locationComponent?.forceLocationUpdate(LocationUpdate.Builder().build())

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
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap?.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG)
                .show()
            finish()
        }
        //Log.i("Retrieve closest dock", getClosestDocks(10,0.01).size.toString())


    }

    private fun getCurrentLocation(): Location?
    {
        //Can call lat and lon from this function
        return locationComponent!!.lastKnownLocation
    }

    // Get all the docks around the current location
    private fun getRadiusDocks(radius: Double): MutableList<Dock>
    {
        val currentLat = getCurrentLocation()?.latitude as Double
        val currentLon = getCurrentLocation()?.longitude as Double

        return docks.filter { dock ->
            val dockLat = dock.lat
            val dockLon = dock.lon
            abs(dockLat - currentLat) <= radius && abs(dockLon - currentLon) <= radius
        }.toMutableList()
    }

    // Get relevant docks around the current location
    private fun getClosestDocks(numberOfDock: Int, radius: Double): MutableList<Dock>
    {
        val closestDocks = getRadiusDocks(radius)

        // Filtering out docks that don't have available spaces
        closestDocks.filter {it.nbSpaces != 0}

        closestDocks.sortBy {it.lat.pow(2.0) + it.lon.pow(2.0)}

        return closestDocks.subList(0, numberOfDock)
    }

    // Get all the docks around the searched location ie destination
    private fun getDestinationRadiusDocks(radius: Double): MutableList<Dock>
    {
        //Using location data of searched location via search bar
        val currentLat = searchedLocationLat
        val currentLon = searchedLocationLon

        return docks.filter { dock ->
            val dockLat = dock.lat
            val dockLon = dock.lon
            abs(dockLat - currentLat) <= radius && abs(dockLon - currentLon) <= radius
        }.toMutableList()
    }

    // Get relevant docks around the searched location ie destination
    private fun getDestinationClosestDocks(numberOfDock: Int, radius: Double): MutableList<Dock>
    {
        val closestDocks = getDestinationRadiusDocks(radius)

        // Filtering out docks that don't have available spaces
        closestDocks.filter {it.nbSpaces != 0}

        closestDocks.sortBy {it.lat.pow(2.0) + it.lon.pow(2.0)}

        return closestDocks.subList(0, numberOfDock)
    }


}