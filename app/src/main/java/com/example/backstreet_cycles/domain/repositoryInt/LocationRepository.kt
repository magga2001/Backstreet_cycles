package com.example.backstreet_cycles.domain.repositoryInt

import android.app.Application
import com.example.backstreet_cycles.domain.model.dto.Locations

interface LocationRepository {

    fun loadLocations(application: Application)

    fun getTouristLocations(): MutableList<Locations>
}