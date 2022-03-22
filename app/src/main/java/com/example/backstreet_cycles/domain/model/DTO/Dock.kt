package com.example.backstreet_cycles.domain.model.DTO

import com.example.backstreet_cycles.data.remote.dto.BikeStatusInfo

data class Dock(
    val id : String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val nbBikes: Int = 0,
    val nbSpaces: Int = 0,
    val nbDocks: Int = 0
)


