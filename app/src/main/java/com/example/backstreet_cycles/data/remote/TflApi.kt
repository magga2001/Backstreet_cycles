package com.example.backstreet_cycles.data.remote

import com.example.backstreet_cycles.data.remote.dto.DockDto
import retrofit2.http.GET


/**
 * Interface for TFL API
 */
interface TflApi {

    @GET("/BikePoint/")
    suspend fun getDocks(): MutableList<DockDto>

}