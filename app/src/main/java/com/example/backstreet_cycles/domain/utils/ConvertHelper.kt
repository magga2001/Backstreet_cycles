package com.example.backstreet_cycles.domain.utils

import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.mapbox.geojson.Point
import kotlin.math.roundToInt

/**
 * Helper class for conversion of different data
 */
object ConvertHelper {


    fun shortenName(name: String): List<String> {
        val delimiter = ","
        return name.split(delimiter)
    }

    fun convertLocationToPoint(locations: Locations): Point {
        return Point.fromLngLat(locations.lon, locations.lat)
    }

    fun convertDockToLocations(dock: Dock): Locations {
        return Locations(dock.name, dock.lat, dock.lon)
    }

    fun convertMToKm(distances: List<Double>): Int {
        return (distances.sum() / 1000).roundToInt()
    }

    fun convertMsToS(durations: List<Double>): Int {
        return (durations.sum() / 60).roundToInt()
    }

    /**
     * @param locations - a list of locations
     * @return a list of strings - of the location names
     */
    fun getLocationNames(locations: List<Locations>): List<String> {
        return locations.map { it.name }
    }
}