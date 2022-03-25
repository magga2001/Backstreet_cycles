package com.example.backstreet_cycles.domain.utils

import com.example.backstreet_cycles.domain.model.dto.Locations
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.lang.reflect.Type

object JsonHelper {

    fun convertJSON(serializedObject: String): List<Locations> {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Locations?>?>() {}.type
        return gson.fromJson(serializedObject, type)
    }
}