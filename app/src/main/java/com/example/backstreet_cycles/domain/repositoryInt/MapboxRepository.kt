package com.example.backstreet_cycles.domain.repositoryInt

import android.content.Context
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.common.Resource
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface MapboxRepository {

    fun requestRoute(mapboxNavigation: MapboxNavigation,
                           routeOptions: RouteOptions,
                           info: Boolean,
                           listener: CallbackResource<DirectionsRoute>
    )
}