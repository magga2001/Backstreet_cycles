package com.example.backstreet_cycles.dto

data class Distance (
    val location1: Location? = null,
    val location2: Location? = null,
    val distance: Double = 0.0
)