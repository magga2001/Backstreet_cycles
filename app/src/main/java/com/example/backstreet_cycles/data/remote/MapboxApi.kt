package com.example.backstreet_cycles.data.remote

import android.util.Log
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.utils.MapInfoHelper
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.base.route.RouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation

object MapboxApi {

    fun requestRoute(
        mapboxNavigation: MapboxNavigation,
        routeOptions: RouteOptions,
        info: Boolean,
        listener: CallbackResource<DirectionsRoute>,
        mapboxRepository: MapboxRepository
    ) {

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

                    Log.i("retrieving route", "success")

                    val fastestRoute = MapInfoHelper.getFastestRoute(routes)

                    if (info) {
                        val distance = MapInfoHelper.retrieveJourneyDistances(fastestRoute)
                        val duration = MapInfoHelper.retrieveJourneyDurations(fastestRoute)

                        mapboxRepository.addJourneyDistances(distance)
                        mapboxRepository.addJourneyDuration(duration)
                    } else {
                        mapboxRepository.setJourneyCurrentRoute(fastestRoute)
                    }

                    listener.getResult(fastestRoute)
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
                    listener.getResult(DirectionsRoute.fromJson(""))
                }
            }
        )
    }

}