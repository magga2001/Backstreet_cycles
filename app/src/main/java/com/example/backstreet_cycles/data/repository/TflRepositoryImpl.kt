package com.example.backstreet_cycles.data.repository

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.remote.TflApi
import com.example.backstreet_cycles.data.remote.dto.toDock
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Class responsible for implementation of the TFL repository
 */
class TflRepositoryImpl @Inject constructor(
    private val tflApi: TflApi
) : TflRepository {

    var docks = mutableListOf<Dock>()

    /**
     * Receive the dock from the TFL API and maps them to the dataclass
     */
    override suspend fun getDocks(): Flow<Resource<MutableList<Dock>>> = channelFlow {
        try {
            val docks = tflApi.getDocks()
                .map { it.toDock() }
                .filter { (it.nbDocks - (it.nbBikes + it.nbSpaces) == 0) }
                .toMutableList()
            send(Resource.Success(docks))
        } catch (e: HttpException) {
            send(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            send(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    /**
     * Sets the dock to be live
     *
     * @param docks - a list of docks
     */
    override fun setCurrentDocks(docks: MutableList<Dock>) {
        this.docks = docks
    }

    /**
     * @return the list of docks
     */
    override fun getCurrentDocks(): MutableList<Dock> {
        return docks
    }
}