package com.example.backstreet_cycles.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.dto.Dock
import com.example.backstreet_cycles.dto.Location
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray


class TflRepository(private val application: Application) {

    private val locationType = Types.newParameterizedType(List::class.java, Location::class.java)
    private var docks = mutableListOf<Dock>()
    private val touristAttractionList = mutableListOf<Location>()
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val touristAttractionText = getTextFromResources(R.raw.touristattraction)
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

    private fun parseFile(text: String): List<Location>? {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val adapter: JsonAdapter<List<Location>> = moshi.adapter(locationType)
        return adapter.fromJson(text)
    }

    private fun addTouristLocations (text: String) {
        val attractionTouristData = parseFile(text)

        for (attraction in attractionTouristData ?: emptyList()){
            touristAttractionList.add(attraction)
            Log.i("attractions", "${attraction.name},${attraction.lat},${attraction.lon}")
        }

        Log.i("attractions", touristAttractionList.toString())

    }

    fun getTouristLocations(): MutableList<Location> {
        return touristAttractionList
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
                Log.i("ERROR", "Fail to fetch data")

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
            val nbBikes = json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(6).getInt("value")
            val nbSpaces = json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(7).getInt("value")
            val nbDocks = json.getJSONObject(i).getJSONArray("additionalProperties").getJSONObject(8).getInt("value")

            val dock = Dock(id,name,lat,lon,nbBikes,nbSpaces,nbDocks)
            docks.add(dock)
            Log.i("Dock_station $i", dock.toString())
        }

        isReadyMutableLiveData.postValue(true)
    }

    fun getDocks(): MutableList<Dock> {
        loadDocks()
        return docks
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean> {
        return isReadyMutableLiveData
    }
}
