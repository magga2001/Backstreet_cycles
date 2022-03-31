package com.example.backstreet_cycles

import android.app.Application
import com.example.backstreet_cycles.data.local.TouristAttractionFile
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository

class FakeLocationRepoImpl : LocationRepository{

    override fun loadLocations(application: Application) {
        TouristAttractionFile.loadLocations(application)
    }

    override fun getTouristLocations(): MutableList<Locations> {
        return TouristAttractionFile.getTouristLocations()
    }

}