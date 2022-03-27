package com.example.backstreet_cycles.domain.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPrefHelper {

    companion object
    {
        private lateinit var sharedPref: SharedPreferences
        private lateinit var key:String
        private lateinit var application: Application

        fun initialiseSharedPref(application: Application, key: String)
        {
            this.application = application
            this.key = key

            sharedPref = application.getSharedPreferences(
                key, Context.MODE_PRIVATE)
        }

        fun checkIfSharedPrefEmpty(key: String):Boolean {
            val serializedObject: String? = sharedPref.getString(key, null)
            sharedPref.contains(key)
            if (serializedObject == null){
                return true
            }
            return false
        }

        fun <T> overrideSharedPref(values: MutableList<T>, type: Class<T>) {
            val json = JsonHelper.objectToString(values, type)
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

        fun <T> getSharedPref(type: Class<T>): MutableList<T> {
            val serializedObject: String? =
                sharedPref.getString(key, null)
            Log.i("serializedObject", serializedObject.toString())
            return if (serializedObject != null) {
                JsonHelper.stringToObject(serializedObject, type)!!.toMutableList()
            } else {
                emptyList<T>().toMutableList()
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