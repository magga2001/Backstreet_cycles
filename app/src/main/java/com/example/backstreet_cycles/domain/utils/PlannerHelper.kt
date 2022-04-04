package com.example.backstreet_cycles.domain.utils

import android.app.Application
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.interfaces.Planner
import com.mapbox.geojson.Point

/**
 * Helper class for planning journey
 */
object PlannerHelper {

    /**
     * Calculation engine for bike rental price
     */
    fun calcBicycleRental(
        application: Application,
        docks: MutableList<Dock>,
        locations: MutableList<Locations>,
        numCyclists: Int,
        plannerInterface: Planner
    ) {
        val points = mutableListOf<Point>()

        for (i in 1 until locations.size) {
            val journey = calcRoutePlanner(docks, locations[i - 1], locations[i], numCyclists)
            val pickUpPoint = journey["pickUpPoint"]!!
            val dropOffPoint = journey["dropOffPoint"]!!

            points.add(ConvertHelper.convertLocationToPoint(pickUpPoint))
            points.add(ConvertHelper.convertLocationToPoint(dropOffPoint))

            plannerInterface.onFetchJourney(mutableListOf(pickUpPoint, dropOffPoint))
        }

        SharedPrefHelper.initialiseSharedPref(application, Constants.DOCKS_LOCATIONS)
        SharedPrefHelper.overrideSharedPref(points, Point::class.java)
        SharedPrefHelper.getSharedPref(Point::class.java)
    }

    /**
     * Calculate the most optimal route
     */
    fun calcRoutePlanner(
        docks: MutableList<Dock>,
        fromLocation: Locations,
        ToLocation: Locations,
        numUser: Int
    ): HashMap<String, Locations?> {

        //Starting point of the individual journey
        val startingPoint = Point.fromLngLat(fromLocation.lon, fromLocation.lat)

        //Find closest pick up dock station

        val findClosestPickUp =
            MapInfoHelper.getClosestDocksToOrigin(
                docks,
                Point.fromLngLat(
                    startingPoint.longitude(),
                    startingPoint.latitude()
                ), numUser
            )
        val pickUpDock = ConvertHelper.convertDockToLocations(findClosestPickUp)

        //Find closest drop off dock station
        val findClosestDropOff =
            MapInfoHelper.getClosestDocksToDestination(
                docks,
                Point.fromLngLat(
                    ToLocation.lon,
                    ToLocation.lat
                ), numUser
            )
        val dropOffDock = ConvertHelper.convertDockToLocations(findClosestDropOff)

        return hashMapOf(
            "startingPoint" to fromLocation,
            "pickUpPoint" to pickUpDock,
            "dropOffPoint" to dropOffDock,
            "destination" to ToLocation,
        )
    }
}