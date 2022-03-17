package com.example.backstreet_cycles.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.dto.Maneuver
import com.example.backstreet_cycles.utils.BitmapHelper
import com.example.backstreet_cycles.utils.MapHelper
import com.google.common.reflect.TypeToken
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
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.lang.reflect.Type
import kotlin.math.roundToInt

class JourneyRepository(private val application: Application): MapRepository(application) {

    private lateinit var sharedPref: SharedPreferences
    private val isReadyMutableLiveData: MutableLiveData<Boolean>
    private lateinit var routeOptions: RouteOptions

    lateinit var pointAnnotationManager: PointAnnotationManager

    companion object
    {
        lateinit var result: RoutesUpdatedResult
    }

    init {
        isReadyMutableLiveData = MutableLiveData()
        isReadyMutableLiveData.value = false
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

    fun initialiseRoutesObserver(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView): RoutesObserver
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
        }
    }

    fun initialiseRouteProgressObserver(mapboxMap: MapboxMap,
                                        routeLineApi: MapboxRouteLineApi,
                                        routeLineView: MapboxRouteLineView,
                                        routeArrowApi: MapboxRouteArrowApi,
                                        routeArrowView: MapboxRouteArrowView
    ): RouteProgressObserver
    {
        return RouteProgressObserver { routeProgress ->
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
                   overview: Boolean)
    {

//        currentRoute.clear()
//        maneuvers.clear()
        location.distinct()
        points.distinct()

        //Act as current location first
//        location.add(0, Locations("Camden Town", 51.5390, -0.1426))
//        points.add(0, Point.fromLngLat(location[0].lon, location[0].lat))

//        val routeOptions = when(profile)
//        {
//            "walking" -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_WALKING)
//            "cycling" -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
//            else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_DRIVING)
//        }

//        Add your current location
//        points.add(0, Point.fromLngLat(enhancedLocation.longitude, enhancedLocation.latitude))



        wayPoints.addAll(points)
        centerPoint = MapHelper.getCenterViewPoint(points)

        if(overview)
        {
            routeOptions = when(profile)
            {
                "walking" -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_WALKING)
                else -> customiseRouteOptions(context, points, DirectionsCriteria.PROFILE_CYCLING)
            }

            requestRoute(mapboxNavigation, routeOptions,mainPath = true, lastPoint = true)
        }
        else
        {
            val startWalk = points.slice(0 until 2)
            val cycling = points.slice(1 until 3)
            val endWalk = points.slice(2 until 4)

            runBlocking {

                async {
                    routeOptions = customiseRouteOptions(context, endWalk, DirectionsCriteria.PROFILE_WALKING)
                    requestRoute(mapboxNavigation, routeOptions, mainPath = false, lastPoint = false)

                    routeOptions = customiseRouteOptions(context, startWalk, DirectionsCriteria.PROFILE_WALKING)
                    requestRoute(mapboxNavigation, routeOptions, mainPath = false,lastPoint = false)
                }.await()

                routeOptions = customiseRouteOptions(context, cycling, DirectionsCriteria.PROFILE_CYCLING)
//                routeOptions.toBuilder().applyDefaultNavigationOptions(DirectionsCriteria.PROFILE_CYCLING).build()
                requestRoute(mapboxNavigation, routeOptions,mainPath = true,lastPoint = true)
            }
        }
    }

    fun addLocationSharedPreferences(locations: MutableList<Locations>):Boolean {
//        sharedPref =

        if (getListLocations().isEmpty()){
            overrideListLocation(locations)
            return false
        }
        return true
//    Need to pop up with a message saying that they are currently in a journey. Should it
//    proceed with the already save journey or start the new one.
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

    private fun requestRoute(mapboxNavigation: MapboxNavigation, routeOptions: RouteOptions, mainPath: Boolean,lastPoint: Boolean)
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

                    if(mainPath)
                    {
                        Log.i("bruh", "Cycling")
                        currentRoute.add(0, fastestRoute)
                    }else
                    {
                        Log.i("bruh", "Walking")
                        currentRoute.add(MapHelper.getFastestRoute(routes))
                    }

                    if(lastPoint)
                    {
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

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }
}