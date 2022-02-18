package com.example.backstreet_cycles

import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.backstreet_cycles.dto.Dock
import org.json.JSONArray

class Api{

    companion object
    {
        val docks = mutableListOf<Dock>()
        var isLoaded:Boolean=false

        fun getDock()
        {
            isLoaded=false
            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(App.context)
            val url = App.context.getString(R.string.tfl_url)

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->

                    val json = JSONArray(response)
                    fetchDocks(json)
                },
                {
                    Toast.makeText(App.context,it.message.toString(),Toast.LENGTH_LONG).show()
                    Log.i("ERROR", "Fail to fetch data")

                isLoaded=true
                })


            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }

        private fun fetchDocks(json: JSONArray)
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
                    Log.i("Dock_station $i", dock.toString())
                }
            }

            isLoaded=true
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
}