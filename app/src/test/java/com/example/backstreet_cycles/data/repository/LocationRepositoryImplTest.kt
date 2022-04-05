package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.data.local.TouristAttractionFile
import org.junit.Before
import org.junit.Test


class LocationRepositoryImplTest{

    private lateinit var locationRepositoryImpl: LocationRepositoryImpl

    @Before
    fun setUp(){
        locationRepositoryImpl = LocationRepositoryImpl(TouristAttractionFile)
    }


    @Test
    fun `test if the touristLocations are being retrieved`() {
        assert(locationRepositoryImpl.getTouristLocations() == TouristAttractionFile.getTouristLocations())
    }

}