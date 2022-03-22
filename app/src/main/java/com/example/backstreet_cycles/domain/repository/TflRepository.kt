package com.example.backstreet_cycles.domain.repository

import android.content.Context
import com.example.backstreet_cycles.data.remote.dto.DockDto
import com.example.backstreet_cycles.domain.model.DTO.Dock
import com.example.backstreet_cycles.interfaces.Assests

interface TflRepository {

//    fun getDock(context: Context, listener: Assests<MutableList<Dock>>)

    suspend fun getDocks(): MutableList<DockDto>
}