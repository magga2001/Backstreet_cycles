package com.example.backstreet_cycles.DTO

data class Maneuver(
    val instruction: String,
    val type: String,
    val modifier: String = "",
    val distance: Int
)