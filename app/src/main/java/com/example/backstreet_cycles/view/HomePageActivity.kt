package com.example.backstreet_cycles.view

//---------------------------------

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.adapter.DockAdapter
import com.example.backstreet_cycles.adapter.StopsAdapter
import com.example.backstreet_cycles.dto.Dock
import com.example.backstreet_cycles.model.HomePageRepository
import com.example.backstreet_cycles.utils.MapHelper
import com.example.backstreet_cycles.viewModel.HomePageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.homepage_bottom_sheet.*
import java.util.*


class HomePageActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    private lateinit var homePageViewModel: HomePageViewModel
    private lateinit var docks: MutableList<Dock>
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var mapboxMap: MapboxMap
    private lateinit var locationComponent: LocationComponent
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private lateinit var mAdapter: DockAdapter

//    Currently in progress
    private lateinit var stops: MutableList<String>
    private lateinit var sAdapter: StopsAdapter

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var changeTo: Boolean? = false
    private var changeFrom: Boolean? = false

    companion object {
        private const val geoJsonSourceLayerId = "GeoJsonSourceLayerId"
        private const val symbolIconId = "SymbolIconId"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_homepage)

        homePageViewModel = ViewModelProvider(this).get(HomePageViewModel::class.java)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(this)

        initialiseNavigationDrawer()
        initialiseView()
        initialiseListeners()





        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(closest_dock_recycling_view)
    }

    private fun initialiseNavigationDrawer()
    {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> Toast.makeText(
                    applicationContext,
                    "clicked home",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.profile -> Toast.makeText(
                    applicationContext,
                    "clicked view",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.plan_journey -> Toast.makeText(
                    applicationContext,
                    "clicked sync",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.about -> Toast.makeText(
                    applicationContext,
                    "clicked settings",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.help -> Toast.makeText(applicationContext, "clicked trash", Toast.LENGTH_SHORT)
                    .show()
                R.id.logout -> Toast.makeText(
                    applicationContext,
                    "clicked settings",
                    Toast.LENGTH_SHORT
                ).show()

            }
            true
        }
    }

    private fun initialiseView()
    {
        //Relative Layout
        locationLayout.visibility = View.GONE
        backButton.visibility = View.GONE
    }

    private fun initialiseListeners()
    {
        initialiseViewListeners()
        initialiseSearchBarListeners()
    }

    private fun initialiseSearchBarListeners()
    {
        val REQUESTCODEAUTOCOMPLETE = 7171

        fromButton.setOnClickListener {
            val intent = homePageViewModel.initialisePlaceAutoComplete(activity = this)
            changeFrom = true
            startActivityForResult(intent, REQUESTCODEAUTOCOMPLETE)
        }

        toButton.setOnClickListener {
            val intent = homePageViewModel.initialisePlaceAutoComplete(activity = this)
            changeTo = true
            startActivityForResult(intent, REQUESTCODEAUTOCOMPLETE)
        }
    }

    private fun initialiseViewListeners()
    {
        planJourneyButton.setOnClickListener {
            locationLayout.visibility = View.VISIBLE
            fromButton!!.visibility = View.VISIBLE
            toButton!!.visibility = View.VISIBLE
            fromTextView!!.visibility = View.VISIBLE
            toTextView!!.visibility = View.VISIBLE
            backButton.visibility = View.VISIBLE

            planJourneyButton.visibility = View.GONE
        }

        backButton.setOnClickListener {
            locationLayout.visibility = View.GONE
            fromButton!!.visibility = View.GONE
            toButton!!.visibility = View.GONE
            fromTextView!!.visibility = View.GONE
            toTextView!!.visibility = View.GONE
            backButton.visibility = View.GONE

            planJourneyButton.visibility = View.VISIBLE
        }
    }


    val simpleCallback: SimpleCallback = object : SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
        0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Collections.swap(stops, fromPosition, toPosition)
            recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    }

    private fun initBottomSheet()
    {
        stops = listOf("Stop1","Stop2","Stop3","Stop4") as MutableList<String>
        sAdapter = StopsAdapter(stops)
        closest_dock_recycling_view.layoutManager = LinearLayoutManager(this)
        closest_dock_recycling_view.adapter = sAdapter
//        docks = MapHelper.getClosestDocks(Point.fromLngLat(longitude,latitude))

//        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_view)
//        mAdapter = DockAdapter(docks)
//        closest_dock_recycling_view.layoutManager = LinearLayoutManager(this)
//        closest_dock_recycling_view.adapter = mAdapter

    }

    private fun updateBottomSheet()
    {
        mAdapter.updateList(docks)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->

            enableLocationComponent(style)
            homePageViewModel.displayingDocks(mapView, mapboxMap, style)
            //init search fab()
            setUpSource(style)
            setUpLayer(style)

            val drawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_location_on_red_24dp,
                null
            )
            val bitmapUtils = BitmapUtils.getBitmapFromDrawable(drawable)
            style.addImage(symbolIconId, bitmapUtils!!)
        }
    }

    private fun setUpLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(
            SymbolLayer("SYMBOL_LAYER_ID", geoJsonSourceLayerId).withProperties(
                PropertyFactory.iconImage(symbolIconId),
                PropertyFactory.iconOffset(arrayOf(0f, -8f))
            )
        )
    }

    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(geoJsonSourceLayerId))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val REQUESTCODEAUTOCOMPLETE = 7171

        if (resultCode == RESULT_OK && requestCode == REQUESTCODEAUTOCOMPLETE) {
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            val style = mapboxMap.style
            if (style != null) {

                latitude = selectedCarmenFeature.center()!!.latitude()
                longitude = selectedCarmenFeature.center()!!.longitude()

                style.getSourceAs<GeoJsonSource>(geoJsonSourceLayerId)?.setGeoJson(
                    FeatureCollection.fromFeatures(
                        arrayOf(
                            Feature.fromJson(
                                selectedCarmenFeature.toJson()
                            )
                        )
                    )
                )
                homePageViewModel.updateCamera(mapboxMap, latitude, longitude)
            }
            selectedCarmenFeature.placeName()?.let { updateSearchBar(latitude, longitude, it) }

            docks = MapHelper.getClosestDocks(Point.fromLngLat(longitude,latitude))

            updateBottomSheet()
        }
    }

    private fun updateSearchBar(latitude: Double, longitude: Double, name: String)
    {
        val point =  Point.fromLngLat(longitude, latitude)

        if(changeFrom!!){
            fromButton.text = name
            title_homepage.text = getString(R.string.Depart)
            HomePageRepository.DepartPoint = point
            changeFrom = false
        }
        else if(changeTo!!)
        {
            toButton.text = name
            title_homepage.text = getString(R.string.Arrive)
            HomePageRepository.DestinationPoint = point
            changeTo = false
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) { // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            locationComponent = homePageViewModel.initialiseLocationComponent(mapboxMap)
            homePageViewModel.initialiseCurrentLocation(loadedMapStyle, locationComponent)

            longitude = homePageViewModel.getCurrentLocation(locationComponent)!!.longitude
            latitude = homePageViewModel.getCurrentLocation(locationComponent)!!.latitude

            initBottomSheet()

        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initBottomSheet()
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
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
}