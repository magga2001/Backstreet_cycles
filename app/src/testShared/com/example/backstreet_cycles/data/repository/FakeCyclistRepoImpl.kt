package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.data.repository.CyclistRepositoryImpl
import com.example.backstreet_cycles.domain.repositoryInt.CyclistRepository
import javax.inject.Inject

class FakeCyclistRepoImpl @Inject constructor(): CyclistRepository{

    private var testNumCyclists = 1

    override fun incrementNumCyclists(){
        ++testNumCyclists
    }

    override fun decrementNumCyclists(){
        --testNumCyclists
    }

    override fun resetNumCyclist() {
        testNumCyclists = 1
    }

    override fun getNumCyclists(): Int{
        return testNumCyclists
    }
}