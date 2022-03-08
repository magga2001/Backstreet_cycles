package com.example.backstreet_cycles.utils

import com.example.backstreet_cycles.dto.Dock
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
    }

}