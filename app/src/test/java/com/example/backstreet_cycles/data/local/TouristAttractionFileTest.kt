package com.example.backstreet_cycles.data.local

import com.example.backstreet_cycles.common.BackstreetApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = BackstreetApplication::class, manifest = Config.NONE)
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
        assert(TouristAttractionFile.getTouristLocations().size == 2)
    }

    @Test
    fun `test if the locations are bing retrieved`(){
        assert(TouristAttractionFile.getTouristLocations().isEmpty())
    }



}