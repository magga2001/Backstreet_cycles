package com.example.backstreet_cycles

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.repository.TflRepositoryImpl
import com.example.backstreet_cycles.dependencyInjection.AppModule
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FakeTflRepoImpl : TflRepository {

    var testDocks = mutableListOf<Dock>()

    override suspend fun getDocks(): Flow<Resource<MutableList<Dock>>> {
        TODO("Not yet implemented")
    }

    override fun getCurrentDocks(): MutableList<Dock> {
        return testDocks
    }

    override fun setCurrentDocks(docks: MutableList<Dock>) {
        this.testDocks = docks
    }

}