package com.example.backstreet_cycles.data.remote.dto


/**
 * Data class for holding status of the bike fetched from Json array of objects
 */
data class BikeStatusInfo(
    val `$type`: String,
    val category: String,
    val key: String,
    val modified: String,
    val sourceSystemKey: String,
    val value: String
)