package com.example.backstreet_cycles.domain.useCase


import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import kotlin.math.abs
import kotlin.math.ceil

object MapInfoUseCase {

    fun getClosestDocks(point: Point, numUser: Int): Dock {

        BackstreetApplication.docks.filter { it.nbSpaces >= numUser }
        BackstreetApplication.docks.sortBy {
            abs(it.lat - point.latitude()) + abs(it.lon - point.longitude())
        }

        return BackstreetApplication.docks.first()
    }

    fun getFastestRoute(routes: List<DirectionsRoute>): DirectionsRoute
    {
        val route = routes.sortedBy { it.duration() }

        return route.first()
    }

    fun getJourneyInfo(route: DirectionsRoute)
    {
        BackstreetApplication.distances.add(route.distance())
        BackstreetApplication.durations.add(route.duration())

    }

    fun getRental(): Double
    {
        var prices = ceil(((((BackstreetApplication.durations.sum()/60) - Constants.MAX_TIME_TO_USE_THE_BIKE_FOR_FREE) / Constants.MINUTE_RATE))) * 2

        if(prices <= 0)
        {
            prices = 0.0
        }

        if(prices.toInt() % 2 != 0)
        {
            prices++
        }

        return prices
    }
}