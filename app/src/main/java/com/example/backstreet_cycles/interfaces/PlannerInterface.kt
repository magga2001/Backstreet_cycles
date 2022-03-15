package com.example.backstreet_cycles.interfaces

import com.example.backstreet_cycles.dto.Locations
import com.mapbox.geojson.Point

interface PlannerInterface {

    fun onSelectedStop(location: Locations)

    fun onSelectedJourney(location: Locations, profile: String, points: MutableList<Point>)
}