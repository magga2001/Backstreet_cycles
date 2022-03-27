package com.example.backstreet_cycles.domain.utils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsonHelper {


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