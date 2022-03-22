package com.example.backstreet_cycles.data.repository

import android.content.Context
import android.util.Log
import com.example.backstreet_cycles.data.remote.TflApi
import com.example.backstreet_cycles.data.remote.dto.DockDto
import com.example.backstreet_cycles.domain.model.DTO.Dock
import com.example.backstreet_cycles.domain.repository.TflRepository
import com.example.backstreet_cycles.interfaces.Assests
import javax.inject.Inject

class TflRepositoryImpl @Inject constructor(
    private val tflApi: TflApi
): TflRepository {

    //    override fun getDock(context: Context, listener: Assests<MutableList<Dock>>) {
//        tflApi.getDock(context,listener)
//    }
    override suspend fun getDocks(): MutableList<DockDto> {
        Log.i("Calling dock", "Success")
        return tflApi.getDocks()
    }
}