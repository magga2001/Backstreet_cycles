package com.example.backstreet_cycles.domain.repository

import android.app.Application
import com.example.backstreet_cycles.domain.model.DTO.Locations

interface LocationRepository {

    fun loadLocations(application: Application)

    fun addStop(stop: Locations)

    fun addStop(index: Int, stop: Locations)

    fun removeStop(stop: Locations)

    fun removeStopAt(index: Int)

    fun getTouristLocations(): MutableList<Locations>
}