package com.example.backstreet_cycles.data

data class Dock(
    val id : String,
    val lat: Float,
    val lon: Float,
    val nbBikes: Int,
    val nbSpaces: Int
)
