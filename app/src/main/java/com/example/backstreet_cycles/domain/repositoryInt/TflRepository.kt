package com.example.backstreet_cycles.domain.repositoryInt

import com.example.backstreet_cycles.data.remote.dto.DockDto

interface TflRepository {

//    fun getDock(context: Context, listener: Assests<MutableList<Dock>>)

    suspend fun getDocks(): MutableList<DockDto>
}