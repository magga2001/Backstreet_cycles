package com.example.backstreet_cycles.data.local

import android.app.Application
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.JsonHelper

object TouristAttractionFile {

    private val touristAttractions = mutableListOf<Locations>()

    /**
     * Loads all the tourist attractions in the application
     *
     * @param application - The Application
     */
    fun loadLocations(application: Application) {
        val touristAttractionText =
            JsonHelper.getJsonFromResources(application, R.raw.touristattraction)
        addTouristLocations(touristAttractionText)
    }

    /**
     * For adding the name and details of the tourist attraction into the list of tourist attractions
     *
     * @param text - a String that holds the name of the tourist attraction
     */
    fun addTouristLocations(text: String) {
        val attractionTouristData = JsonHelper.stringToObject(text, Locations::class.java)
        for (attraction in attractionTouristData ?: emptyList()) {
            touristAttractions.add(attraction)
        }
    }

    /**
     * @return a mutable list of the tourist attractions
     */
    fun getTouristLocations(): MutableList<Locations> {
        return touristAttractions
    }
}