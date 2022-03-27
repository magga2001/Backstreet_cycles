package com.example.backstreet_cycles.domain.repositoryInt

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Dock
import kotlinx.coroutines.flow.Flow

interface TflRepository {

    suspend fun getDocks(): Flow<Resource<MutableList<Dock>>>

    fun setCurrentDocks(docks: MutableList<Dock>)

    fun getCurrentDocks(): MutableList<Dock>
}