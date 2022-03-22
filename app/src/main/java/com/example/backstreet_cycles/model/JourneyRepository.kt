package com.example.backstreet_cycles.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.DTO.Maneuver
import com.example.backstreet_cycles.DTO.Users
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.utils.BitmapHelper
import com.example.backstreet_cycles.utils.MapHelper
import com.example.backstreet_cycles.viewModel.LoggedInViewModel
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.RouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.lang.reflect.Type
import kotlin.math.ceil
import kotlin.math.roundToInt

class JourneyRepository(private val application: Application,
                        fireStore: FirebaseFirestore,): MapRepository(application) {

    private var sharedPref: SharedPreferences
    private val isReadyMutableLiveData: MutableLiveData<Boolean>
    private val distanceMutableLiveData: MutableLiveData<String>
    private val durationMutableLiveData: MutableLiveData<String>
    private val priceMutableLiveData: MutableLiveData<String>
    private lateinit var routeOptions: RouteOptions
    private val dataBase = fireStore
    lateinit var pointAnnotationManager: PointAnnotationManager
    private val loggedInViewModel: LoggedInViewModel
    private var numberOfUsers: Int

    companion object
    {
        lateinit var result: RoutesUpdatedResult
        const val MAX_TIME_TO_USE_THE_BIKE_FOR_FREE = 30
    }

    init {
        numberOfUsers = 0
        loggedInViewModel = LoggedInViewModel(application)
        isReadyMutableLiveData = MutableLiveData()
        isReadyMutableLiveData.value = false
        distanceMutableLiveData = MutableLiveData()
        durationMutableLiveData = MutableLiveData()
        priceMutableLiveData = MutableLiveData()
        sharedPref = application.getSharedPreferences(
            R.string.preference_file_Locations.toString(), Context.MODE_PRIVATE)
    }

    override fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return (if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            MapboxNavigationProvider.create(
                NavigationOptions.Builder(application)
                    .accessToken(application.getString(R.string.mapbox_access_token))
                    .build()
            )
        })
    }

    fun initialiseLocationObserver(navigationCamera: NavigationCamera, viewportDataSource: MapboxNavigationViewportDataSource): LocationObserver
    {
        return object : LocationObserver {
            var firstLocationUpdateReceived = false

            override fun onNewRawLocation(rawLocation: Location) {
                // not handled
            }

            override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
                val enhancedLocation = locationMatcherResult.enhancedLocation
                // update location puck's position on the map
                navigationLocationProvider.changePosition(
                    location = enhancedLocation,
                    keyPoints = locationMatcherResult.keyPoints,
                )

                // update camera position to account for new location
                viewportDataSource.onLocationChanged(enhancedLocation)
                viewportDataSource.evaluate()

                // if this is the first location update the activity has received,
                // it's best to immediately move the camera to the current user location
                if (!firstLocationUpdateReceived) {
                    firstLocationUpdateReceived = true
                    navigationCamera.requestNavigationCameraToOverview(
                        stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                            .maxDuration(0) // instant transition
                            .build()
                    )
                }
            }
        }
    }

    fun getNumberOfUsers():Int {
        return numberOfUsers
    }

    fun setNumberOfUsers(numUsers: Int) {
        numberOfUsers = numUsers
    }

    fun initialiseRoutesObserver(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView, viewportDataSource: MapboxNavigationViewportDataSource): RoutesObserver
    {
        return RoutesObserver { routeUpdateResult ->
            // RouteLine: wrap the DirectionRoute objects and pass them
            // to the MapboxRouteLineApi to generate the data necessary to draw the route(s)
            // on the map.
            result = routeUpdateResult
            val routeLines = routeUpdateResult.routes.map { RouteLine(it, null) }

            routeLineApi.setRoutes(
                routeLines
            ) { value ->
                // RouteLine: The MapboxRouteLineView expects a non-null reference to the map style.
                // the data generated by the call to the MapboxRouteLineApi above must be rendered
                // by the MapboxRouteLineView in order to visualize the changes on the map.
                mapboxMap.getStyle()?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }

            viewportDataSource.onRouteChanged(routeUpdateResult.routes.first())
            viewportDataSource.evaluate()
        }
    }

    fun initialiseRouteProgressObserver(mapboxMap: MapboxMap,
                                        routeLineApi: MapboxRouteLineApi,
                                        routeLineView: MapboxRouteLineView,
                                        routeArrowApi: MapboxRouteArrowApi,
                                        routeArrowView: MapboxRouteArrowView,
                                        viewportDataSource: MapboxNavigationViewportDataSource
    ): RouteProgressObserver
    {
        return RouteProgressObserver { routeProgress ->

            viewportDataSource.onRouteProgressChanged(routeProgress)
            viewportDataSource.evaluate()
            // RouteLine: This line is only necessary if the vanishing route line feature
            // is enabled.
            routeLineApi.updateWithRouteProgress(routeProgress) { result ->
                mapboxMap.getStyle()?.apply {
                    routeLineView.renderRouteLineUpdate(this, result)
                }
            }

            // RouteArrow: The next maneuver arrows are driven by route progress events.
            // Generate the next maneuver arrow update data and pass it to the view class
            // to visualize the updates on the map.
            val arrowUpdate = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
            mapboxMap.getStyle()?.apply {
                // Render the result to update the map.
                routeArrowView.renderManeuverUpdate(this, arrowUpdate)
            }
        }
    }

    fun fetchRoute(context: Context,
                   mapboxNavigation: MapboxNavigation,
                   points: MutableList<Point>,
                   profile: String,
                   info: Boolean)
    {

        Log.i("Waypoint route", "Success")

        location.distinct()
        points.distinct()

        if(!info)
        {
            distances.clear()
            durations.clear()
            wayPoints.addAll(points)
            centerPoint = MapHelper.getCenterViewPoint(points)

            routeOptions = when(profile)
            {
                "walking" -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_WALKING)
                else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
            }

            requestRoute(mapboxNavigation, routeOptions, info)
        }else
        {
            routeOptions = customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
            requestRoute(mapboxNavigation, routeOptions, info)
        }
    }

    fun addLocationSharedPreferences(locations: MutableList<Locations>):Boolean {
        if (getListLocations().isEmpty()){
            overrideListLocation(locations)
            return false
        }
        return true
    }

    fun overrideListLocation(locations: MutableList<Locations>) {
        val gson = Gson();
        val json = gson.toJson(locations);
        with (sharedPref.edit()) {
            putString(R.string.preference_file_Locations.toString(), json)
            apply()
        }
    }

    fun clearListLocations() {
        with (sharedPref.edit()) {
            clear()
            apply()
        }
    }

    fun getListLocations(): List<Locations> {
        val locations: List<Locations>
        val serializedObject: String? = sharedPref.getString(R.string.preference_file_Locations.toString(), null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<Locations?>?>() {}.getType()
            locations = gson.fromJson<List<Locations>>(serializedObject, type)
        }else {
            locations = emptyList()
        }
        return locations
    }

    private fun customiseRouteOptions(context: Context, points: List<Point>, criteria: String): RouteOptions
    {
        return RouteOptions.builder()
            // applies the default parameters to route options
            .applyDefaultNavigationOptions(DirectionsCriteria.PROFILE_CYCLING)
            .applyLanguageAndVoiceUnitOptions(context)
            .profile(criteria)
            // lists the coordinate pair i.e. origin and destination
            // If you want to specify waypoints you can pass list of points instead of null
            .coordinatesList(points)
            // set it to true if you want to receive alternate routes to your destination
            .alternatives(true)
            .build()
    }

    private fun requestRoute(mapboxNavigation: MapboxNavigation, routeOptions: RouteOptions, info: Boolean)
    {

        Log.i("retrieving the route", "success")

        mapboxNavigation.requestRoutes(
            routeOptions,
            object : RouterCallback {
                /**
                 * The callback is triggered when the routes are ready to be displayed.
                 */
                override fun onRoutesReady(
                    routes: List<DirectionsRoute>,
                    routerOrigin: RouterOrigin
                ) {
                    // GSON instance used only to print the response prettily
//                    val gson = GsonBuilder().setPrettyPrinting().create()


                    Log.i("retrieving route", "success")

                    val fastestRoute = MapHelper.getFastestRoute(routes)
                    getInstructions(fastestRoute)

                    if(info)
                    {
                        getJourneyInfo(fastestRoute)
                    }
                    else
                    {
                        currentRoute.add(fastestRoute)
                        isReadyMutableLiveData.postValue(true)
                    }
                }

                /**
                 * The callback is triggered if the request to fetch a route was canceled.
                 */
                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                    // This particular callback is executed if you invoke
                    //mapboxNavigation.cancelRouteRequest()
                    Log.i("retrieving route", "cancel")
                }

                /**
                 * The callback is triggered if the request to fetch a route failed for any reason.
                 */
                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    //Route request fail
                    Log.i("retrieving route", "fail")
                }
            }
        )
    }

    private fun getJourneyInfo(route: DirectionsRoute)
    {
        val minutesRate = 30

        Log.i("DistanceJSON", route.distance().toString())
        Log.i("DurationJSON", route.duration().toString())

        distances.add(route.distance())
        durations.add(route.duration())

        Log.i("distance size", distances.size.toString())
        Log.i("duration size", durations.size.toString())

        var prices = ceil(((((durations.sum()/60) - MAX_TIME_TO_USE_THE_BIKE_FOR_FREE) / minutesRate))) * 2

        if(prices <= 0)
        {
            prices = 0.0
        }

        if(prices.toInt() % 2 != 0)
        {
            prices++
        }

        distanceMutableLiveData.postValue(distances.sum().roundToInt().toString())
        durationMutableLiveData.postValue((durations.sum()/60).roundToInt().toString())
        priceMutableLiveData.postValue((prices*numberOfUsers).toString())
    }

    fun getInstructions(route:DirectionsRoute)
    {
        val json = JSONObject(route.toJson())
        val legs = JSONObject(json.getJSONArray("legs").getString(0))
        val steps = legs.getJSONArray("steps")

        for(i in 0 until steps.length())
        {
            val maneuver = JSONObject(steps.getString(i)).getString("maneuver")
            val instruction = JSONObject(maneuver).getString("instruction")
            val type = JSONObject(maneuver).getString("type")
            var modifier = ""
            val distance = JSONObject(steps.getString(i)).getString("distance")

            Log.i("distance", distance)

            if(JSONObject(maneuver).has("modifier"))
            {
                modifier = JSONObject(maneuver).getString("modifier")
                Log.i("modifier $i",modifier )
            }

            Log.i("maneuver $i", maneuver)
            Log.i("instruction $i", instruction + "type: " + type)

            val theManeuver = Maneuver(instruction, type, modifier, distance.toDouble().roundToInt())
            maneuvers.add(theManeuver)
        }

    }

    fun addAnnotationToMap(context: Context, mapView: MapView) {

        Log.i("wayPoints", wayPoints.size.toString())
        // Create an instance of the Annotation API and get the PointAnnotationManager.
        val raw_bitmap = BitmapHelper.bitmapFromDrawableRes(context, R.drawable.dock_station) as Bitmap
        val bitmap = Bitmap.createScaledBitmap(raw_bitmap, 150, 150, false)
        bitmap.let {
            // Set options for the resulting symbol layer.
            val annotationApi = mapView.annotations
            pointAnnotationManager = annotationApi.createPointAnnotationManager()

            for(i in wayPoints.indices)
            {
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    // Define a geographic coordinate.
                    .withPoint(wayPoints[i])
                    // Specify the bitmap you assigned to the point annotation
                    // The bitmap will be added to map style automatically.
                    .withIconImage(it)
                    .withTextAnchor(textAnchor = TextAnchor.TOP)
                    .withTextField((i + 65).toChar().toString())
                    .withTextSize(10.00)
                // Add the resulting pointAnnotation to the map.
                pointAnnotationManager.create(pointAnnotationOptions)
            }
        }
    }

    fun removeAnnotations()
    {
        pointAnnotationManager.deleteAll()
    }

    fun registerObservers(mapboxNavigation: MapboxNavigation,
                          routesObserver: RoutesObserver,
                          locationObserver: LocationObserver,
                          routeProgressObserver: RouteProgressObserver
    )
    {
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerLocationObserver(locationObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
    }

    fun unregisterObservers(mapboxNavigation: MapboxNavigation,
                            routesObserver: RoutesObserver,
                            locationObserver: LocationObserver,
                            routeProgressObserver: RouteProgressObserver)
    {
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
    }


    fun addJourneyToJourneyHistory(locations: MutableList<Locations>,userDetails: Users) =
    CoroutineScope(Dispatchers.IO).launch {

        val user =  dataBase
            .collection("users")
            .whereEqualTo("email", userDetails.email)
            .get()
            .await()
        val gson = Gson()
        val jsonObject = gson.toJson(locations)
        if (jsonObject.isNotEmpty()){
            userDetails.journeyHistory.add(jsonObject)
            if (user.documents.isNotEmpty()) {
                for (document in user) {

                    try {
                        dataBase.collection("users")
                            .document(document.id)
                            .update("journeyHistory",userDetails.journeyHistory)
                    }
                    catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(application,e.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


    }

    fun convertJSON(serializedObject: String): List<Locations> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Locations?>?>() {}.getType()
        return gson.fromJson(serializedObject, type)
    }

    fun getJourneyHistory(userDetails: Users):MutableList<List<Locations>> {
        val listLocations = emptyList<List<Locations>>().toMutableList()
        for (journey in userDetails.journeyHistory){
            val serializedObject: String = journey
            listLocations.add(convertJSON(serializedObject))
        }
        return listLocations
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

    fun getDistanceMutableLiveData(): MutableLiveData<String>
    {
        return distanceMutableLiveData
    }

    fun getDurationMutableLiveData(): MutableLiveData<String>
    {
        return durationMutableLiveData
    }

    fun getPriceMutableLiveData(): MutableLiveData<String>
    {
        return priceMutableLiveData
    }
}