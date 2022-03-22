package com.example.backstreet_cycles.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.R
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type
import com.mapbox.geojson.Point

class SharedPrefHelper {

    companion object
    {
        private lateinit var sharedPref: SharedPreferences
        private lateinit var key:String
        private lateinit var application: Application
        private val pointType = Types.newParameterizedType(List::class.java, Point::class.java)

        fun initialiseSharedPref(application: Application, key: String)
        {
//            setKey(application, key)
            this.application = application
            this.key = key
            sharedPref = application.getSharedPreferences(
                key, Context.MODE_PRIVATE)
        }

        fun checkIfSharedPrefEmpty(key: String):Boolean {
            val serializedObject: String? =
                sharedPref.getString(key, null)
            return serializedObject?.isEmpty()!!
        }

        fun <T> overrideSharedPref(values: MutableList<T>) {
            val gson = Gson();
            val json = gson.toJson(values);
            with (sharedPref.edit()) {
                putString(key, json)
                apply()
            }
        }

        fun clearListLocations() {
            with (sharedPref.edit()) {
                clear()
                apply()
            }
        }

        fun getSharedPref(): List<Point>? {
            val serializedObject: String? =
                sharedPref.getString(key, null)
            Log.i("serializedObject", serializedObject.toString())
            return if (serializedObject != null) {
//                val gson = Gson()
//                val type: Type = object : TypeToken<List<T>?>() {}.type
//                gson.fromJson(serializedObject, type)
                val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
                val adapter: JsonAdapter<List<Point>> = moshi.adapter(pointType)
                //Log.i("adapter", adapter.toString())
                Log.i("adapter", adapter.fromJson(serializedObject).toString())
                adapter.fromJson(serializedObject)
            } else {
                emptyList()
            }
        }

        fun changeSharedPref(key:String)
        {
            this.key = key
            sharedPref = application.getSharedPreferences(
                key, Context.MODE_PRIVATE)
        }
    }
}