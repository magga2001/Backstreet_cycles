package com.example.backstreet_cycles

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.dto.Dock
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.model.MapRepository
import com.example.backstreet_cycles.utils.TflHelper
import com.example.backstreet_cycles.view.JourneyActivity
import com.example.backstreet_cycles.viewModel.PlanJourneyViewModel
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import kotlinx.android.synthetic.main.activity_plan_journey.*
import kotlinx.coroutines.*

class PlanJourneyActivity : AppCompatActivity() {

    private lateinit var planJourneyViewModel: PlanJourneyViewModel
    private lateinit var mapboxNavigation: MapboxNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_journey)
        initListener()

        planJourneyViewModel = ViewModelProvider(this).get(PlanJourneyViewModel::class.java)
        planJourneyViewModel.getIsReadyMutableLiveData().observe(this) {ready ->
            if(ready)
            {
                Log.i("Ready to load", "Success")
                loadActivity()
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
//            when {
//                TextUtils.isEmpty(et_startDock.text.toString()) -> {
//                    et_startDock.error = "Please enter your starting dock"
//                }
//                TextUtils.isEmpty(et_endDock.text.toString()) -> {
//                    et_endDock.error = "Please enter your destination dock"
//                }
//                else -> {
//
//                    Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
//                    //THIS FUNCTION TO FETCH THE ORIGIN AND DESTINATION FROM EDIT TEXT
//                    lifecycleScope.launch {fetchPoints()}
//
//                }
//            }

            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
            //THIS FUNCTION TO FETCH THE ORIGIN AND DESTINATION FROM EDIT TEXT
            lifecycleScope.launch {fetchPoints()}
        }
    }

    private suspend fun fetchPoints()
    {
//        var startDock:Dock? = null
//        var endDock:Dock? = null
//
//        coroutineScope {
//            val start = async { Tfl.readDock(et_startDock.text.toString())}
//            val end = async {  Tfl.readDock(et_endDock.text.toString()) }
//
//            startDock = start.await()
//            endDock = end.await()
//
//            if(startDock == null || endDock == null)
//            {
//                Toast.makeText(this@PlanJourneyActivity,"No location", Toast.LENGTH_SHORT).show()
//                return@coroutineScope
//            }
//
//            val startPoint = Point.fromLngLat(startDock!!.lon,startDock!!.lat)
//            val endPoint = Point.fromLngLat(endDock!!.lon,endDock!!.lat)
//            fetchRoute(mutableListOf(startPoint,endPoint))

//            delay(2000)
//            loadActivity()
//        }
//        MapRepository.location.add(0, Locations("Covent Garden", 51.5117, -0.1240))
        MapRepository.location.add(Locations("Harrods", 51.5144, -0.1528))
        MapRepository.location.add(Locations("Tower Bridge", 51.5055, -0.0754))

        val currentPoint = Point.fromLngLat(-0.1426,51.5390)
        val stopOne = Point.fromLngLat(MapRepository.location[0].lon, MapRepository.location[0].lat)
        val stopTwo = Point.fromLngLat(MapRepository.location[1].lon, MapRepository.location[1].lat)
//        val stopThree = Point.fromLngLat(MapRepository.location[2].lon, MapRepository.location[2].lat)

        fetchRoute(mutableListOf(currentPoint,stopOne,stopTwo))
    }

    private fun loadActivity()
    {
        val intent = Intent(this, JourneyActivity::class.java)
        startActivity(intent)
    }

    private fun fetchRoute(wayPoints: MutableList<Point>) {

        planJourneyViewModel.fetchRoute(this, mapboxNavigation, wayPoints, "walking", true)
        TflHelper.getDock(applicationContext)
    }

    override fun onStop() {
        super.onStop()
//        MapboxNavigationProvider.destroy()
//        mapboxNavigation.onDestroy()
    }
}
