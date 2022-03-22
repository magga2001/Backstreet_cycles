package com.example.backstreet_cycles.presentation.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backstreet_cycles.domain.model.DTO.Locations
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.presentation.adapter.JourneyHistoryAdapter
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.presentation.viewModel.HomePageViewModel
import com.example.backstreet_cycles.presentation.viewModel.JourneyViewModel
import com.example.backstreet_cycles.presentation.viewModel.LoggedInViewModel
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.activity_journey_history.*

class JourneyHistoryActivity : AppCompatActivity() {

    private lateinit var mapboxMap: MapboxMap
    private lateinit var homePageViewModel: HomePageViewModel
    private lateinit var journeyViewModel: JourneyViewModel
    private lateinit var loggedInViewModel: LoggedInViewModel
    private lateinit var nAdapter: JourneyHistoryAdapter
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var locationComponent: LocationComponent
    private var journeys: MutableList<List<Locations>> = emptyList<List<Locations>>().toMutableList()
    private var stops: MutableList<Locations> = emptyList<Locations>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey_history)


        loggedInViewModel = ViewModelProvider(this)[LoggedInViewModel::class.java]
        journeyViewModel = ViewModelProvider(this)[JourneyViewModel::class.java]
        homePageViewModel = ViewModelProvider(this)[HomePageViewModel::class.java]

        homePageViewModel.getIsReadyMutableLiveData().observe(this) {ready ->
            if(ready)
            {
                startActivity(Intent(this, JourneyActivity::class.java))
                homePageViewModel.getIsReadyMutableLiveData().value = false
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            }
        }

        homePageViewModel.getIsDockReadyMutableLiveData().observe(this) { ready ->
            if (ready) {
                fetchPoints()
            }
        }

        loggedInViewModel.getUserDetails()
        loggedInViewModel.getUserDetailsMutableLiveData().observe(this) { userDetails ->
            if (userDetails != null) {
                journeys = journeyViewModel.getJourneyHistory(userDetails)
            }
            init()
        }

//        locationComponent = homePageViewModel.initialiseLocationComponent(mapboxMap)

    }

    fun init() {
        initCardView()
    }

    fun initCardView() {

        nAdapter = JourneyHistoryAdapter(journeys)
        nAdapter.setOnItemClickListener(object : JourneyHistoryAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                stops.addAll(journeys[position])
//                replaceCurrentLocation()
                homePageViewModel.getDocks()
            }
        })
        journey_history_recycler_view.layoutManager = LinearLayoutManager(this)
        journey_history_recycler_view.adapter = nAdapter
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        mapboxNavigation = homePageViewModel.initialiseMapboxNavigation()
    }

//    How to make an util class for those functions below!

//    private fun replaceCurrentLocation(){
//        val oldCurrentLocation = stops.find { it.name == "Current Location" }
//        if (oldCurrentLocation != null) {
//            val currentLocation  = homePageViewModel.getCurrentLocation(locationComponent)
//            Collections.replaceAll(stops,oldCurrentLocation,Locations("Current Location",currentLocation!!.latitude, currentLocation.longitude))
//        }
//    }

    private fun fetchPoints()
    {
        MapRepository.wayPoints.clear()
        MapRepository.currentRoute.clear()
        MapRepository.maneuvers.clear()

        MapRepository.location.addAll(stops)

        val checkForARunningJourney = journeyViewModel.addLocationSharedPreferences(MapRepository.location)
        if (checkForARunningJourney){
            alertDialog(MapRepository.location)
        } else{
            val locationPoints = setPoints(MapRepository.location)
            fetchRoute(locationPoints)
        }
    }

    private fun fetchRoute(wayPoints: MutableList<Point>) {

        homePageViewModel.fetchRoute(this, mapboxNavigation, wayPoints, "cycling", false)
    }

    private fun alertDialog(newStops: MutableList<Locations>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Planner Alert")
        builder.setMessage("There is already a planned journey that you are currently using." +
                "Do you want to change the journey to the current one or keep the same one?")

        builder.setPositiveButton(R.string.continue_with_current_journey) { dialog, which ->
            val listOfLocations = journeyViewModel.getListLocations().toMutableList()
            MapRepository.location = listOfLocations
            val listPoints = setPoints(listOfLocations)
            fetchRoute(listPoints)
        }

        builder.setNegativeButton(R.string.continue_with_newly_set_journey) { dialog, which ->
            val listPoints = setPoints(newStops)
            journeyViewModel.overrideListLocation(newStops)
            fetchRoute(listPoints)
        }
        builder.show()
    }

    private fun setPoints(newStops: MutableList<Locations>): MutableList<Point> {
        val listPoints = emptyList<Point>().toMutableList()
        for (i in 0 until newStops.size){
            listPoints.add(Point.fromLngLat(newStops[i].lon, newStops[i].lat))
        }
        return listPoints
    }
}