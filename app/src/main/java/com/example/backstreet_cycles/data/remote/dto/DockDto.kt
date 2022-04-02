package com.example.backstreet_cycles.data.remote.dto

import android.util.Log
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
        nbBikes = checkValidity(additionalProperties.filter { it.key == "NbBikes" }
            .map { it.value }.first().toString()),
        nbDocks = checkValidity(additionalProperties.filter { it.key == "NbDocks" }
            .map { it.value }.first().toString()),
        nbSpaces = checkValidity(additionalProperties.filter { it.key == "NbEmptyDocks" }
            .map { it.value }.first().toString())
    )
}

/**
 * @param value - a string that contains the value fetched from TFL JSon file
 * @return an Int - the value itself in an integer format or zero if it is not applicable
 */
private fun checkValidity(value: String): Int {
    return try {
        value.toInt()
    } catch (e: Exception) {
        // handler
        0
    }
}
