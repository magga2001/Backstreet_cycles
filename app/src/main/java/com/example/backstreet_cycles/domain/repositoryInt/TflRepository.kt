package com.example.backstreet_cycles.domain.repositoryInt

import com.example.backstreet_cycles.data.remote.dto.DockDto

interface TflRepository {

    suspend fun getDocks(): MutableList<DockDto>
}