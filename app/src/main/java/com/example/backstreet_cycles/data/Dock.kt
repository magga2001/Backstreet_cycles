package com.example.backstreet_cycles.data

import com.google.gson.annotations.SerializedName

data class Dock(
    val id : String,
    val name: String,
    val lat: Double,
    val lon: Double,
    val nbBikes: Int,
    val nbSpaces: Int,
    val nbDocks: Int
)
