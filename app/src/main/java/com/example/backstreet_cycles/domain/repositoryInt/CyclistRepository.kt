package com.example.backstreet_cycles.domain.repositoryInt

/**
 * Interface for the cyclist repository
 */
interface CyclistRepository {

    fun incrementNumCyclists()

    fun decrementNumCyclists()

    fun resetNumCyclist()

    fun getNumCyclists(): Int
}