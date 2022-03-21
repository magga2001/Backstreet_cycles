package com.example.backstreet_cycles.utils

import android.app.Application
import android.util.Log
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.interfaces.PlannerInterface
import com.example.backstreet_cycles.model.MapRepository
import com.example.backstreet_cycles.service.MyApplication
import com.mapbox.geojson.Point

class PlannerHelper {

    companion object
    {
        fun calcRoutePlanner(
            fromLocation: Locations,
            ToLocation: Locations,
            numUser: Int
        ): HashMap<String, Point?> {

            //Starting point of the individual journey
            val startingPoint = Point.fromLngLat(fromLocation.lon, fromLocation.lat)

            //Find closest pick up dock station

            val findClosestPickUp =
                MapHelper.getClosestDocks(
                    Point.fromLngLat(
                        startingPoint.longitude(),
                        startingPoint.latitude()
                    ), numUser
            )
            val pickUpPoint = Point.fromLngLat(findClosestPickUp.lon, findClosestPickUp.lat)

            //Find closest drop off dock station
            val findClosestDropOff =
                MapHelper.getClosestDocks(
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

        fun calcBicycleRental(application: Application, numUser: Int, plannerInterface: PlannerInterface)
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

            SharedPrefHelper.initialiseSharedPref(application,"DOCKS_LOCATIONS")
            SharedPrefHelper.overrideSharedPref(points)
            SharedPrefHelper.getSharedPref()
        }
    }
}