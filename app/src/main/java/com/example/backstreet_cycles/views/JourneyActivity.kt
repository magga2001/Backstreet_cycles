package com.example.backstreet_cycles.views

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.adapter.PlanJourneyAdapter
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.interfaces.PlannerInterface
import com.example.backstreet_cycles.model.JourneyRepository
import com.example.backstreet_cycles.model.MapRepository
import com.example.backstreet_cycles.utils.MapHelper
import com.example.backstreet_cycles.utils.TflHelper
import com.example.backstreet_cycles.viewModel.JourneyViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.observable.eventdata.MapLoadingErrorEventData
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadErrorListener
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.route.RouteLayerConstants.TOP_LEVEL_ROUTE_LINE_LAYER_ID
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import kotlinx.android.synthetic.main.activity_journey.*
import kotlinx.android.synthetic.main.activity_journey.mapView
import kotlinx.android.synthetic.main.bottom_sheet_journey.*


class JourneyActivity : AppCompatActivity(), PlannerInterface {

    /**
     * RouteLine: This class is responsible for rendering route line related mutations generated
     * by the [routeLineApi]
     */
    private lateinit var routeLineView: MapboxRouteLineView

    /**
     * RouteLine: This class is responsible for generating route line related data which must be
     * rendered by the [routeLineView] in order to visualize the route line on the map.
     */
    private lateinit var routeLineApi: MapboxRouteLineApi

    /**
     * RouteArrow: This class is responsible for generating data related to maneuver arrows. The
     * data generated must be rendered by the [routeArrowView] in order to apply mutations to
     * the map.
     */
    private val routeArrowApi: MapboxRouteArrowApi = MapboxRouteArrowApi()

    /**
     * RouteArrow: This class is responsible for rendering the arrow related mutations generated
     * by the [routeArrowApi]
     */
    private lateinit var routeArrowView: MapboxRouteArrowView

    private lateinit var routeLineResources: RouteLineResources
    private lateinit var routesObserver: RoutesObserver
    private lateinit var routeProgressObserver: RouteProgressObserver
    private lateinit var locationObserver: LocationObserver
    private lateinit var locationComponent: LocationComponentPlugin
    private lateinit var onPositionChangedListener: OnIndicatorPositionChangedListener
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var journeyViewModel: JourneyViewModel
    private lateinit var sheetBehavior: BottomSheetBehavior<*>

    //    private lateinit var mAdapter: PlannerAdapter
    private lateinit var nAdapter: PlanJourneyAdapter
    private val currentRoute = MapRepository.currentRoute

    // ExpandableListView variables:
    private var expandableListView: ExpandableListView? = null
    private var expandableListAdapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)

        journeyViewModel = ViewModelProvider(this).get(JourneyViewModel::class.java)

        Log.i("mutable live data", journeyViewModel.getIsReadyMutableLiveData().value.toString())
        journeyViewModel.getIsReadyMutableLiveData().observe(this) { ready ->
            if (ready) {
                Log.i("Ready to update UI", "Success")
                updateUI()
                journeyViewModel.getIsReadyMutableLiveData().value = false
            }
        }

        journeyViewModel.checkPermission(this, activity = this)


        mapboxMap = mapView.getMapboxMap()
