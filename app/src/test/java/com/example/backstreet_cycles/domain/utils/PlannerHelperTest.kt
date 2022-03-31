package com.example.backstreet_cycles.domain.utils

import android.graphics.Point
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PlannerHelperTest {


    @Test
    fun test_shorten_name(){
        val longName = "Test,User,firstName,LastName"
        val shortName = ConvertHelper.shortenName(longName)
        val testList : List<String> = listOf("Test", "User", "firstName", "LastName")

        assertEquals(shortName, testList)
    }

    @Test
    fun test_convert_location_to_point(){
        val location : Locations = Locations("Bush House", 51.5131, -0.1174)
        val point = ConvertHelper.convertLocationToPoint(location)
        assertEquals(point.latitude(), location.lat, 0.0)
        assertEquals(point.longitude(), location.lon,0.0)
    }

    @Test
    fun test_convert_dock_to_locations(){
        val dock: Dock = Dock("BikePoints_85", "Tanner Street, Bermondsey", 51.500647, -0.0786, 38, 3,41)
        val location = ConvertHelper.convertDockToLocations(dock)
        assertEquals(Locations(dock.name,dock.lat,dock.lon), location)
    }

    @Test
    fun test_convert_MT_to_KM(){
        val mtList = listOf<Double>(34.56, 65.435)
        val kmResult = ConvertHelper.convertMToKm(mtList)
        val kmTestList = (mtList.sum()/1000).toInt()

        assertEquals(kmResult, kmTestList)

    }

    @Test
    fun test_Ms_to_S(){
        val MiliSecondsList = listOf<Double>(6000.00,6000.00)
        val seconds = ConvertHelper.convertMsToS(MiliSecondsList)
        val secondsTestList = (MiliSecondsList.sum()/60).toInt()

        assertEquals(seconds,secondsTestList)
    }

}