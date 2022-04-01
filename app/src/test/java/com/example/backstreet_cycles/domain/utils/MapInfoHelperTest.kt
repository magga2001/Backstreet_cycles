package com.example.backstreet_cycles.domain.utils

import com.example.backstreet_cycles.domain.model.dto.Dock
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import org.junit.After
import org.junit.Before
import org.junit.Test

class MapInfoHelperTest {

    private lateinit var routes:MutableList<DirectionsRoute>
    private lateinit var docks:MutableList<Dock>
    private lateinit var startingPoint: Point
    private lateinit var destinationPoint: Point

    @Before
    fun setUp(){
        routes = mutableListOf()
        for (i in 0 until 10){
            val route = DirectionsRoute.builder()
                .distance(100.0)
                .duration(1200.0)
                .build()
            route.toBuilder().duration(route.duration()+i)
            routes.add(route)
        }

        docks = mutableListOf(
            Dock("BikePoints_85", "Tanner Street, Bermondsey", 51.500647, -0.0786, 38, 3,41),
            Dock("BikePoints_86", "Tanner Street_2, Bermondsey_2", 89.0, -1.90, 40, 2,42),
            Dock("BikePoints_87", "Tanner Street_3, Bermondsey_3", 21.90, -0.90, 39, 4,43)
        )

        startingPoint = Point.fromLngLat(0.78,88.00)
        destinationPoint = Point.fromLngLat(0.78,22.00)


    }

    @Test
    fun `test get closest docks to origin`(){
        val closestDock = docks[1]
        assert(MapInfoHelper.getClosestDocksToOrigin(docks,startingPoint,2) == closestDock)
    }

    @Test
    fun `test get closest docks to destination`(){
        val closestDock = docks[2]
        assert(MapInfoHelper.getClosestDocksToDestination(docks,destinationPoint,2) == closestDock)
    }

    @Test
    fun `test getFastestRoute`(){
        assert(MapInfoHelper.getFastestRoute(routes) == routes.first())
    }

    @Test
    fun `test retrieveJourneyDistances`(){
        assert(MapInfoHelper.retrieveJourneyDistances(routes[0]) == 100.0)
    }

    @Test
    fun `test retrieveJourneyDurations`(){
        assert(MapInfoHelper.retrieveJourneyDurations(routes[0]) == 1200.0)
    }

    @Test
    fun `test getRental if the journey is more than 30min long`(){
        val durations = routes.map { it.duration() }.toMutableList()
        assert(MapInfoHelper.getRental(durations) == 12.0)
    }

    @Test
    fun `test getRental if the journey is less than 30 min long`(){
        val durations = mutableListOf(routes.first().duration())
        assert(MapInfoHelper.getRental(durations) == 0.0)
    }



    @After
    fun tearDown(){
        routes.clear()
        docks.clear()
    }
}