package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.backstreet_cycles.model.MapHelper
import com.mapbox.navigation.core.MapboxNavigation

class NavigationViewModel(application: Application) : AndroidViewModel(application) {

    private val mapRepository: MapHelper

    init {
        mapRepository = MapHelper(application)
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

}