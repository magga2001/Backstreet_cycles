package com.example.backstreet_cycles.data.repository

import android.app.Application
import com.example.backstreet_cycles.data.local.TouristAttractionFile
import com.example.backstreet_cycles.domain.model.DTO.Locations
import com.example.backstreet_cycles.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val touristAttractionFile: TouristAttractionFile
): LocationRepository{

    override fun loadLocations(application: Application) {
        touristAttractionFile.loadLocations(application)
    }

    override fun addStop(stop: Locations) {
        touristAttractionFile.addStop(stop)
    }

    override fun addStop(index: Int, stop: Locations) {
        touristAttractionFile.addStop(index, stop)
    }

    override fun removeStop(stop: Locations) {
        touristAttractionFile.removeStop(stop)
    }

    override fun removeStopAt(index: Int) {
        touristAttractionFile.removeStopAt(index)
    }

    override fun getTouristLocations(): MutableList<Locations> {
        return touristAttractionFile.getTouristLocations()
    }


}