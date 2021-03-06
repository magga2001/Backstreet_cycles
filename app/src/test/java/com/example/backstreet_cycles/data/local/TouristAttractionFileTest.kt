package com.example.backstreet_cycles.data.local

import org.junit.Before
import org.junit.Test


class TouristAttractionFileTest{

//    @Test
//    fun `test if the locations can be loaded`(){
//        TouristAttractionFile.loadLocations(application)
//        assert(TouristAttractionFile.getTouristLocations().isNotEmpty())
//    }

    private lateinit var string: String

    @Before
    fun setUp(){
        string = "[{\n" +
                "    \"name\": \"Tower of London\",\n" +
                "    \"lat\": \"51.5081\",\n" +
                "    \"lon\": \"-0.0759\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"St Paul’s Cathedral\",\n" +
                "    \"lat\": \"51.5138\",\n" +
                "    \"lon\": \"-0.0984\"\n" +
                "  }]"
    }

    @Test
    fun `test if the tourist locations are being added`(){
        TouristAttractionFile.addTouristLocations(string)
        print(TouristAttractionFile.getTouristLocations()[0].toString())
        print("Test")
        val test = TouristAttractionFile.getTouristLocations()
        assert(test.size == 2)
    }

    @Test
    fun `test if the locations are being retrieved`(){
        assert(TouristAttractionFile.getTouristLocations().isEmpty())
    }



}