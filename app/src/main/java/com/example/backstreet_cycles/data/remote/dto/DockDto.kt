package com.example.backstreet_cycles.data.remote.dto

import com.example.backstreet_cycles.domain.model.dto.Dock

data class DockDto(
    val `$type`: String,
    val additionalProperties: List<BikeStatusInfo>,
    val children: List<Any>,
    val childrenUrls: List<Any>,
    val commonName: String,
    val id: String,
    val lat: Double,
    val lon: Double,
    val placeType: String,
    val url: String
)

fun DockDto.toDock(): Dock {

    return Dock(
        id = id,
        name = commonName,
        lat = lat,
        lon = lon,
        nbBikes = checkValidity(additionalProperties.filter { it.category == "nbBikes" }
            .map { it.value }.toString()),
        nbDocks = checkValidity(additionalProperties.filter { it.category == "nbDocks" }
            .map { it.value }.toString()),
        nbSpaces = checkValidity(additionalProperties.filter { it.category == "nbSpaces" }
            .map { it.value }.toString())
    )
}

private fun validDock(nbBikes: Int, nbSpaces: Int, nbDocks: Int): Boolean {
    return (nbDocks - (nbBikes + nbSpaces) == 0)
}

private fun checkValidity(value: String): Int {
    return try {
        value.toInt()
    } catch (e: Exception) {
        // handler
        0
    }
}
