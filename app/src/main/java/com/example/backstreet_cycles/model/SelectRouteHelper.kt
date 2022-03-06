package com.example.backstreet_cycles.model

import android.util.Log
import com.example.backstreet_cycles.dto.Distance
import com.example.backstreet_cycles.dto.Location
import kotlin.math.*

class SelectRouteHelper {

    val fakeLocation1 = Location("Tower of London",51.5081,-0.0759)
    val fakeLocation2 = Location("St Paul’s Cathedral",51.5138,-0.0984)
    val fakeLocation3 = Location("Trafalgar Square",51.5080,-0.1281)
    val fakeLocation4 = Location("Buckingham Palace",51.5014,-0.1419)

    val userLocations : List<Location> = listOf(fakeLocation1, fakeLocation2, fakeLocation3, fakeLocation4)
    var userDistance : MutableList<Distance> = mutableListOf()

    fun findDistanceBetweenLocations(location1: Location, location2: Location): Double {
        val radius = 6371
        val phi1 = location1.lat * Math.PI / 180
        val phi2 = location2.lat * Math.PI / 180
        val deltaPhi = (location2.lat - location1.lat) * Math.PI / 180
        val deltaLambda = (location2.lon - location1.lon) * Math.PI / 180

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radius * c
    }

    fun createDistanceList (){

        for (i in userLocations.indices) {
            addToDistanceList(i)
            Log.i("DistanceList", userDistance.toString())
        }
    }

    private fun addToDistanceList(locationIndex: Int) {

        for (i in locationIndex until userLocations.size) {
            if (userLocations[locationIndex] != userLocations[i]) {
                userDistance.add(
                    Distance(
                        userLocations[locationIndex],
                        userLocations[i],
                        findDistanceBetweenLocations(userLocations[locationIndex], userLocations[i])
                    )
                )
            }
        }
    }

}