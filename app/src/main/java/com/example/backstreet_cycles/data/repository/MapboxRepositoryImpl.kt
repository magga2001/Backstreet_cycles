package com.example.backstreet_cycles.data.repository

import android.content.Context
import com.example.backstreet_cycles.data.remote.MapboxApi
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation
import javax.inject.Inject

class MapboxRepositoryImpl@Inject constructor(
    private val mapboxApi: MapboxApi
): MapboxRepository {

    override fun fetchRoute(
        context: Context,
        mapboxNavigation: MapboxNavigation,
        points: MutableList<Point>,
        profile: String,
        info: Boolean
    ) {
        mapboxApi.fetchRoute(context,mapboxNavigation,points,profile,info)
    }


}