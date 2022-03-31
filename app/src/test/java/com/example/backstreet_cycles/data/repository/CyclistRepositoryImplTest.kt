package com.example.backstreet_cycles.data.repository

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.properties.Delegates

@RunWith(JUnit4::class)
class CyclistRepositoryImplTest{

    private var numCyclists by Delegates.notNull<Int>()
    private lateinit var cyclistRepositoryImpl: CyclistRepositoryImpl

    @Before
    fun setUp(){
        numCyclists = 1
        cyclistRepositoryImpl = CyclistRepositoryImpl()
    }

    @Test
    fun `test if the number of cyclists decrements`(){
        cyclistRepositoryImpl.decrementNumCyclists()
        assert(cyclistRepositoryImpl.getNumCyclists() == numCyclists-1)
    }

    @Test
    fun `test if the number of cyclists increments`(){
        cyclistRepositoryImpl.incrementNumCyclists()
        assert(cyclistRepositoryImpl.getNumCyclists() == numCyclists+1)
    }

    @Test
    fun `test if the number of cyclists reset`() {
        cyclistRepositoryImpl.resetNumCyclist()
        assert(cyclistRepositoryImpl.getNumCyclists() == numCyclists)
    }

    @Test
    fun `test if the number of cyclists can be retrieved`() {
        assert(cyclistRepositoryImpl.getNumCyclists() == numCyclists)
    }

}