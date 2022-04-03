package com.example.backstreet_cycles.domain.utils
//
//import com.example.backstreet_cycles.domain.model.dto.Dock
//import com.example.backstreet_cycles.domain.model.dto.Locations
//import com.mapbox.geojson.Point
//import org.junit.Test
//
//class ConvertHelperTest {
//
//    @Test
//    fun `test whether you can split the name of the location`(){
//        val location = "Harrods, YC16 AJ5, Harrow Road"
//        assert(ConvertHelper.shortenName(location).size == 3)
//    }
//
//    @Test
//    fun `test whether you can convert Location to Point`(){
//        val location = Locations("Harrods", -13.2,13.4)
//        val point = Point.fromLngLat(location.lon, location.lat)
//        assert(ConvertHelper.convertLocationToPoint(location).longitude() == point.longitude())
//        assert(ConvertHelper.convertLocationToPoint(location).latitude() == point.latitude())
//
//    }
//
//    @Test
//    fun `test whether you can convert Dock to Locations`(){
//        val dock = Dock("1","Dock_A",12.0,13.0,4,5,2)
//        val location = Locations("Dock_A", 12.0,13.0)
//        assert(ConvertHelper.convertDockToLocations(dock).lat == location.lat)
//        assert(ConvertHelper.convertDockToLocations(dock).lon == location.lon)
//
//    }
//
//    @Test
//    fun `test if you convert meters to km`(){
//        val distances = listOf(1000.0, 1000.0)
//        assert(ConvertHelper.convertMToKm(distances) == 2)
//    }
//
//
//    @Test
//    fun `test if you convert milliseconds to seconds`(){
//        val durations = listOf(60.0, 60.0)
//        assert(ConvertHelper.convertMsToS(durations) == 2)
//    }
//
//}
