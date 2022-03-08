package com.example.backstreet_cycles.model

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.dto.Maneuver
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources

abstract class MapRepository(private val application: Application)
{
    companion object
    {
        var currentRoute = mutableListOf<DirectionsRoute>()
        val maneuvers = mutableListOf<Maneuver>()
        val wayPoints = mutableListOf<Point>()
        val location = mutableListOf<Locations>()
        lateinit var centerPoint: Point
        lateinit var enhancedLocation: Location
    }

    /**
     * [NavigationLocationProvider] is a utility class that helps to provide location updates generated by the Navigation SDK
     * to the Maps SDK in order to update the user location indicator on the map.
     */
    protected val navigationLocationProvider = NavigationLocationProvider()

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

    open fun initialiseLocationComponent(mapView: MapView): LocationComponentPlugin
    {
        Log.i("current location", "Success")

        return mapView.location.apply {
//            setLocationProvider(navigationLocationProvider)
//             When true, the blue circular puck is shown on the map. If set to false, user
            // location in the form of puck will not be shown on the map.
            enabled = true
        }
    }

    fun initialiseLocationObserver(mapView: MapView): LocationObserver
    {
        return object : LocationObserver {
            override fun onNewRawLocation(rawLocation: Location) {}
            override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
                enhancedLocation = locationMatcherResult.enhancedLocation
                navigationLocationProvider.changePosition(
                    location = enhancedLocation,
                    keyPoints = locationMatcherResult.keyPoints,
                )
//                    updateCamera(Point.fromLngLat(enhancedLocation.longitude, enhancedLocation.latitude),
//                    enhancedLocation.bearing.toDouble(),mapView)
            }
        }
    }

    fun updateCamera(point: Point, bearing: Double?, mapView: MapView) {
        val mapAnimationOptionsBuilder = MapAnimationOptions.Builder()
        mapView.camera.easeTo(
            CameraOptions.Builder()
                .center(point)
                .bearing(bearing)
                //                .pitch(45.0)
                .zoom(12.0)
                .padding(EdgeInsets(1000.0, 0.0, 0.0, 0.0))
                .build(),
            mapAnimationOptionsBuilder.build()
        )
    }

    fun initialiseRouteLineResources(): RouteLineResources
    {
        return RouteLineResources.Builder()
            /**
             * Route line related colors can be customized via the [RouteLineColorResources]. If using the
             * default colors the [RouteLineColorResources] does not need to be set as seen here, the
             * defaults will be used internally by the builder.
             */
            .routeLineColorResources(RouteLineColorResources.Builder().build())
            .build()
    }

    fun initialiseOnPositionChangedListener(mapboxMap: MapboxMap, routeLineApi: MapboxRouteLineApi, routeLineView: MapboxRouteLineView): OnIndicatorPositionChangedListener
    {
        return  OnIndicatorPositionChangedListener { point ->
            val result = routeLineApi.updateTraveledRouteLine(point)
            mapboxMap.getStyle()?.apply {
                // Render the result to update the map.
                routeLineView.renderRouteLineUpdate(this, result)
            }
        }
    }

    abstract fun initialiseMapboxNavigation(): MapboxNavigation
}

//mapView.location.apply {
//    this.enabled = true
////            this.locationPuck = LocationPuck2D(
////                bearingImage = AppCompatResources.getDrawable(
////                    this@LocationTrackingActivity,
////                     com.mapbox.services.android.navigation.ui.v5.R.drawable.ic_arrow_head,
////                ),
////                shadowImage = AppCompatResources.getDrawable(
////                    this@LocationTrackingActivity,
////                    com.mapbox.services.android.navigation.R.drawable.ic_circle,
////                ),
////                scaleExpression = interpolate {
////                    linear()
////                    zoom()
////                    stop {
////                        literal(0.0)
////                        literal(0.6)
////                    }
////                    stop {
////                        literal(20.0)
////                        literal(1.0)
////                    }
////                }.toJson()
////            )
//}