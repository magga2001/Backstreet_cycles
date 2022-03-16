package com.example.backstreet_cycles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.model.MapRepository
import com.example.backstreet_cycles.utils.TflHelper
import com.example.backstreet_cycles.views.JourneyActivity
import com.example.backstreet_cycles.viewModel.JourneyViewModel
import com.example.backstreet_cycles.viewModel.PlanJourneyViewModel
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.android.synthetic.main.bottom_sheet_navigation.*
import kotlinx.coroutines.*

class PlanJourneyActivity : AppCompatActivity() {

    private lateinit var planJourneyViewModel: PlanJourneyViewModel
    private lateinit var journeyViewModel: JourneyViewModel
    private lateinit var mapboxNavigation: MapboxNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_journey)
        initListener()

        journeyViewModel = ViewModelProvider(this).get(JourneyViewModel::class.java)
        planJourneyViewModel = ViewModelProvider(this).get(PlanJourneyViewModel::class.java)
        planJourneyViewModel.getIsReadyMutableLiveData().observe(this) {ready ->
            if(ready)
            {
                Log.i("Ready to load", "Success")
                planJourneyViewModel.getIsReadyMutableLiveData().value = false
            }
        }
        planJourneyViewModel.checkPermission(this, activity = this)
    }

    override fun onStart() {
        super.onStart()
        mapboxNavigation = planJourneyViewModel.initialiseMapboxNavigation()
    }

    private fun initListener()
    {
        val goButton = findViewById<Button>(R.id.goButton)
        goButton.setOnClickListener {

            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
            //THIS FUNCTION TO FETCH THE ORIGIN AND DESTINATION FROM EDIT TEXT
            lifecycleScope.launch {fetchPoints()}
        }
    }

    private fun fetchPoints()
    {
//        MapRepository.location.add(0, Locations("Current Location", 51.5390,-0.1426))
        MapRepository.location.add(Locations("Harrods", 51.5144, -0.1528))
        MapRepository.location.add(Locations("Tower Bridge", 51.5055, -0.0754))

        val checkForARunningJourney = journeyViewModel.addLocationSharedPreferences(MapRepository.location)
        if (checkForARunningJourney){
            alertDialog(MapRepository.location)
        } else{
            val locationPoints = setPoints(MapRepository.location)
            fetchRoute(locationPoints)
        }
    }

    private fun fetchRoute(wayPoints: MutableList<Point>) {

        planJourneyViewModel.fetchRoute(this, mapboxNavigation, wayPoints, "cycling", true)
        TflHelper.getDock(applicationContext)
    }

    private fun alertDialog(newStops: MutableList<Locations>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Planner Alert")
        builder.setMessage("There is already a planned journey that you are currently useing." +
                "Do you want to change the journey to the current one or keep the same one?")

        builder.setPositiveButton(R.string.continue_with_current_journey) { dialog, which ->
            journeyViewModel.getListLocations()
            val listPoints = setPoints(newStops)
            fetchRoute(listPoints)
        }

        builder.setNegativeButton(R.string.continue_with_newly_set_journey) { dialog, which ->
//
            val currentPoint = Point.fromLngLat(MapRepository.location[0].lon,MapRepository.location[0].lat)
            val stopOne = Point.fromLngLat(MapRepository.location[1].lon, MapRepository.location[1].lat)
            val stopTwo = Point.fromLngLat(MapRepository.location[2].lon, MapRepository.location[2].lat)
            fetchRoute(mutableListOf(currentPoint,stopOne,stopTwo))
        }
        builder.show()

    }

    private fun setPoints(newStops: MutableList<Locations>): MutableList<Point> {
        val listPoints = emptyList<Point>().toMutableList()
        for (i in 0 until newStops.size){
            listPoints.add(Point.fromLngLat(MapRepository.location[i].lon,MapRepository.location[i].lat))
        }
        return listPoints
    }
}
