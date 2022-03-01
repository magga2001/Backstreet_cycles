package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.backstreet_cycles.model.JourneyHelper
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation

class PlanJourneyViewModel(application: Application) : AndroidViewModel(application) {

    private val mapRepository: JourneyHelper

    init {
        mapRepository = JourneyHelper(application)
    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

    fun checkPermission(context: Context, activity: Activity)
    {
        mapRepository.checkPermission(context, activity)
    }

    fun fetchRoute(context: Context, mapboxNavigation: MapboxNavigation, wayPoints: MutableList<Point>) {

        mapRepository.fetchRoute(context,mapboxNavigation, wayPoints)
    }

}