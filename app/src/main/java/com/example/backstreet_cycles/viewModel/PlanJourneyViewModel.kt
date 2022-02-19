package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.backstreet_cycles.model.MapHelper
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation

class PlanJourneyViewModel(application: Application) : AndroidViewModel(application) {

    private val mapRepository: MapHelper

    init {
        mapRepository = MapHelper(application)
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

    fun checkPermission(context: Context, activity: Activity)
    {
        mapRepository.checkPermission(context, activity)
    }

    fun fetchRoute(context: Context, mapboxNavigation: MapboxNavigation, wayPoints: List<Point>) {

        mapRepository.fetchRoute(context,mapboxNavigation, wayPoints)
    }

}