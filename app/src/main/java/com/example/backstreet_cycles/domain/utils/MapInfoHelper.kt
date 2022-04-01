package com.example.backstreet_cycles.domain.utils


import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import kotlin.math.abs
import kotlin.math.ceil

object MapInfoHelper {

    /**
     *
     */
    fun getClosestDocksToOrigin(docks: MutableList<Dock>, point: Point, numUser: Int): Dock {
        docks.filter { it.nbBikes >= numUser }
        docks.sortBy {
            abs(it.lat - point.latitude()) + abs(it.lon - point.longitude())
        }
        return docks.first()
    }

    /**
     *
     */
    fun getClosestDocksToDestination(docks: MutableList<Dock>, point: Point, numUser: Int): Dock {
        docks.filter { it.nbSpaces >= numUser }
        docks.sortBy {
            abs(it.lat - point.latitude()) + abs(it.lon - point.longitude())
        }
        return docks.first()
    }

    /**
     *
     */
    fun getFastestRoute(routes: List<DirectionsRoute>): DirectionsRoute {
        val route = routes.sortedBy { it.duration() }
        return route.first()
    }

    /**
     *
     */
    fun retrieveJourneyDistances(route: DirectionsRoute): Double {
        return route.distance()
    }

    /**
     *
     */
    fun retrieveJourneyDurations(route: DirectionsRoute): Double {
        return route.duration()
    }

    /**
     *
     */
    fun getRental(durations: MutableList<Double>): Double {
        var prices =
            ceil(((((durations.sum() / 60) - Constants.MAX_TIME_TO_USE_THE_BIKE_FOR_FREE) / Constants.MINUTE_RATE))) * Constants.BIKE_RENTING_PRICE
        if (prices <= 0) {
            prices = 0.0
        }
        if (prices.toInt() % 2 != 0) {
            prices++
        }
        return prices
    }
}