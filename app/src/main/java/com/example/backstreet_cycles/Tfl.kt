package com.example.backstreet_cycles

import android.util.Log
import com.example.backstreet_cycles.data.Dock
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Tfl {

    private val listType = Types.newParameterizedType(
        List::class.java, Dock::class.java
    )

    fun parseJSON (url: String){
        val moshi = Moshi.Builder().build()
        val adapter: JsonAdapter<List<Dock>> = moshi.adapter(listType)
        val dockData = adapter.fromJson("@string/tfl_url")

        for (dock in dockData?: emptyList()){
            Log.i("Rany", "${dock.lat}")
        }
    }

//    fun getDockFromJSON(): Dock{
//
//    }

}