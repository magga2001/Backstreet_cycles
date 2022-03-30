package com.example.backstreet_cycles.data.repository

import android.app.Application
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.data.local.TouristAttractionFile
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.repositoryInt.LocationRepository
import com.mapbox.api.directions.v5.models.DirectionsRoute
import javax.inject.Inject

open class LocationRepositoryImpl @Inject constructor(
    private val touristAttractionFile: TouristAttractionFile
) : LocationRepository {

    /**
     * Loads all the tourist attractions in the application
     *
     * @param application - The Application
     */
    override fun loadLocations(application: Application) {
        touristAttractionFile.loadLocations(application)
    }

    /**
     * @return a mutable list of the locations of teh tourist attractions
     */
    override fun getTouristLocations(): MutableList<Locations> {
        return touristAttractionFile.getTouristLocations()
    }

}