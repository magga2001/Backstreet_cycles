package com.example.backstreet_cycles.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.backstreet_cycles.R
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

class SharedPrefHelper {

    companion object
    {
        private lateinit var sharedPref: SharedPreferences
        private lateinit var key:String

        fun initialiseSharedPref(application: Application, key: String)
        {
            setKey(application, key)

            sharedPref = application.getSharedPreferences(
                key, Context.MODE_PRIVATE)
        }

//        fun checkIfSharedPrefEmpty():Boolean {
//            if (getSharedPref().isEmpty()){
//                return true
//            }
//            return false
//        }

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

//        fun getSharedPref(): List<Any> {
//            val serializedObject: String? =
//                sharedPref.getString(key, null)
//            return if (serializedObject != null) {
//                val gson = Gson()
//                val type: Type = object : TypeToken<List<Any?>?>() {}.getType()
//                gson.fromJson(serializedObject, type)
//            } else {
//                emptyList()
//            }
//        }

        fun setKey(application: Application,key:String)
        {
            sharedPref = application.getSharedPreferences(
                key, Context.MODE_PRIVATE)
        }
    }
}