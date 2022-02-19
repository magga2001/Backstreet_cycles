package com.example.backstreet_cycles.model

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import com.example.backstreet_cycles.R
import com.google.gson.GsonBuilder
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
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
import org.json.JSONObject

class MapHelper(private val application: Application) {

    //So you can call from every class
    companion object
    {
        lateinit var currentRoute: DirectionsRoute
        lateinit var centerPoint: Point
        lateinit var wayPoints : List<Point>
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
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

    fun checkPermission(context: Context, activity: Activity)
    {
        val TAG_CODE_PERMISSION_LOCATION = 0

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                TAG_CODE_PERMISSION_LOCATION
            )
        }
    }

    fun fetchRoute(context: Context, mapboxNavigation: MapboxNavigation, points: List<Point>) {

        val routeOptions = RouteOptions.builder()
            // applies the default parameters to route options
            .applyDefaultNavigationOptions()
            .applyLanguageAndVoiceUnitOptions(context)
//            .profile(DirectionsCriteria.PROFILE_CYCLING)
            // lists the coordinate pair i.e. origin and destination
            // If you want to specify waypoints you can pass list of points instead of null
            .coordinatesList(points)
            // set it to true if you want to receive alternate routes to your destination
            .alternatives(true)
            .build()

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
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    currentRoute = getFastestRoute(routes)
                    centerPoint = getCenterViewPoint(points)
                    wayPoints = points

                    //Getting route instruction
                    getInstructions(currentRoute)
                }

                /**
                 * The callback is triggered if the request to fetch a route was canceled.
                 */
                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                    // This particular callback is executed if you invoke
                    //mapboxNavigation.cancelRouteRequest()
                }

                /**
                 * The callback is triggered if the request to fetch a route failed for any reason.
                 */
                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    //Route request fail
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

            if(JSONObject(maneuver).has("modifier"))
            {
                val modifier = JSONObject(maneuver).getString("modifier")
                Log.i("modifier $i",modifier )
            }

            Log.i("maneuver $i", maneuver)
            Log.i("instruction $i", instruction + "type: " + type)
        }

    }

    fun getFastestRoute(routes: List<DirectionsRoute>): DirectionsRoute
    {
        routes.sortedBy { it.duration() }

        return routes.first()
    }

    fun getCenterViewPoint(docks: List<Point>): Point
    {
        var totalLat = 0.0
        var totalLng = 0.0
        val size = docks.size

        for(dock in docks)
        {
            totalLat += dock.latitude()
            totalLng += dock.longitude()
        }

        return Point.fromLngLat(totalLng/size, totalLat/size)
    }

    fun updateCamera(point: Point, bearing: Double?, mapView: MapView) {
        val mapAnimationOptionsBuilder = MapAnimationOptions.Builder()
        mapView.camera.easeTo(
            CameraOptions.Builder()
                .center(point)
                .bearing(bearing)
    //                .pitch(45.0)
                .zoom(13.0)
                .padding(EdgeInsets(1000.0, 0.0, 0.0, 0.0))
                .build(),
            mapAnimationOptionsBuilder.build()
        )
    }

    fun addAnnotationToMap(context: Context, mapView: MapView) {
        // Create an instance of the Annotation API and get the PointAnnotationManager.
        val raw_bitmap = bitmapFromDrawableRes(context, R.drawable.dock_station) as Bitmap
        val bitmap = Bitmap.createScaledBitmap(raw_bitmap, 150, 150, false)
        bitmap.let {
            // Set options for the resulting symbol layer.
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager()!!

            for(i in 0 until MapHelper.wayPoints.size)
            {
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    // Define a geographic coordinate.
                    .withPoint(MapHelper.wayPoints[i])
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
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}