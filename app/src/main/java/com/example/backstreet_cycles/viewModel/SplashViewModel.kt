package com.example.backstreet_cycles.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.backstreet_cycles.dto.Dock
import com.example.backstreet_cycles.dto.Location
import com.example.backstreet_cycles.model.TflRepository

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val tflRepository: TflRepository = TflRepository(application)
    val touristAttractionList = MutableLiveData<List<Location>?>()
    val docksList = MutableLiveData<List<Dock>?>()

    private val isReadyMutableLiveData: MutableLiveData<Boolean>

    init{
        touristAttractionList.value = loadTouristLocations()
        docksList.value = loadDocks()
        isReadyMutableLiveData = tflRepository.getIsReadyMutableLiveData()
    }

    fun loadTouristLocations(): List<Location> {
        return tflRepository.getTouristLocations()
    }

    fun loadDocks(): List<Dock> {
        return tflRepository.getDocks()
    }

    fun getIsReadyMutableLiveData(): LiveData<Boolean>
    {
        return isReadyMutableLiveData
    }

}