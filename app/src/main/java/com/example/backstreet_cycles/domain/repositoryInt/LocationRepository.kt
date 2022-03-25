package com.example.backstreet_cycles.domain.repositoryInt

import android.app.Application
import com.example.backstreet_cycles.common.CallbackResource
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.mapbox.api.directions.v5.models.DirectionsRoute

interface LocationRepository {

    fun loadLocations(application: Application)

    fun getTouristLocations(): MutableList<Locations>
}