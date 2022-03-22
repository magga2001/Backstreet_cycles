package com.example.backstreet_cycles.ui.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.DTO.Locations
import com.example.backstreet_cycles.data.repository.LocationRepository

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository: LocationRepository = LocationRepository(application)
    private val isReadyMutableLiveData: MutableLiveData<Boolean> = locationRepository.getIsReadyMutableLiveData()

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