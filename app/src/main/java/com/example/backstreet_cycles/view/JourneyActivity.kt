package com.example.backstreet_cycles.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.adapter.ManeuverAdapter
import com.example.backstreet_cycles.model.MapRepository
import com.example.backstreet_cycles.viewModel.JourneyViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import kotlinx.android.synthetic.main.journey_bottom_sheet.*


class JourneyActivity : AppCompatActivity() {

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
    private lateinit var mAdapter: ManeuverAdapter
    private val currentRoute = MapRepository.currentRoute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)
        journeyViewModel = ViewModelProvider(this).get(JourneyViewModel::class.java)

        mapboxMap = mapView.getMapboxMap()
        mapboxNavigation = journeyViewModel.initialiseMapboxNavigation()
        journeyViewModel.checkPermission(this, activity = this)
        init()
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

    private fun initObservers()
    {
        routesObserver = journeyViewModel.initialiseRoutesObserver(
            mapboxMap,
            routeLineApi,
            routeLineView)

        routeProgressObserver = journeyViewModel.initialiseRouteProgressObserver(
            mapboxMap,
            routeLineApi,
            routeLineView,
            routeArrowApi,
            routeArrowView)

        locationObserver = journeyViewModel.initialiseLocationObserver(mapView)
    }

    private fun initStyle() {
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS,
            {
                journeyViewModel.updateCamera(MapRepository.centerPoint, null, mapView)
                startNavigation?.visibility = View.VISIBLE
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

    private fun initialiseLocationPuck()
    {
        // initialize the location puck
        locationComponent = journeyViewModel.initialiseLocationComponent(mapView)

        onPositionChangedListener = journeyViewModel.initialiseOnPositionChangedListener(
            mapboxMap,
            routeLineApi,
            routeLineView)

        locationComponent.addOnIndicatorPositionChangedListener(onPositionChangedListener)
    }


    private fun initNavigation() {
//        mapboxNavigation.startTripSession()
        mapboxNavigation.setRoutes(currentRoute)
        journeyViewModel.registerObservers(
            mapboxNavigation,
            routesObserver,
            locationObserver,
            routeProgressObserver)

    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        startNavigation?.text = "Start Navigation"
        startNavigation?.setOnClickListener {
            val intent = Intent(this, NavigationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRouteLineUI()
    {
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

    private fun initBottomSheet()
    {
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_view)
        mAdapter = ManeuverAdapter(this, MapRepository.maneuvers)
        maneuver_recycling_view.layoutManager = LinearLayoutManager(this)
        maneuver_recycling_view.adapter = mAdapter
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
        MapboxNavigationProvider.destroy()
        routeLineView.cancel()
        routeLineApi.cancel()
        mapboxNavigation.onDestroy()
//    }
    }
}