package com.example.backstreet_cycles.utils

import android.location.Location
import com.example.backstreet_cycles.Tfl
import com.example.backstreet_cycles.dto.Dock
import com.mapbox.geojson.Point
import kotlin.math.abs

class MapHelper {

    companion object
    {
        // Get relevant docks around the searched location; i.e. destination
        fun getClosestDocks(point: Point): MutableList<Dock> {

            Tfl.docks.filter { it.nbSpaces != 0 }
            Tfl.docks.sortBy {
                abs(it.lat - point.latitude()) + abs(it.lon - point.longitude())
            }

            return Tfl.docks.subList(0, 10)
        }
    }
}