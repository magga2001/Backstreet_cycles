package com.example.backstreet_cycles.interfaces

import com.example.backstreet_cycles.domain.model.dto.Locations
import com.mapbox.geojson.Point

interface Planner {

    fun onSelectedJourney(location: Locations, profile: String, points: MutableList<Point>)

    fun onFetchJourney(points: MutableList<Point>)
}