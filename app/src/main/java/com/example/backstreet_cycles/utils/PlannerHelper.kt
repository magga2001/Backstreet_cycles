package com.example.backstreet_cycles.utils

import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.interfaces.PlannerInterface
import com.example.backstreet_cycles.model.MapRepository
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

        fun calcBicycleRental(numUser: Int, plannerInterface: PlannerInterface)
        {
            val locations = MapRepository.location

            for(i in 0..locations.size - 2)
            {
                val journey = calcRoutePlanner(locations[i], locations[i+1], numUser)
                val pickUpPoint = journey["pickUpPoint"]!!
                val dropOffPoint = journey["dropOffPoint"]!!

                plannerInterface.onFetchJourney(mutableListOf(pickUpPoint,dropOffPoint))
            }
        }
    }
}