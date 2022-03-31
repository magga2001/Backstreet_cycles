package com.example.backstreet_cycles.data.repository

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.backstreet_cycles.data.local.TouristAttractionFile
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class )
class LocationRepositoryImplTest{

    private lateinit var locationRepositoryImpl: LocationRepositoryImpl
    private lateinit var application: Application

    @Before
    fun setUp(){
        locationRepositoryImpl = LocationRepositoryImpl(TouristAttractionFile)
        application = ApplicationProvider.getApplicationContext()
    }

//    @Test
//    fun `test if the tourist attraction file is being loaded`(){
//        locationRepositoryImpl.loadLocations(application)
//    }

    @Test
    fun `test if the touristLocations are being retrieved`() {
        assert(locationRepositoryImpl.getTouristLocations() == TouristAttractionFile.getTouristLocations())
    }
}