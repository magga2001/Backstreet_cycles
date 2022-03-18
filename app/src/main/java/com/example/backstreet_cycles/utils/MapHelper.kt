package com.example.backstreet_cycles.utils

import com.example.backstreet_cycles.dto.Dock
import com.example.backstreet_cycles.model.LocationRepository
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import kotlin.math.abs

class MapHelper {

    companion object
    {
        fun getClosestDocks(point: Point, numUser: Int): Dock {

            LocationRepository.docks.filter { it.nbSpaces >= numUser }
            LocationRepository.docks.sortBy {
                abs(it.lat - point.latitude()) + abs(it.lon - point.longitude())
            }

            return LocationRepository.docks.first()
        }

        fun getFastestRoute(routes: List<DirectionsRoute>): DirectionsRoute
        {
            routes.sortedBy { it.duration() }

            return routes.first()

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