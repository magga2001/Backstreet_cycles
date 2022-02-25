package com.example.backstreet_cycles.dto

data class Maneuver(
    val instruction: String,
    val type: String,
    val modifier: String = ""
)
