package com.example.backstreet_cycles.domain.useCase

import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.data.remote.dto.TflHelper
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import kotlin.math.abs

class MapHelper {

    companion object
    {
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

        fun getCenterViewPoint(points: List<Point>): Point
        {
            var totalLat = 0.0
            var totalLng = 0.0
            val size = points.size

            for(point in points)
            {
                totalLat += point.latitude()
                totalLng += point.longitude()
            }

            return Point.fromLngLat(totalLng/size, totalLat/size)
        }
    }

}