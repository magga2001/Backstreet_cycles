package com.example.backstreet_cycles.domain.repositoryInt

interface CyclistRepository {

    fun incrementNumCyclists()

    fun decrementNumCyclists()

    fun resetNumCyclist()

    fun getNumCyclists(): Int
}