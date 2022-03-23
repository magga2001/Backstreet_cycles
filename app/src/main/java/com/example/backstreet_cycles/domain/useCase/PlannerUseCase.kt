package com.example.backstreet_cycles.domain.useCase

import android.app.Application
import android.util.Log
import com.example.backstreet_cycles.data.repository.MapRepository
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.interfaces.Planner
import com.mapbox.geojson.Point

object PlannerUseCase {

    fun calcBicycleRental(application: Application, numUser: Int, plannerInterface: Planner)
    {
        val locations = MapRepository.location

        Log.i("location", MapRepository.location.toString())
        val points = mutableListOf<Point>()

        for(i in 1 until locations.size)
        {
            Log.i("Looping: ", i.toString())

            val journey = calcRoutePlanner(locations[i-1], locations[i], numUser)
            val pickUpPoint = journey["pickUpPoint"]!!
            val dropOffPoint = journey["dropOffPoint"]!!

            Log.i("pickUpPoint", pickUpPoint.toString())
            Log.i("dropOffPoint", dropOffPoint.toString())

            points.add(pickUpPoint)
            points.add(dropOffPoint)

            plannerInterface.onFetchJourney(mutableListOf(pickUpPoint,dropOffPoint))

        }

        SharedPrefHelper.initialiseSharedPref(application, "DOCKS_LOCATIONS")
        SharedPrefHelper.overrideSharedPref(points,Point::class.java)
        SharedPrefHelper.getSharedPref(Point::class.java)
    }

    fun calcRoutePlanner(
        fromLocation: Locations,
        ToLocation: Locations,
        numUser: Int
    ): HashMap<String, Point?> {

        //Starting point of the individual journey
        val startingPoint = Point.fromLngLat(fromLocation.lon, fromLocation.lat)

        //Find closest pick up dock station

        val findClosestPickUp =
            MapInfoUseCase.getClosestDocks(
                Point.fromLngLat(
                    startingPoint.longitude(),
                    startingPoint.latitude()
                ), numUser
            )
        val pickUpPoint = Point.fromLngLat(findClosestPickUp.lon, findClosestPickUp.lat)

        //Find closest drop off dock station
        val findClosestDropOff =
            MapInfoUseCase.getClosestDocks(
                Point.fromLngLat(
                    ToLocation.lon,
                    ToLocation.lat)
                , numUser
            )
        val dropOffPoint = Point.fromLngLat(findClosestDropOff.lon, findClosestDropOff.lat)

        //Destination
        val destination = Point.fromLngLat(ToLocation.lon, ToLocation.lat)

        return hashMapOf(
            "startingPoint" to startingPoint,
            "pickUpPoint" to pickUpPoint,
            "dropOffPoint" to dropOffPoint,
            "destination" to destination,
        )
    }
}