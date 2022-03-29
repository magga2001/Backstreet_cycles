package com.example.backstreet_cycles.domain.useCase

import android.app.Application
import android.util.Log
import com.example.backstreet_cycles.common.BackstreetApplication
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.PlannerHelper
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.interfaces.Planner
import com.mapbox.geojson.Point

object PlannerUseCase {

    fun calcBicycleRental(
        application: Application,
        docks: MutableList<Dock>,
        locations: MutableList<Locations>,
        numCyclists: Int,
        plannerInterface: Planner
    ) {
        val points = mutableListOf<Point>()

        for (i in 1 until locations.size) {
            Log.i("Looping: ", i.toString())

            val journey = calcRoutePlanner(docks, locations[i - 1], locations[i], numCyclists)
            val pickUpPoint = journey["pickUpPoint"]!!
            val dropOffPoint = journey["dropOffPoint"]!!

            Log.i("pickUpPoint", pickUpPoint.toString())
            Log.i("dropOffPoint", dropOffPoint.toString())

            points.add(PlannerHelper.convertLocationToPoint(pickUpPoint))
            points.add(PlannerHelper.convertLocationToPoint(dropOffPoint))

            plannerInterface.onFetchJourney(mutableListOf(pickUpPoint, dropOffPoint))
        }

        SharedPrefHelper.initialiseSharedPref(application, Constants.DOCKS_LOCATIONS)
        SharedPrefHelper.overrideSharedPref(points, Point::class.java)
        SharedPrefHelper.getSharedPref(Point::class.java)
    }

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
            MapInfoUseCase.getClosestDocksToOrigin(
                docks,
                Point.fromLngLat(
                    startingPoint.longitude(),
                    startingPoint.latitude()
                ), numUser
            )
        val pickUpDock = PlannerHelper.convertDockToLocations(findClosestPickUp)
        //val pickUpPoint = Point.fromLngLat(findClosestPickUp.lon, findClosestPickUp.lat)

        //Find closest drop off dock station
        val findClosestDropOff =
            MapInfoUseCase.getClosestDocksToDestination(
                docks,
                Point.fromLngLat(
                    ToLocation.lon,
                    ToLocation.lat
                ), numUser
            )
        val dropOffDock = PlannerHelper.convertDockToLocations(findClosestDropOff)
        //val dropOffPoint = Point.fromLngLat(findClosestDropOff.lon, findClosestDropOff.lat)

        //Destination
        //val destination = Point.fromLngLat(ToLocation.lon, ToLocation.lat)

        return hashMapOf(
            "startingPoint" to fromLocation,
            "pickUpPoint" to pickUpDock,
            "dropOffPoint" to dropOffDock,
            "destination" to ToLocation,
        )
    }
}