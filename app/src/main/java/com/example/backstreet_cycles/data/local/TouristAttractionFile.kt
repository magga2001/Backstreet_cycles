package com.example.backstreet_cycles.data.local

import android.app.Application
import android.util.Log
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.JsonHelper
import timber.log.Timber

object TouristAttractionFile {

    private val touristAttractions = mutableListOf<Locations>()

    fun loadLocations(application: Application)
    {
        Log.i("Loading Tourist...", "success")
        val touristAttractionText = getTextFromResources(application, R.raw.touristattraction)
        addTouristLocations(touristAttractionText)
    }

    private fun getTextFromResources(application: Application, resourceId: Int): String{
        return application.resources.openRawResource(resourceId).use { it ->
            it.bufferedReader().use {
                it.readText()
            }
        }
    }

    private fun addTouristLocations (text: String) {
        val attractionTouristData = JsonHelper.stringToObject(text, Locations::class.java)

        for (attraction in attractionTouristData ?: emptyList()){
            touristAttractions.add(attraction)
            Timber.tag("attractions").i("${attraction.name},${attraction.lat},${attraction.lon}")
        }
        Timber.tag("attractions").i(touristAttractions.toString())
    }

    fun getTouristLocations(): MutableList<Locations> {
        return touristAttractions
    }
}