package com.example.backstreet_cycles.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.R
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray
import timber.log.Timber


class LocationRepository(private val application: Application) {

    companion object
    {
        var docks = mutableListOf<Dock>()
    }

    private val locationType = Types.newParameterizedType(List::class.java, Locations::class.java)
    private var stops: MutableList<Locations>
    private val touristAttractions = mutableListOf<Locations>()
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val touristAttractionText = getTextFromResources(R.raw.touristattraction)
        stops = mutableListOf()
        addTouristLocations(touristAttractionText)
        isReadyMutableLiveData.value = false
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

    private fun loadDocks()
    {
        isReadyMutableLiveData.postValue(false)
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(application)
        val url = application.getString(R.string.tfl_url)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val json = JSONArray(response)
                addDocks(json)
            },
            {
                Timber.tag("ERROR").w("Fail to fetch data")
                isReadyMutableLiveData.postValue(false)
            })

        queue.add(stringRequest)
    }

    private fun addDocks(json: JSONArray)
    {
        for(i in 0 until json.length())
        {
            val id = json.getJSONObject(i).getString("id")
            val name = json.getJSONObject(i).getString("commonName")
            val lat = json.getJSONObject(i).getDouble("lat")
            val lon = json.getJSONObject(i).getDouble("lon")
            val nbBikes = checkValidity(json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(6).getString("value"))
            val nbSpaces = checkValidity(json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(7).getString("value"))
            val nbDocks = checkValidity(json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(8).getString("value"))

            //Check if dock is broken
            if(validDock(nbBikes,nbSpaces,nbDocks))
            {
                val dock = Dock(id,name,lat,lon,nbBikes,nbSpaces,nbDocks)
                docks.add(dock)
                Timber.tag("Dock_station $i").w(dock.toString())
            }
        }
        isReadyMutableLiveData.postValue(true)
    }

    fun getDocks(): MutableList<Dock> {
        docks.clear()
        loadDocks()
        return docks
    }

    private fun validDock(nbBikes: Int, nbSpaces: Int, nbDocks: Int): Boolean
    {
        return (nbDocks - (nbBikes + nbSpaces) == 0)
    }

    private fun checkValidity(value : String): Int
    {
        return try {
            value.toInt()
        } catch (e: Exception) {
            // handler
            0
        }
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean> {
        return isReadyMutableLiveData
    }
}
