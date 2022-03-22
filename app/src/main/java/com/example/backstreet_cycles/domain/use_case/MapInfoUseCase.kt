package com.example.backstreet_cycles.domain.use_case

import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.data.remote.TflApi
import com.example.backstreet_cycles.data.remote.TflHelper
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.DTO.Dock
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import kotlin.math.abs
import kotlin.math.ceil

object MapInfoUseCase {

    fun getClosestDocks(point: Point, numUser: Int): Dock {

        TflHelper.docks.filter { it.nbSpaces >= numUser }
        TflHelper.docks.sortBy {
            abs(it.lat - point.latitude()) + abs(it.lon - point.longitude())
        }

        return TflHelper.docks.first()
    }

    fun getFastestRoute(routes: List<DirectionsRoute>): DirectionsRoute
    {
        val route = routes.sortedBy { it.duration() }

        return route.first()
    }

    fun getJourneyInfo(route: DirectionsRoute)
    {
        MapRepository.distances.add(route.distance())
        MapRepository.durations.add(route.duration())

        var prices = ceil(((((MapRepository.durations.sum()/60) - Constants.MAX_TIME_TO_USE_THE_BIKE_FOR_FREE) / Constants.MINUTE_RATE))) * 2

        if(prices <= 0)
        {
            prices = 0.0
        }

        if(prices.toInt() % 2 != 0)
        {
            prices++
        }
    }
}