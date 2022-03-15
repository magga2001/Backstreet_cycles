package com.example.backstreet_cycles.utils

import com.example.backstreet_cycles.dto.Dock
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import kotlin.math.abs

class MapHelper {

    companion object
    {
        fun getClosestDocks(point: Point): Dock {

            TflHelper.docks.filter { it.nbSpaces != 0 }
            TflHelper.docks.sortBy {
                abs(it.lat - point.latitude()) + abs(it.lon - point.longitude())
            }

            return TflHelper.docks.first()
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