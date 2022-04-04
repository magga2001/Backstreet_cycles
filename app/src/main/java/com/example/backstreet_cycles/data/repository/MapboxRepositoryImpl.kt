package com.example.backstreet_cycles.data.repository

import android.util.Log
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.remote.MapboxApi
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.MapboxRepository
import com.example.backstreet_cycles.domain.utils.MapInfoHelper
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.navigation.base.route.RouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MapboxRepositoryImpl @Inject constructor(
    private val mapboxApi: MapboxApi
) : MapboxRepository {

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

    /**
     * Requests the route of the journey from mapbox api.
     *
     * @param mapboxNavigation - An object of MapboxNavigation
     * @param routeOptions - An object of Route Options that holds different options
     * @param info - a boolean
     */
    override fun requestRoute(
        mapboxNavigation: MapboxNavigation,
        routeOptions: RouteOptions,
        info: Boolean
    ): Flow<Resource<DirectionsRoute>> = callbackFlow {

        mapboxNavigation.requestRoutes(
            routeOptions,
            object : RouterCallback {
                /**
                 * The callback is triggered when the routes are ready to be displayed.
                 */
                override fun onRoutesReady(
                    routes: List<DirectionsRoute>,
                    routerOrigin: RouterOrigin
                ) {

                    val fastestRoute = MapInfoHelper.getFastestRoute(routes)

                    if (info) {
                        val distance = MapInfoHelper.retrieveJourneyDistances(fastestRoute)
                        val duration = MapInfoHelper.retrieveJourneyDurations(fastestRoute)

                        addJourneyDistances(distance)
                        addJourneyDuration(duration)
                    } else {
                        setJourneyCurrentRoute(fastestRoute)
                    }

                    trySend(Resource.Success(fastestRoute))
                }

                /**
                 * The callback is triggered if the request to fetch a route was canceled.
                 */
                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                    trySend(Resource.Error("Request to fetch a route was canceled"))
                }

                /**
                 * The callback is triggered if the request to fetch a route failed for any reason.
                 */
                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    trySend(Resource.Error("Fail to fetch the route"))
                }
            }
        )

        awaitClose { channel.close() }
    }

    /**
     * @return a mutable list of direction routes.
     */
    override fun getJourneyCurrentRoute(): MutableList<DirectionsRoute> {
        return currentRoute
    }

    /**
     * @return a mutable list of doubles - the distance of the two locations
     */
    override fun getJourneyDistances(): MutableList<Double> {
        return distances
    }

    /**
     * @return a mutable list of doubles - the duration of the two locations
     */
    override fun getJourneyDurations(): MutableList<Double> {
        return durations
    }

    /**
     * @return a mutable list of locations selected on the map
     */
    override fun getJourneyLocations(): MutableList<Locations> {
        return locations
    }

    /**
     * @return a mutable list of locations selected on the map
     */
    override fun getJourneyWayPointsLocations(): MutableList<Locations> {
        return wayPoints
    }

    /**
     * @param route - a direction route that will be added to the current route list
     */
    override fun setJourneyCurrentRoute(route: DirectionsRoute) {
        currentRoute.add(route)
    }

    /**
     * Adds a list of stops to the locations list that the user has chosen
     *
     * @param stops - another mutable list of location
     */
    override fun setJourneyLocations(stops: MutableList<Locations>) {
        locations.addAll(stops)
    }

    /**
     * Adds a list of locations that lead to the waypoint
     *
     * @param locations - a mutable list of location
     */
    override fun setJourneyWayPointsLocations(locations: MutableList<Locations>) {
        wayPoints.addAll(locations)
    }

    /**
     * @param distance - a double(distance) that will be added to the current route list
     */
    override fun addJourneyDistances(distance: Double) {
        distances.add(distance)
    }

    /**
     * @param duration - a double(duration) that will be added to the duration list
     */
    override fun addJourneyDuration(duration: Double) {
        durations.add(duration)
    }

    /**
     * Removes duplicates from the locations list
     */
    override fun distinctJourneyLocations() {
        locations.distinct()
    }

    /**
     * Clears the current route selected
     */
    override fun clearJourneyCurrentRoute() {
        currentRoute.clear()
    }

    /**
     * Clears the distances between the locations
     */
    override fun clearJourneyDistances() {
        distances.clear()
    }

    /**
     * Clears the durations between the locations
     */
    override fun clearJourneyDurations() {
        durations.clear()
    }

    /**
     * Clears the locations
     */
    override fun clearJourneyLocations() {
        locations.clear()
    }

    /**
     * Clears the the list of wayPoints
     */
    override fun clearJourneyWayPointsLocations() {
        wayPoints.clear()
    }
}