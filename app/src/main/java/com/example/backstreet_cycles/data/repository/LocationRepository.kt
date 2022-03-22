package com.example.backstreet_cycles.data.repository

import android.app.Application
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber


class LocationRepository(private val application: Application) {

    private val locationType = Types.newParameterizedType(List::class.java, Locations::class.java)
    private var stops: MutableList<Locations>
    private val touristAttractions = mutableListOf<Locations>()

    init {
        val touristAttractionText = getTextFromResources(R.raw.touristattraction)
        stops = mutableListOf()
        addTouristLocations(touristAttractionText)
    }

    private fun getTextFromResources(resourceId: Int): String{
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

    fun addStop(stop: Locations){
        stops.add(stop)
    }

    fun addStop(index: Int, stop: Locations){
        stops.add(index, stop)
    }

    fun removeStop(stop: Locations){
        stops.remove(stop)
    }

    fun removeStopAt(index: Int){
        stops.removeAt(index)
    }


    fun getStops(): MutableList<Locations> {
        return stops
    }
}
