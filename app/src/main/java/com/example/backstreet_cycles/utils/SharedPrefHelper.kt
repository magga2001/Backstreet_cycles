package com.example.backstreet_cycles.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.backstreet_cycles.R
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

class SharedPrefHelper(private val application: Application, key:String) {

    private var sharedPref: SharedPreferences
    private var key:String

    init {
        sharedPref = application.getSharedPreferences(
            key, Context.MODE_PRIVATE)
        this.key = key
    }

    fun checkIfSharedPrefEmpty():Boolean {
        if (getSharedPref().isEmpty()){
            return true
        }
        return false
    }

    fun overrideSharedPref(values: Any) {
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

    fun getSharedPref(): List<Any> {
        val values: List<Any>
        val serializedObject: String? =
            sharedPref.getString(key, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<List<Any?>?>() {}.getType()
            values = gson.fromJson<List<Any>>(serializedObject, type)
        } else {
            values = emptyList()
        }
        return values
    }

}