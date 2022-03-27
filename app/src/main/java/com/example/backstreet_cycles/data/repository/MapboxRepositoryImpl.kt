package com.example.backstreet_cycles.data.repository

import android.content.Context
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.data.remote.MapboxApi
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation
import javax.inject.Inject

class MapboxRepositoryImpl@Inject constructor(
    private val mapboxApi: MapboxApi
): MapboxRepository {

    override fun requestRoute(mapboxNavigation: MapboxNavigation,
                                    routeOptions: RouteOptions,
                                    info: Boolean,
                                    listener: CallbackResource<DirectionsRoute>
    ) {
        mapboxApi.requestRoute(mapboxNavigation,routeOptions,info, listener)
    }
}