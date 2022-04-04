package com.example.backstreet_cycles.domain.repositoryInt

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the MapBox repository
 */
interface MapboxRepository {

    fun requestRoute(
        mapboxNavigation: MapboxNavigation,
        routeOptions: RouteOptions,
        info: Boolean = false,
    ): Flow<Resource<DirectionsRoute>>

    fun getJourneyCurrentRoute(): MutableList<DirectionsRoute>

    fun getJourneyDistances(): MutableList<Double>

    fun getJourneyDurations(): MutableList<Double>

    fun getJourneyLocations(): MutableList<Locations>

    fun getJourneyWayPointsLocations(): MutableList<Locations>

    fun setJourneyCurrentRoute(route: DirectionsRoute)

    fun setJourneyLocations(stops: MutableList<Locations>)

    fun setJourneyWayPointsLocations(locations: MutableList<Locations>)

    fun addJourneyDistances(distance: Double)

    fun addJourneyDuration(duration: Double)

    fun distinctJourneyLocations()

    fun clearJourneyCurrentRoute()

    fun clearJourneyDistances()

    fun clearJourneyDurations()

    fun clearJourneyLocations()

    fun clearJourneyWayPointsLocations()
}