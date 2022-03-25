package com.example.backstreet_cycles.data.local

import android.app.Application
import android.util.Log
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber

object TouristAttractionFile {

    private val locationType = Types.newParameterizedType(List::class.java, Locations::class.java)
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

    private fun parseFile(text: String): List<Locations>? {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val adapter: JsonAdapter<List<Locations>> = moshi.adapter(locationType)
        return adapter.fromJson(text)
    }

    private fun addTouristLocations (text: String) {
        val attractionTouristData = parseFile(text)

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