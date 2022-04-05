package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.domain.model.dto.Locations
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigator.RouterCallback
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapboxRepositoryImplTest{

    private lateinit var mapboxRepositoryImpl: MapboxRepositoryImpl

    // The current route of the journey
    private var currentRoute = mutableListOf<DirectionsRoute>()
    // The distance of the journey
    private val distances = mutableListOf<Double>()
    // The duration of the journey
    private val durations = mutableListOf<Double>()
    // A location that the user chooses in its journey
    private var locations = mutableListOf<Locations>()
    // The way-points that get the user to the location in the journey
    private val wayPoints = mutableListOf<Locations>()

    @Mock
    private lateinit var mapboxNavigation: MapboxNavigation

    @Mock
    private lateinit var routeOptions: RouteOptions

    @Mock
    private lateinit var routerCallback: RouterCallback


    @Before
    fun setUp(){
        mapboxRepositoryImpl = MapboxRepositoryImpl()
    }

    @Test
    fun `test if the journey current route is being returned`(){

        for (i in 0 until 10){
            currentRoute.add(DirectionsRoute.builder()
                .distance(100.0)
                .duration(20.0)
                .build())
            mapboxRepositoryImpl.setJourneyCurrentRoute(currentRoute[i])
        }
        assert(mapboxRepositoryImpl.getJourneyCurrentRoute() == currentRoute)
    }

    @Test
   fun `test if the journey Distance can be retrieved`(){
        val distance = 20.0
        mapboxRepositoryImpl.addJourneyDistances(distance)
        assert(mapboxRepositoryImpl.getJourneyDistances().first() == distance)
   }

    @Test
    fun `test if the journey Durations can be retrieved`(){
        val duration = 20.0
        mapboxRepositoryImpl.addJourneyDuration(duration)
        assert(mapboxRepositoryImpl.getJourneyDurations().first() == duration)
    }

    @Test
    fun `test if the journey Locations can be retrieved`(){
        val location = Locations("TestLocation",12.5,13.5)
        mapboxRepositoryImpl.setJourneyLocations(mutableListOf(location))
        assert(mapboxRepositoryImpl.getJourneyLocations().first() == location)
    }

    @Test
    fun `test if the journey WayPoints can be retrieved`(){

        val wayPoints = mutableListOf(Locations("TestLocation",12.5,13.5),
            Locations("TestLocation_2",13.5,12.5))
        mapboxRepositoryImpl.setJourneyWayPointsLocations(wayPoints)
        assert(mapboxRepositoryImpl.getJourneyWayPointsLocations() == wayPoints)
    }

    @Test
    fun `test if the journey Locations are distinct`(){

        val locations = mutableListOf(Locations("TestLocation",12.5,13.5),
            Locations("TestLocation",12.5,13.5))
        mapboxRepositoryImpl.setJourneyLocations(locations)
        mapboxRepositoryImpl.distinctJourneyLocations()
        assert(mapboxRepositoryImpl.getJourneyWayPointsLocations().size < locations.size)
    }

    @Test
    fun `test if the journey can be cleared`() {
        val route = DirectionsRoute.builder()
            .distance(100.0)
            .duration(20.0)
            .build()
        for (i in 0 until 10){
            currentRoute.add(route)
            mapboxRepositoryImpl.setJourneyCurrentRoute(currentRoute[i])
        }
        mapboxRepositoryImpl.clearJourneyCurrentRoute()
        assert(mapboxRepositoryImpl.getJourneyCurrentRoute().isEmpty())
    }

    @Test
    fun `test if the journey Distance can be cleared`(){
        mutableListOf(20.0,30.0,90.0).forEach {
            mapboxRepositoryImpl.addJourneyDistances(it)
        }
        mapboxRepositoryImpl.clearJourneyDistances()
        assert(mapboxRepositoryImpl.getJourneyDistances().isEmpty())
    }

    @Test
    fun `test if the journey Duration can be cleared`(){
        mutableListOf(20.0,30.0,90.0).forEach {
            mapboxRepositoryImpl.addJourneyDuration(it)
        }
        mapboxRepositoryImpl.clearJourneyDurations()
        assert(mapboxRepositoryImpl.getJourneyDurations().isEmpty())
    }

    @Test
    fun `test if the journey Locations can be cleared`(){
        val locations = mutableListOf(Locations("TestLocation",12.5,13.5),
            Locations("TestLocation_2",13.5,12.5))
        mapboxRepositoryImpl.setJourneyLocations(locations)

        mapboxRepositoryImpl.clearJourneyLocations()
        assert(mapboxRepositoryImpl.getJourneyLocations().isEmpty())
    }

    @Test
    fun `test if the journey WayPoints can be cleared`(){
        val wayPoints = mutableListOf(Locations("TestLocation",12.5,13.5),
            Locations("TestLocation_2",13.5,12.5))
        mapboxRepositoryImpl.setJourneyWayPointsLocations(wayPoints)
        mapboxRepositoryImpl.clearJourneyWayPointsLocations()
        assert(mapboxRepositoryImpl.getJourneyWayPointsLocations().isEmpty())
    }

    @After
    fun tearDown(){
        currentRoute.clear()
        distances.clear()
        locations.clear()
        durations.clear()
        wayPoints.clear()
    }
}