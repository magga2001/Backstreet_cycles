package com.example.backstreet_cycles.domain.utils

import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.google.common.collect.ImmutableList
import com.mapbox.geojson.Point
import junit.framework.Assert.assertTrue
import org.junit.Test

class ConvertHelperTest {

    @Test
    fun `test whether you can split the name of the location`(){
        val location = "Harrods, YC16 AJ5, Harrow Road"
        assert(ConvertHelper.shortenName(location).size == 3)
    }

    @Test
    fun `test whether you can convert Location to Point`(){
        val location = Locations("Harrods", -13.2,13.4)
        val point = Point.fromLngLat(location.lon, location.lat)
        assert(ConvertHelper.convertLocationToPoint(location).longitude() == point.longitude())
        assert(ConvertHelper.convertLocationToPoint(location).latitude() == point.latitude())

    }

    @Test
    fun `test whether you can convert Dock to Locations`(){
        val dock = Dock("1","Dock_A",12.0,13.0,4,5,2)
        val location = Locations("Dock_A", 12.0,13.0)
        assert(ConvertHelper.convertDockToLocations(dock).lat == location.lat)
        assert(ConvertHelper.convertDockToLocations(dock).lon == location.lon)

    }

    @Test
    fun `test if you convert meters to km`(){
        val distances = listOf(1000.0, 1000.0)
        assert(ConvertHelper.convertMToKm(distances) == 2)
    }


    @Test
    fun `test if you convert milliseconds to seconds`(){
        val durations = listOf(60.0, 60.0)
        assert(ConvertHelper.convertMsToS(durations) == 2)
    }


    @Test
    fun test_get_loaction_names(){


        val locationList = listOf(
            Locations("St Paul’s Cathedral", 51.5138, -0.0984),
            Locations("Tower of London",51.5081, -0.0759),
            Locations("Tate Modern",51.5076, -0.0994 )
        )

        val result = ConvertHelper.getLocationNames(locationList)

        val expectedResult = listOf(
            "St Paul’s Cathedral",
            "Tower of London",
            "Tate Modern"
        )

        assertTrue(
                result.size == expectedResult.size
                && result.containsAll(expectedResult)
                && expectedResult.containsAll(result)
        )

    }

}
