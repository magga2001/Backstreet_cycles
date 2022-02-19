package com.example.backstreet_cycles

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.viewModel.PlanJourneyViewModel
import com.google.gson.GsonBuilder
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.RouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import kotlinx.android.synthetic.main.activity_plan_journey.*
import kotlinx.coroutines.*
import org.json.JSONObject

class PlanJourneyActivity : AppCompatActivity() {

    private lateinit var planJourneyViewModel: PlanJourneyViewModel
    private lateinit var mapboxNavigation: MapboxNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_journey)
        initListener()

        planJourneyViewModel = ViewModelProvider(this).get(PlanJourneyViewModel::class.java)
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
            when {
                TextUtils.isEmpty(et_startDock.text.toString()) -> {
                    et_startDock.error = "Please enter your starting dock"
                }
                TextUtils.isEmpty(et_endDock.text.toString()) -> {
                    et_endDock.error = "Please enter your destination dock"
                }
                else -> {

                    //THIS FUNCTION TO FETCH THE ORIGIN AND DESTINATION FROM EDIT TEXT
                    lifecycleScope.launch {fetchPoints()}

                }
            }
        }
    }

    private suspend fun fetchPoints()
    {
        var startDock:Dock? = null
        var endDock:Dock? = null

        coroutineScope {
            val start = async { Tfl.readDock(et_startDock.text.toString())}
            val end = async {  Tfl.readDock(et_endDock.text.toString()) }

            startDock = start.await()
            endDock = end.await()

            if(startDock == null || endDock == null)
            {
                Toast.makeText(this@PlanJourneyActivity,"No location", Toast.LENGTH_SHORT).show()
                return@coroutineScope
            }

            val startPoint = Point.fromLngLat(startDock!!.lon,startDock!!.lat)
            val endPoint = Point.fromLngLat(endDock!!.lon,endDock!!.lat)
//            wayPoints.addAll(listOf(startPoint,endPoint))
            fetchRoute(listOf(startPoint,endPoint))

            delay(500)
            loadActivity()
        }
    }

    private fun loadActivity()
    {
        val intent = Intent(this, JourneyActivity::class.java)
        startActivity(intent)
    }

    private fun fetchRoute(wayPoints: List<Point>) {

        planJourneyViewModel.fetchRoute(this, mapboxNavigation, wayPoints)
    }

    override fun onStop() {
        super.onStop()
        MapboxNavigationProvider.destroy()
    }

//    private fun fetchRoute() {
//
//        val routeOptions = RouteOptions.builder()
//            // applies the default parameters to route options
//            .applyDefaultNavigationOptions()
//            .applyLanguageAndVoiceUnitOptions(this)
////            .profile(DirectionsCriteria.PROFILE_CYCLING)
//            // lists the coordinate pair i.e. origin and destination
//            // If you want to specify waypoints you can pass list of points instead of null
//            .coordinatesList(wayPoints)
//            // set it to true if you want to receive alternate routes to your destination
//            .alternatives(true)
//            .build()
//
//        mapboxNavigation.requestRoutes(
//            routeOptions,
//            object : RouterCallback {
//                /**
//                 * The callback is triggered when the routes are ready to be displayed.
//                 */
//                override fun onRoutesReady(
//                    routes: List<DirectionsRoute>,
//                    routerOrigin: RouterOrigin
//                ) {
//                    // GSON instance used only to print the response prettily
//                    val gson = GsonBuilder().setPrettyPrinting().create()
//
//                    currentRoute= getFastestRoute(routes)
//                    centerPoint = getCenterViewPoint(wayPoints)
//
//                    //Getting route instruction
//                    getInstructions(currentRoute)
//                }
//
//                /**
//                 * The callback is triggered if the request to fetch a route was canceled.
//                 */
//                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
//                    // This particular callback is executed if you invoke
//                    //mapboxNavigation.cancelRouteRequest()
//                }
//
//                /**
//                 * The callback is triggered if the request to fetch a route failed for any reason.
//                 */
//                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
//                    //Route request fail
//                }
//            }
//        )
//
//    }
//
//    private fun getInstructions(route:DirectionsRoute)
//    {
//        val json = JSONObject(route.toJson())
//        val legs = JSONObject(json.getJSONArray("legs").getString(0))
//        val steps = legs.getJSONArray("steps")
//
//        for(i in 0 until steps.length())
//        {
//            val maneuver = JSONObject(steps.getString(i)).getString("maneuver")
//            val instruction = JSONObject(maneuver).getString("instruction")
//            val type = JSONObject(maneuver).getString("type")
//
//            if(JSONObject(maneuver).has("modifier"))
//            {
//                val modifier = JSONObject(maneuver).getString("modifier")
//                Log.i("modifier $i",modifier )
//            }
//
//            Log.i("maneuver $i", maneuver)
//            Log.i("instruction $i", instruction + "type: " + type)
//        }
//
//    }
//
//    private fun getFastestRoute(routes: List<DirectionsRoute>): DirectionsRoute
//    {
//        routes.sortedBy { it.duration() }
//
//        return routes.first()
//    }
//
//    private fun getCenterViewPoint(docks: List<Point>): Point
//    {
//        var totalLat = 0.0
//        var totalLng = 0.0
//        val size = docks.size
//
//        for(dock in docks)
//        {
//            totalLat += dock.latitude()
//            totalLng += dock.longitude()
//        }
//
//        return Point.fromLngLat(totalLng/size, totalLat/size)
//    }
}
