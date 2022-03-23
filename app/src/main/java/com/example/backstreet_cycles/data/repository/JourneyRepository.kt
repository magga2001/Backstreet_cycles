package com.example.backstreet_cycles.data.repository

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.domain.useCase.MapInfoUseCase
import com.example.backstreet_cycles.domain.utils.BitmapHelper
import com.example.backstreet_cycles.ui.viewModel.LoggedInViewModel
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
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
import kotlin.math.ceil

class JourneyRepository(private val application: Application,
                        fireStore: FirebaseFirestore,): MapRepository(application) {

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

    fun fetchRoute(context: Context,
                   mapboxNavigation: MapboxNavigation,
                   points: MutableList<Point>,
                   profile: String,
                   info: Boolean)
    {

        val routeOptions: RouteOptions

        location.distinct()
        points.distinct()

        if(!info)
        {
            distances.clear()
            durations.clear()
            wayPoints.addAll(points)

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

                    val fastestRoute = MapInfoUseCase.getFastestRoute(routes)
                    if(info)
                    {
                        getJourneyInfo(fastestRoute)
                    }
                    else
                    {
                        currentRoute.add(fastestRoute)
                        //isReadyMutableLiveData.postValue(true)
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
        distances.add(route.distance())
        durations.add(route.duration())

        var prices = ceil(((((durations.sum()/60) - Constants.MAX_TIME_TO_USE_THE_BIKE_FOR_FREE) / Constants.MINUTE_RATE))) * 2

        if(prices <= 0)
        {
            prices = 0.0
        }

        if(prices.toInt() % 2 != 0)
        {
            prices++
        }

        val numUser = 1

//        distanceMutableLiveData.postValue((distances.sum()/1000).roundToInt().toString())
//        durationMutableLiveData.postValue((durations.sum()/60).roundToInt().toString())
//        priceMutableLiveData.postValue((prices*numUser).toString())
    }
}