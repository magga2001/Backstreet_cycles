package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository

class CyclistRepositoryImpl: CyclistRepository{

    private var numCyclists = 1

    override fun incrementNumCyclists(){
        ++numCyclists
    }

    override fun decrementNumCyclists(){
        --numCyclists
    }

    override fun resetNumCyclist() {
        numCyclists = 1
    }

    override fun getNumCyclists(): Int{
        return numCyclists
    }
}