package com.example.backstreet_cycles.data.remote

import android.content.Context
import android.util.Log
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.useCase.MapInfoUseCase
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.route.RouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation

object MapboxApi {

    fun requestRoute(mapboxNavigation: MapboxNavigation,
                             routeOptions: RouteOptions,
                             info: Boolean,
                             listener: CallbackResource<DirectionsRoute>)
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

                    Log.i("retrieving route", "success")

                    val fastestRoute = MapInfoUseCase.getFastestRoute(routes)

                    if(info)
                    {
                        MapInfoUseCase.getJourneyInfo(fastestRoute)
                    }
                    else
                    {
                        MapRepository.currentRoute.add(fastestRoute)
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
                }
            }
        )
    }

}