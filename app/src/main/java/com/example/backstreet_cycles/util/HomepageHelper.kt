package com.example.backstreet_cycles.util

import android.location.Location
import com.example.backstreet_cycles.Tfl
import com.example.backstreet_cycles.dto.Dock
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.location.LocationComponent
import kotlin.math.abs
import kotlin.math.pow

class HomepageHelper {

    companion object{

        private var locationComponent: LocationComponent? = null


//        fun setLocationComponent(locationComponent: LocationComponent?) {
//            this.locationComponent = locationComponent
//        }

        fun getLocationComponent(): LocationComponent? {
            return locationComponent
        }


        private fun getCurrentLocation(): Location?
        {
            //Can call lat and lon from this function
            return locationComponent!!.lastKnownLocation
        }

        private fun getRadiusDocks(radius: Double): MutableList<Dock>
        {
            val currentLat = getCurrentLocation()?.latitude as Double
            val currentLon = getCurrentLocation()?.longitude as Double

            return Tfl.docks.filter { dock ->
                val dockLat = dock.lat
                val dockLon = dock.lon
                abs(dockLat - currentLat) <= radius && abs(dockLon - currentLon) <= radius
            }.toMutableList()
        }


        private fun getClosestDocks(numberOfDock: Int, radius: Double): MutableList<Dock>
        {
            val closestDocks = getRadiusDocks(radius)

            closestDocks.sortBy {it.lat.pow(2.0) + it.lon.pow(2.0)}

            return closestDocks.subList(0, numberOfDock)
        }


    }
}