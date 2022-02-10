package com.example.backstreet_cycles

import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.backstreet_cycles.data.Dock
import org.json.JSONArray

class Tfl{

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
                    seedDocks(json)
                },
                {
                    Toast.makeText(App.context,it.message.toString(),Toast.LENGTH_LONG).show()
                    Log.i("ERROR", "Fail to fetch data")

                isLoaded=true
                })


            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }

        private fun seedDocks(json: JSONArray)
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

            isLoaded=true
        }
    }
}