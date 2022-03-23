package com.example.backstreet_cycles.domain.utils

import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.mapbox.geojson.Point

object PlannerHelper {

    fun setPoints(newStops: MutableList<Locations>): MutableList<Point> {
        val listPoints = emptyList<Point>().toMutableList()
        for (i in 0 until newStops.size){
            listPoints.add(Point.fromLngLat(MapRepository.location[i].lon, MapRepository.location[i].lat))
        }
        return listPoints
    }
}