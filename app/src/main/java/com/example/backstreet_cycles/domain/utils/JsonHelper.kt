package com.example.backstreet_cycles.domain.utils

import android.app.Application
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

object JsonHelper {

    fun getJsonFromResources(application: Application, resourceId: Int): String {
        return application.resources.openRawResource(resourceId).use { it ->
            it.bufferedReader().use {
                it.readText()
            }
        }
    }

    fun writeJsonToResources(application: Application, json: String, resourceId: Int) {
        val writeFile : String = application.resources.getResourceName(resourceId)
        File(writeFile).writeText(json)
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
}