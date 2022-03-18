package com.example.backstreet_cycles.dto


data class Users(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var journeyHistory: MutableList<String> = emptyList<String>().toMutableList()
)
