package com.example.backstreet_cycles.data.repository

import android.util.Log
import com.example.backstreet_cycles.data.remote.TflApi
import com.example.backstreet_cycles.data.remote.dto.DockDto
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import javax.inject.Inject

class TflRepositoryImpl @Inject constructor(
    private val tflApi: TflApi
): TflRepository {

    override suspend fun getDocks(): MutableList<DockDto> {
        return tflApi.getDocks()
    }
}