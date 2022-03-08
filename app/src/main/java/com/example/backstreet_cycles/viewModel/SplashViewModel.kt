package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.dto.Dock
import com.example.backstreet_cycles.dto.Locations
import com.example.backstreet_cycles.model.LocationRepository

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository: LocationRepository = LocationRepository(application)
    val touristAttractionList = MutableLiveData<List<Locations>?>()
    val docksList = MutableLiveData<List<Dock>?>()

    private val isReadyMutableLiveData: MutableLiveData<Boolean>

    init{
        touristAttractionList.value = loadTouristLocations()
        docksList.value = loadDocks()
        isReadyMutableLiveData = locationRepository.getIsReadyMutableLiveData()
    }

    fun loadTouristLocations(): List<Locations> {
        return locationRepository.getTouristLocations()
    }

    fun loadDocks(): List<Dock> {
        return locationRepository.getDocks()
    }

    fun getIsReadyMutableLiveData(): LiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

}