package com.example.backstreet_cycles.domain.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SharedPrefHelper {

    companion object
    {
        private lateinit var sharedPref: SharedPreferences
        private lateinit var key:String
        private lateinit var application: Application
//        private lateinit var parameterizedType: ParameterizedType

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

        fun <T> overrideSharedPref(values: MutableList<T>, type: Class<T>) {
            val json = objectToString(values, type)
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

        fun <T> getSharedPref(type: Class<T>): List<T>? {
            val serializedObject: String? =
                sharedPref.getString(key, null)
            Log.i("serializedObject", serializedObject.toString())
            return if (serializedObject != null) {
                  stringToObject(serializedObject, type)
            } else {
                emptyList()
            }
        }

        fun <T> stringToObject(text: String,  type: Class<T>): List<T>? {
            val parameterizedType = Types.newParameterizedType(List::class.java,type)
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter: JsonAdapter<List<T>> = moshi.adapter(parameterizedType)
            return adapter.fromJson(text)
        }

        fun <T> objectToString(values: List<T>,  type: Class<T>): String{
            val parameterizedType = Types.newParameterizedType(List::class.java,type)
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter: JsonAdapter<List<T>> = moshi.adapter(parameterizedType)
            return adapter.toJson(values)
        }

        fun changeSharedPref(key:String)
        {
            this.key = key
            sharedPref = application.getSharedPreferences(
                key, Context.MODE_PRIVATE)
        }
    }
}