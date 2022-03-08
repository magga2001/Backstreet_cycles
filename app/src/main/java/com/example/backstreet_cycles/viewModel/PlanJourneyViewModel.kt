package com.example.backstreet_cycles.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.model.JourneyRepository
import com.mapbox.geojson.Point
import com.mapbox.navigation.core.MapboxNavigation

class PlanJourneyViewModel(application: Application) : AndroidViewModel(application) {

    private val mapRepository: JourneyRepository
    private val isReadyMutableLiveData: MutableLiveData<Boolean>


    init {
        mapRepository = JourneyRepository(application)
        isReadyMutableLiveData = mapRepository.getIsReadyMutableLiveData()

    }

    fun initialiseMapboxNavigation(): MapboxNavigation
    {
        return mapRepository.initialiseMapboxNavigation()
    }

    fun checkPermission(context: Context, activity: Activity)
    {
        mapRepository.checkPermission(context, activity)
    }

    fun fetchRoute(context: Context, mapboxNavigation: MapboxNavigation, wayPoints: MutableList<Point>, profile: String, overview:Boolean) {

        mapRepository.fetchRoute(context,mapboxNavigation, wayPoints, profile, overview)
    }

    fun getIsReadyMutableLiveData(): MutableLiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

}