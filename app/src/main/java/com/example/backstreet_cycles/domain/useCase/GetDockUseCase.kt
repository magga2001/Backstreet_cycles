package com.example.backstreet_cycles.domain.useCase

import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.data.remote.dto.toDock
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetDockUseCase @Inject constructor(
    private val tflRepository: TflRepository
) {
    operator fun invoke(): Flow<Resource<MutableList<Dock>>> = flow {
        try {
            emit(Resource.Loading())
            val docks = tflRepository.getDocks()
                .map { it.toDock() }
                .filter { (it.nbDocks - (it.nbBikes + it.nbSpaces) == 0) }
                .toMutableList()
            emit(Resource.Success(docks))
        } catch(e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch(e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}