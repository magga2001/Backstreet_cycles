package com.example.backstreet_cycles.view

//---------------------------------
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.adapter.LocationAdapter
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.utils.TouchScreenCallBack
import com.example.backstreet_cycles.viewModel.HomePageViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
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
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var mapboxMap: MapboxMap
    private lateinit var locationComponent: LocationComponent
    private lateinit var sheetBehavior: BottomSheetBehavior<*>
    private val REQUESTCODEAUTOCOMPLETE = 7171

    private lateinit var addsBtn: FloatingActionButton
    private lateinit var locationsList:ArrayList<Locations>
    private lateinit var locationAdapter: LocationAdapter

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var updateInfo:Boolean=false

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
    }

    private var pos:Int=0
    private fun addInfo(name:String, lat: Double, long: Double) {
        LayoutInflater.from(this)

        locationsList.add(Locations(name,lat, long))
        locationAdapter.notifyDataSetChanged()
        Toast.makeText(this,"adding user information scuees",Toast.LENGTH_SHORT).show()
    }

    private fun addAndRemoveInfo(name:String, lat: Double, long: Double) {
        locationsList.remove(locationsList[pos])
        LayoutInflater.from(this)
        locationsList.add(pos,Locations(name,lat, long))
        locationAdapter.notifyDataSetChanged()
        Toast.makeText(this,"adding user information scuees",Toast.LENGTH_SHORT).show()
    }

    private fun initialiseNavigationDrawer() {
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
    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_view)
        addsBtn = findViewById(R.id.addingBtn)
        locationsList = ArrayList()
        locationsList.add(Locations("Current Location",homePageViewModel.getCurrentLocation(locationComponent)!!.latitude,homePageViewModel.getCurrentLocation(locationComponent)!!.longitude))
        locationAdapter = LocationAdapter(locationsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = locationAdapter

            val swipeToDeleteCallBack = object : TouchScreenCallBack(){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (viewHolder.absoluteAdapterPosition != 0) {
                        val position = viewHolder.absoluteAdapterPosition
                        locationsList.removeAt(position)
                        recyclerView.adapter?.notifyItemRemoved(position)
                    }
                    else{
                        Toast.makeText(this@HomePageActivity, "Cannot remove location", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.absoluteAdapterPosition
                    val toPosition = target.absoluteAdapterPosition
                    Collections.swap(locationsList, fromPosition, toPosition)
                    recyclerView.adapter!!.notifyItemMoved(fromPosition,toPosition)
                    return true
                }

            }

            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)

            itemTouchHelper.attachToRecyclerView(recyclerView)






        addsBtn.setOnClickListener {
            val intent = homePageViewModel.initialisePlaceAutoComplete(activity = this)
            startActivityForResult(intent, REQUESTCODEAUTOCOMPLETE)
        }
        locationAdapter.setOnItemClickListener(object : LocationAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                updateInfo = true
                val intent = homePageViewModel.initialisePlaceAutoComplete(activity = this@HomePageActivity)
                startActivityForResult(intent, REQUESTCODEAUTOCOMPLETE)
                //locationAdapter.onBindViewHolder(locationAdapter.LocationViewHolder(),position)
                //locationList.remove(locationList[position])
                pos=position
            }

        })
    }

//    private fun updateBottomSheet()
//    {
//        mAdapter.updateList(docks)
//    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.MAPBOX_STREETS
        ) { style ->

            enableLocationComponent(style)

            homePageViewModel.displayingAttractions(mapView, mapboxMap, style, homePageViewModel.getTouristAttraction())
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

            if (updateInfo) {
                updateInfo=false
                addAndRemoveInfo(selectedCarmenFeature.placeName().toString(), selectedCarmenFeature.center()!!.latitude(), selectedCarmenFeature.center()!!.longitude())
                //docks = MapHelper.getClosestDocks(Point.fromLngLat(longitude,latitude))
                //updateBottomSheet()
            } else {
                addInfo(selectedCarmenFeature.placeName().toString(), selectedCarmenFeature.center()!!.latitude(), selectedCarmenFeature.center()!!.longitude())
            }
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