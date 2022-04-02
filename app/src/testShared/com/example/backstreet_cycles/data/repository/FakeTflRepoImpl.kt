package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.remote.dto.BikeStatusInfo
import com.example.backstreet_cycles.data.remote.dto.DockDto
import com.example.backstreet_cycles.data.repository.TflRepositoryImpl
import com.example.backstreet_cycles.dependencyInjection.AppModule
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeTflRepoImpl : TflRepository {

    private val dock1 = Dock("Dock1", "Dock1",51.500647,-0.0786,10,10,20)
    private val dock2 = Dock("Dock2", "Dock2",51.489479,-0.115156,8,12,20)
    private val dock3 = Dock("Dock3", "Dock3",51.516468,-0.079684,12,8,20)
    private val dock4 = Dock("Dock4", "Dock4",51.518587,-0.132053,15,5,20)
    private val dock5 = Dock("Dock5", "Dock5",51.52625,-0.123509,2,18,20)
    private val dock6 = Dock("Dock6", "Dock6",51.533019,-0.139174,5,15,20)
    private val dock7 = Dock("Dock7", "Dock7",51.493686,-0.111014,14,6,20)
    private val dock8 = Dock("Dock8", "Dock8",51.498898,-0.10044,9,11,20)
    private val dock9 = Dock("Dock9", "Dock9",51.534408,-0.109025,7,13,20)
    private val dock10 = Dock("Dock10", "Dock10",51.495061,-0.085814,6,14,20)

    private var isConnect = true
    private var docks: MutableList<Dock> = mutableListOf()

    override suspend fun getDocks(): Flow<Resource<MutableList<Dock>>> = flow{
        if(isConnect){
            loadDocks()
            emit(Resource.Success(docks))
        }
        else {
            emit(Resource.Error("Couldn't connect to server"))
        }
    }

    fun loadDocks(){
        docks.add(dock1)
        docks.add(dock2)
        docks.add(dock3)
        docks.add(dock4)
        docks.add(dock5)
        docks.add(dock6)
        docks.add(dock7)
        docks.add(dock8)
        docks.add(dock9)
        docks.add(dock10)
    }

    override fun getCurrentDocks(): MutableList<Dock> {
        return docks
    }

    override fun setCurrentDocks(docks: MutableList<Dock>) {
        this.docks = docks
    }

    fun setConnection(connection : Boolean){
        isConnect = connection
    }

}