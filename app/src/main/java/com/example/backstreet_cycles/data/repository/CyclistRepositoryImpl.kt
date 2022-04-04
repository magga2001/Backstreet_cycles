package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository

/**
 * Class responsible for implementation of the cyclist repository
 */
class CyclistRepositoryImpl : CyclistRepository {

    private var numCyclists = 1

    /**
     * Incrementing the number of bicycles users add to cycle along.
     */
    override fun incrementNumCyclists() {
        ++numCyclists
    }

    /**
     * Decrementing the number of bicycles users add to cycle along.
     */
    override fun decrementNumCyclists() {
        --numCyclists
    }

    /**
     * Resets the number of bicycles users add to cycle along to one.
     */
    override fun resetNumCyclist() {
        numCyclists = 1
    }

    /**
     * @return the number of cycles the user selects
     */
    override fun getNumCyclists(): Int {
        return numCyclists
    }
}