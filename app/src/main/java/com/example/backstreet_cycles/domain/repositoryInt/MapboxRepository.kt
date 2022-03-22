package com.example.backstreet_cycles.domain.repositoryInt

import android.content.Context
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation

interface MapboxRepository {

    fun fetchRoute(context: Context,
                   mapboxNavigation: MapboxNavigation,
                   points: MutableList<Point>,
                   profile: String,
                   info: Boolean)
}