package com.example.backstreet_cycles.data.remote

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.interfaces.Assests
import org.json.JSONArray
import kotlin.concurrent.thread

object TflHelper {

    var docks = mutableListOf<Dock>()

    fun getDock(context: Context, listener: Assests<MutableList<Dock>>)
    {
        thread(start = true)
        {
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(context)
            val url = context.getString(R.string.tfl_url)

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->

                    val json = JSONArray(response)
                    docks = fetchDocks(json)
                    listener.getResult(docks)
                    Log.i("Getting Dock", "Success")
                },
                {
                    Toast.makeText(context,it.message.toString(), Toast.LENGTH_LONG).show()
                    Log.i("ERROR", "Fail to fetch data")
                })


            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }
    }

    private fun fetchDocks(json: JSONArray): MutableList<Dock>
    {
        val docks = mutableListOf<Dock>()

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
            }
        }

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

}