package com.example.backstreet_cycles.data.repository

import android.app.Application
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.data.local.TouristAttractionFile
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.mapbox.api.directions.v5.models.DirectionsRoute
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val touristAttractionFile: TouristAttractionFile
) : LocationRepository {

    override fun loadLocations(application: Application) {
        touristAttractionFile.loadLocations(application)
    }

    override fun getTouristLocations(): MutableList<Locations> {
        return touristAttractionFile.getTouristLocations()
    }

}