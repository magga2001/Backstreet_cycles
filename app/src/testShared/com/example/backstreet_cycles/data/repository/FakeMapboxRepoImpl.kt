package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.coroutines.flow.Flow

class FakeMapboxRepoImpl: MapboxRepository{

    private var testCurrentRoute = mutableListOf<DirectionsRoute>()
    private val testDistances = mutableListOf<Double>()
    private val testDurations = mutableListOf<Double>()
    private var testLocations = mutableListOf<Locations>()
    private val testWayPoints = mutableListOf<Locations>()

    override fun requestRoute(
        mapboxNavigation: MapboxNavigation,
        routeOptions: RouteOptions,
        info: Boolean
    ): Flow<Resource<DirectionsRoute>> {
        TODO("Not yet implemented")
    }

    override fun getJourneyCurrentRoute(): MutableList<DirectionsRoute> {
        return testCurrentRoute
    }

    override fun getJourneyDistances(): MutableList<Double> {
        return testDistances
    }

    override fun getJourneyDurations(): MutableList<Double> {
        return testDurations
    }

    override fun getJourneyLocations(): MutableList<Locations> {
        return testLocations
    }

    override fun getJourneyWayPointsLocations(): MutableList<Locations> {
        return testWayPoints
    }

    override fun setJourneyCurrentRoute(route: DirectionsRoute)
    {
        testCurrentRoute.add(route)
    }

    override fun setJourneyLocations(stops: MutableList<Locations>) {
        testLocations.addAll(stops)
    }

    override fun setJourneyWayPointsLocations(locations: MutableList<Locations>) {
        testWayPoints.addAll(locations)
    }

    override fun addJourneyDistances(distance: Double) {
        testDistances.add(distance)
    }

    override fun addJourneyDuration(duration: Double) {
        testDurations.add(duration)
    }

    override fun distinctJourneyLocations() {
        testLocations.distinct()
    }

    override fun clearJourneyCurrentRoute() {
        testCurrentRoute.clear()
    }

    override fun clearJourneyDistances() {
        testDistances.clear()
    }

    override fun clearJourneyDurations() {
        testDurations.clear()
    }

    override fun clearJourneyLocations() {
        testLocations.clear()
    }

    override fun clearJourneyWayPointsLocations() {
        testWayPoints.clear()
    }

}