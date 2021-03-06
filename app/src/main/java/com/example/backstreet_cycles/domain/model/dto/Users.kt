package com.example.backstreet_cycles.domain.model.dto

/**
 * Data class for the Users object
 */
data class Users(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var journeyHistory: MutableList<String> = emptyList<String>().toMutableList()
)