//        mapboxNavigation = journeyViewModel.initialiseMapboxNavigation()
//        init()
        mapboxNavigation = journeyViewModel.initialiseMapboxNavigation()
        init()
    }

    override fun onStart() {
        super.onStart()
        MapboxNavigationProvider.destroy()
        mapboxNavigation = journeyViewModel.initialiseMapboxNavigation()
    }

    private fun init() {

        initStyle()
        initRouteLineUI()
        initObservers()
        initialiseLocationPuck()
        initNavigation()
        initListeners()
        initBottomSheet()
    }

    private fun initObservers() {
        routesObserver = journeyViewModel.initialiseRoutesObserver(
            mapboxMap,
            routeLineApi,
            routeLineView
        )

        routeProgressObserver = journeyViewModel.initialiseRouteProgressObserver(
            mapboxMap,
            routeLineApi,
            routeLineView,
            routeArrowApi,
            routeArrowView
        )

        locationObserver = journeyViewModel.initialiseLocationObserver(mapView)
    }

    private fun initStyle() {
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS,
            {

                journeyViewModel.updateCamera(MapRepository.centerPoint, null, 12.0,mapView)
                journeyViewModel.addAnnotationToMap(this, mapView)
            },
            object : OnMapLoadErrorListener {
                @SuppressLint("LogNotTimber")
                override fun onMapLoadError(eventData: MapLoadingErrorEventData) {
                    Log.e(
                        JourneyActivity::class.java.simpleName,
                        "Error loading map: " + eventData.message
                    )
                }
            }
        )
    }

    private fun initialiseLocationPuck() {
        // initialize the location puck
        locationComponent = journeyViewModel.initialiseLocationComponent(mapView)

        onPositionChangedListener = journeyViewModel.initialiseOnPositionChangedListener(
            mapboxMap,
            routeLineApi,
            routeLineView
        )

        locationComponent.addOnIndicatorPositionChangedListener(onPositionChangedListener)
    }


    private fun initNavigation() {
//        mapboxNavigation.startTripSession()
        mapboxNavigation.setRoutes(currentRoute)
        journeyViewModel.registerObservers(
            mapboxNavigation,
            routesObserver,
            locationObserver,
            routeProgressObserver
        )
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {

        start_navigation.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            startActivity(intent)
        }

        overview_journey.setOnClickListener {

            clear()
            val points = setPoints(MapRepository.location)

            journeyViewModel.fetchRoute(context = this, mapboxNavigation, points, "walking", true)
        }

        santander_link.setOnClickListener {

            var intent = packageManager.getLaunchIntentForPackage("uk.gov.tfl.cyclehire")

            if(intent == null) {
                intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "https://play.google.com/store/apps/details?id=uk.gov.tfl.cyclehire"
                    )
                    setPackage("com.android.vending")
                }
            }

            startActivity(intent)

        }
    }

    private fun initRouteLineUI() {
        routeLineResources = journeyViewModel.initialiseRouteLineResources()

        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withVanishingRouteLineEnabled(true)
            .withRouteLineResources(routeLineResources)
            .withRouteLineBelowLayerId("road-label")
            .build()

        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        val routeArrowOptions = RouteArrowOptions.Builder(this)
            .withAboveLayerId(TOP_LEVEL_ROUTE_LINE_LAYER_ID)
            .build()

        routeArrowView = MapboxRouteArrowView(routeArrowOptions)
    }

    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_view_journey)

        nAdapter = PlanJourneyAdapter(this, MapRepository.location, this)
        plan_journey_recycling_view.layoutManager = LinearLayoutManager(this)
        plan_journey_recycling_view.adapter = nAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.journey_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.reload_button -> {
                TflHelper.getDock(context = this)
                finish()
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI() {
//        mapboxNavigation.setRoutes(currentRoute)
        routesObserver.onRoutesChanged(JourneyRepository.result)

        journeyViewModel.updateCamera(MapRepository.centerPoint, null, 12.0, mapView)
        journeyViewModel.removeAnnotations()
        journeyViewModel.addAnnotationToMap(context = this, mapView)
        nAdapter.updateList(MapRepository.location)

        //Refresh the map
        mapView.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        //For future interaction with current location
        locationComponent.removeOnIndicatorPositionChangedListener(onPositionChangedListener)
//        mapboxNavigation.stopTripSession()
        journeyViewModel.unregisterObservers(
            mapboxNavigation,
            routesObserver,
            locationObserver,
            routeProgressObserver
        )

        //To destroy and not let it overlap to next mapbox activity
        mapboxNavigation.setRoutes(listOf())
//        MapboxNavigationProvider.destroy()
        routeLineView.cancel()
        routeLineApi.cancel()
//        mapboxNavigation.onDestroy()
    }

    override fun onSelectedStop(location: Locations) {

        val currentPoint = Point.fromLngLat(-0.1426, 51.5390)

        val stop = Point.fromLngLat(location.lon, location.lat)

        val startDock = MapHelper.getClosestDocks(
            Point.fromLngLat(
                currentPoint.longitude(),
                currentPoint.latitude()
            ),
            1
        )
        val endDock = MapHelper.getClosestDocks(stop, 1)
        val pickUpDock = Point.fromLngLat(startDock.lon, startDock.lat)
        val dropOffDock = Point.fromLngLat(endDock.lon, endDock.lat)

        clear()

        journeyViewModel.fetchRoute(
            context = this,
            mapboxNavigation,
            mutableListOf(currentPoint, pickUpDock, dropOffDock, stop),
            "walking",
            false
        )
    }

    override fun onSelectedJourney(location: Locations, profile: String, points : MutableList<Point>) {
        Log.i("location name", location.name)

        clear()
        journeyViewModel.fetchRoute(context = this, mapboxNavigation, points, profile, true)
    }

    private fun setPoints(newStops: MutableList<Locations>): MutableList<Point> {
        val listPoints = emptyList<Point>().toMutableList()
        for (i in 0 until newStops.size){
            listPoints.add(Point.fromLngLat(MapRepository.location[i].lon, MapRepository.location[i].lat))
        }
        return listPoints
    }

    private fun clear() {
        MapRepository.wayPoints.clear()
        MapRepository.currentRoute.clear()
        MapRepository.maneuvers.clear()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        clear()
        MapRepository.location.clear()
    }
}