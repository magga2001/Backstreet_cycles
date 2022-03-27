package com.example.backstreet_cycles.data.repository

import android.content.Context
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.remote.MapboxApi
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MapboxRepositoryImpl@Inject constructor(
    private val mapboxApi: MapboxApi
): MapboxRepository {

    private var currentRoute = mutableListOf<DirectionsRoute>()
    private val distances = mutableListOf<Double>()
    private val durations = mutableListOf<Double>()
    private var locations = mutableListOf<Locations>()
    private val wayPoints = mutableListOf<Locations>()

    override fun requestRoute(mapboxNavigation: MapboxNavigation, routeOptions : RouteOptions, info: Boolean): Flow<DirectionsRoute> = callbackFlow {

        val callBack = object : CallbackResource<DirectionsRoute> {
            override fun getResult(objects: DirectionsRoute) {
                trySend(objects)
            }
        }

        mapboxApi.requestRoute(mapboxNavigation, routeOptions, info, callBack, mapboxRepository = this@MapboxRepositoryImpl)

        awaitClose { channel.close() }
    }

    override fun getJourneyCurrentRoute(): MutableList<DirectionsRoute> {
        return currentRoute
    }

    override fun getJourneyDistances(): MutableList<Double> {
        return distances
    }

    override fun getJourneyDurations(): MutableList<Double> {
        return durations
    }

    override fun getJourneyLocations(): MutableList<Locations> {
        return locations
    }

    override fun getJourneyWayPointsLocations(): MutableList<Locations> {
        return wayPoints
    }

    override fun setJourneyCurrentRoute(route: DirectionsRoute)
    {
        currentRoute.add(route)
    }

    override fun setJourneyLocations(stops: MutableList<Locations>) {
        locations.addAll(stops)
    }

    override fun setJourneyWayPointsLocations(locations: MutableList<Locations>) {
        wayPoints.addAll(locations)
    }

    override fun addJourneyDistances(distance: Double) {
        distances.add(distance)
    }

    override fun addJourneyDuration(duration: Double) {
        durations.add(duration)
    }

    override fun distinctJourneyLocations() {
        locations.distinct()
    }

    override fun clearJourneyCurrentRoute() {
        currentRoute.clear()
    }

    override fun clearJourneyDistances() {
        distances.clear()
    }

    override fun clearJourneyDurations() {
        durations.clear()
    }

    override fun clearJourneyLocations() {
        locations.clear()
    }

    override fun clearJourneyWayPointsLocations() {
        wayPoints.clear()
    }
}