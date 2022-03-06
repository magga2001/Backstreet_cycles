package com.example.backstreet_cycles.model

import com.example.backstreet_cycles.dto.Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SelectRouteHelper {


    fun findDistanceBetweenLocations(location1: Location, location2: Location): Double {
        val radius = 6371
        val phi1 = location1.lat * Math.PI / 180
        val phi2 = location2.lat * Math.PI / 180
        val deltaPhi = (location2.lat - location1.lat) * Math.PI / 180
        val deltaLambda = (location2.lon - location1.lon) * Math.PI / 180

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radius * c
    }

//    fun findLocationMap (locationList: List<Location>): Map<Int, Location> {
//
//
//    }

}